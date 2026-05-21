/*******************************************************************************
 * Project Name:    Ketchup - A movie management system
 * Course:          COMP1020 - OOP and Data Structure
 * Semester:        Spring 2026
 * Members: Tran Phan Anh <25anh.tp@vinuni.edu.vn>,
 *          Nguyen Trong Khoi Nguyen <25nguyen.ntk@vinuni.edu.vn>,
 *          Nguyen Dinh Quy <25quy.nd@vinuni.edu.vn>,
 *          Hoang Duc Phat <25phat.hd@vinuni.edu.vn>,
 *          Mai Dinh Vinh <25vinh.md@vinuni.edu.vn>
 * <p>
 * Developer:       Tran Phan Anh*, Nguyen Trong Khoi Nguyen*, Nguyen Dinh Quy*,
 *                  Mai Dinh Vinh* (* equal contributions)
 * File Name:       MovieRepository.java
 * Description:     In-memory repository for all movie screening records,
 *                  backed by the MySQL `movies` and `booking_seats` tables.
 *                  Replaces the previous CSV-based implementation; all reads
 *                  and writes now go directly to the database via JDBC.
 *******************************************************************************/

package com.ducksabervn.projects.ketchup.backend.repositories;

import com.ducksabervn.projects.ketchup.backend.database.DatabaseService;
import com.ducksabervn.projects.ketchup.backend.model.Movie;

import java.sql.*;
import java.util.*;

/**
 * Static in-memory repository that holds all {@link Movie} screening records.
 * The in-memory map is loaded once at startup via {@link #loadMovies()} and
 * kept in sync with the {@code movies} table for every write operation.
 *
 * <p>Occupied seats per movie are derived from the {@code booking_seats} and
 * {@code bookings} tables — replacing the denormalised {@code SEAT} column
 * that was previously written back to {@code MOVIES.csv} on save/logout.
 */
public class MovieRepository {

    /**
     * In-memory movie store, keyed by movie ID.
     * Loaded once at startup; kept in sync for every mutating operation.
     */
    private static LinkedHashMap<String, Movie> movies = new LinkedHashMap<>();

    /**
     * Loads all rows from the {@code movies} table into the in-memory map,
     * including the current occupied seats per movie derived from
     * {@code bookings} and {@code booking_seats}.
     *
     * Must be called once at application startup, replacing the previous
     * {@code MovieCsvIO.getIO().readCsvFile()} call in {@code KetchupMain}.
     *
     * @throws SQLException if the database cannot be queried
     */
    public static void loadMovies() throws SQLException {
        LinkedHashMap<String, Movie> result = new LinkedHashMap<>();

        /*
         * Single query: join movies with booking_seats (via bookings) and
         * aggregate occupied seat IDs into a comma-separated string so the
         * existing Movie(String, ..., String occupiedSeat, ...) constructor
         * can be reused without modification.
         */
        String sql = """
                SELECT m.movie_id,
                       m.title,
                       m.genre,
                       m.duration,
                       m.rating,
                       DATE_FORMAT(m.showtime, '%Y-%m-%d %H:%i') AS showtime,
                       m.seat_price,
                       GROUP_CONCAT(bs.seat_id ORDER BY bs.seat_id SEPARATOR ',') AS occupied_seats
                FROM movies m
                LEFT JOIN bookings     b  ON b.movie_id    = m.movie_id
                LEFT JOIN booking_seats bs ON bs.booking_id = b.booking_id
                GROUP BY m.movie_id, m.title, m.genre, m.duration,
                         m.rating, m.showtime, m.seat_price
                """;

        try (PreparedStatement ps = DatabaseService.getConnection().prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                String occupiedSeats = rs.getString("occupied_seats");
                Movie m = new Movie(
                        rs.getString("movie_id"),
                        rs.getString("title"),
                        rs.getString("genre"),
                        rs.getInt("duration"),
                        rs.getString("rating"),
                        rs.getString("showtime"),
                        occupiedSeats == null ? "" : occupiedSeats,
                        rs.getInt("seat_price")
                );
                result.put(m.getMovieId(), m);
            }
        }
        movies = result;
    }

    /**
     * Returns the entire in-memory movie map.
     *
     * @return a {@link LinkedHashMap} mapping movie ID → {@link Movie}
     */
    public static LinkedHashMap<String, Movie> getMovies() {
        return movies;
    }

    /**
     * Creates a new {@link Movie}, inserts it into the {@code movies} table,
     * and adds it to the in-memory map. The movie ID is generated as a UUID.
     *
     * @param title        the title of the movie
     * @param genre        the genre of the movie
     * @param duration     the runtime in minutes
     * @param rating       the age rating (e.g. {@code "PG-13"})
     * @param showTime     the showtime string in {@code yyyy-MM-dd HH:mm} format
     * @param occupiedSeat comma-separated already-booked seat IDs, or {@code ""}
     * @param seatPrice    the price per seat in USD
     * @return the newly created {@link Movie} instance
     * @throws SQLException if the INSERT fails
     */
    public static Movie addMovie(String title,
                                 String genre,
                                 int duration,
                                 String rating,
                                 String showTime,
                                 String occupiedSeat,
                                 int seatPrice) throws SQLException {
        String movieId = UUID.randomUUID().toString();
        Movie m = new Movie(movieId, title, genre, duration, rating, showTime, occupiedSeat, seatPrice);

        String sql = """
                INSERT INTO movies (movie_id, title, genre, duration, rating, showtime, seat_price)
                VALUES (?, ?, ?, ?, ?, STR_TO_DATE(?, '%Y-%m-%d %H:%i'), ?)
                """;

        try (PreparedStatement ps = DatabaseService.getConnection().prepareStatement(sql)) {
            ps.setString(1, movieId);
            ps.setString(2, title);
            ps.setString(3, genre);
            ps.setInt(4, duration);
            ps.setString(5, rating);
            ps.setString(6, showTime);
            ps.setInt(7, seatPrice);
            ps.executeUpdate();
        }

        movies.put(movieId, m);
        return m;
    }

    /**
     * Replaces the movie record in both the {@code movies} table and the
     * in-memory map with the supplied edited {@link Movie}. Occupied seats
     * (stored in {@code booking_seats}) are not affected by this operation.
     *
     * @param id     the movie ID of the record to update
     * @param edited the updated {@link Movie} object to store
     * @throws SQLException if the UPDATE fails
     */
    public static void editMovie(String id, Movie edited) throws SQLException {
        String sql = """
                UPDATE movies
                SET title    = ?,
                    genre    = ?,
                    duration = ?,
                    rating   = ?,
                    showtime = STR_TO_DATE(?, '%Y-%m-%d %H:%i'),
                    seat_price = ?
                WHERE movie_id = ?
                """;

        try (PreparedStatement ps = DatabaseService.getConnection().prepareStatement(sql)) {
            ps.setString(1, edited.getTitle());
            ps.setString(2, edited.getGenre());
            ps.setInt(3, edited.getDuration());
            ps.setString(4, edited.getRating());
            ps.setString(5, edited.getShowTime().format(Movie.getDatetimeFormat()));
            ps.setInt(6, edited.getSeatPrice());
            ps.setString(7, id);
            ps.executeUpdate();
        }

        movies.put(id, edited);
    }

    /**
     * Removes the movie with the given ID from both the {@code movies} table
     * and the in-memory map. Because {@code bookings} has a foreign key
     * referencing {@code movies}, any associated booking records (and their
     * {@code booking_seats} rows) must be deleted first — or the schema must
     * define {@code ON DELETE CASCADE} on those foreign keys.
     *
     * @param id the movie ID of the record to delete
     * @throws SQLException if the DELETE fails
     */
    public static void deleteMovie(String id) throws SQLException {
        String sql = "DELETE FROM movies WHERE movie_id = ?";

        try (PreparedStatement ps = DatabaseService.getConnection().prepareStatement(sql)) {
            ps.setString(1, id);
            ps.executeUpdate();
        }

        movies.remove(id);
    }

    /**
     * Searches the in-memory movie store for records matching the given query.
     * If the query exactly matches a movie ID, that single movie is returned.
     * Otherwise, all movies whose title, genre, duration, rating, showtime, or
     * seat price contain the query are returned.
     *
     * @param information the search query string
     * @return an {@link ArrayList} of matching {@link Movie} objects; empty if
     *         no matches are found
     */
    public static ArrayList<Movie> searchMovie(String information) {
        ArrayList<Movie> result = new ArrayList<>();

        if (movies.containsKey(information)) {
            result.add(movies.get(information));
            return result;
        }

        for (Movie m : movies.values()) {
            if (m.getTitle().equals(information) ||
                    m.getGenre().equals(information) ||
                    Integer.toString(m.getDuration()).equals(information) ||
                    m.getRating().equals(information) ||
                    m.getShowTime().format(Movie.getDatetimeFormat()).equals(information) ||
                    Integer.toString(m.getSeatPrice()).equals(information)) {
                result.add(m);
            }
        }
        return result;
    }
}