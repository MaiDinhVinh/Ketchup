/******************************************************************************
 * Project Name:    Ketchup - A movie management system
 * Course:          COMP1020 - OOP and Data Structure
 * Semester:        Spring 2026
 * <p>
 * Members: Tran Phan Anh <25anh.tp@vinuni.edu.vn>,
 *          Nguyen The Khoi Nguyen <25nguyen.ntk@vinuni.edu.vn>,
 *          Nguyen Dinh Quy <25quy.nd@vinuni.edu.vn>,
 *          Hoang Duc Phat <25phat.hd@vinuni.edu.vn>,
 *          Mai Dinh Vinh <25vinh.md@vinuni.edu.vn>
 * <p>
 * File Name:       MovieRepository.java
 * Developer:       Tran Phan Anh*, Nguyen The Khoi Nguyen*, Nguyen Dinh Quy*,
 *                  Mai Dinh Vinh* (* equal contributions)
 * Description:     In-memory repository for all movie screening records,
 *                  providing methods to add, edit, delete, and search movies
 *                  during a session, with changes persisted to disk on logout
 *                  or exit.
 ******************************************************************************/

package com.ducksabervn.projects.ketchup.backend.repositories;

import com.ducksabervn.projects.ketchup.backend.io.MovieCsvIO;
import com.ducksabervn.projects.ketchup.backend.model.Movie;

import java.io.IOException;
import java.util.*;

/**
 * Static in-memory repository that holds all {@link Movie} screening records
 * loaded from {@code MOVIES.csv} at application startup. Uses a
 * {@link LinkedHashMap} keyed by movie ID to preserve insertion order and
 * support constant-time lookups. Any additions, edits, or deletions made
 * during the session are persisted to disk on logout or exit via
 * {@link MovieCsvIO}.
 */
public class MovieRepository {

    /**
     * The in-memory store of all movie screening records, keyed by movie ID.
     * A {@link LinkedHashMap} is used to maintain the order in which
     * movies were inserted.
     */
    private static LinkedHashMap<String, Movie> movies;

    /**
     * Returns the entire in-memory movie map.
     *
     * @return a {@link LinkedHashMap} mapping movie ID → {@link Movie}
     *         for all current screening records
     */
    public static LinkedHashMap<String, Movie> getMovies() {
        return movies;
    }

    /**
     * Replaces the current in-memory movie store with the given map.
     * Called on application startup after reading all records from
     * {@code MOVIES.csv}.
     *
     * @param movies the {@link LinkedHashMap} of movie ID → {@link Movie}
     *               to set as the active movie store
     */
    public static void setMovies(LinkedHashMap<String, Movie> movies) {
        MovieRepository.movies = movies;
    }

    /**
     * Creates a new {@link Movie} from the supplied parameters, stores it
     * in the in-memory repository, immediately appends it to {@code MOVIES.csv}
     * via {@link MovieCsvIO#writeMovieData(String)}, and returns the created
     * instance. The movie ID is automatically generated as a UUID.
     *
     * @param title        the title of the movie
     * @param genre        the genre of the movie
     * @param duration     the runtime of the movie in minutes
     * @param rating       the age rating of the movie (e.g. {@code "PG-13"})
     * @param showTime     the showtime string in {@code yyyy-MM-dd HH:mm} format
     * @param occupiedSeat a comma-separated string of already-booked seat IDs,
     *                     or an empty string if no seats are booked
     * @param seatPrice    the price in dollars per seat for this screening
     * @return the newly created and stored {@link Movie} instance
     * @throws IOException if the new movie record cannot be appended to the CSV file
     */
    public static Movie addMovie(String title,
                                 String genre,
                                 int duration,
                                 String rating,
                                 String showTime,
                                 String occupiedSeat,
                                 int seatPrice) throws IOException {
        String movieId = UUID.randomUUID().toString();
        Movie m = new Movie(movieId, title, genre, duration, rating, showTime, occupiedSeat, seatPrice);
        MovieRepository.movies.put(movieId, m);
        try {
            MovieCsvIO.getIO().writeMovieData(MovieCsvIO.generateMovieDataAsString(m));
        } catch (IOException e) {
            throw e;
        }
        return m;
    }

    /**
     * Replaces the movie record associated with the given ID with the supplied
     * edited {@link Movie} object. The existing entry in the map is overwritten
     * in place, preserving the movie's position in insertion order.
     *
     * @param id     the movie ID of the record to update
     * @param edited the updated {@link Movie} object to store
     */
    public static void editMovie(String id, Movie edited) {
        MovieRepository.movies.put(id, edited);
    }

    /**
     * Removes the movie record associated with the given ID from the
     * in-memory repository. The deletion is reflected in {@code MOVIES.csv}
     * on the next full save triggered by logout or exit.
     *
     * @param id the movie ID of the record to delete
     */
    public static void deleteMovie(String id) {
        MovieRepository.movies.remove(id);
    }

    /**
     * Searches the in-memory movie store for records matching the given
     * information string. If the string exactly matches a movie ID, that
     * single movie is returned. Otherwise, all movies whose title, genre,
     * duration, rating, showtime string, or seat price match the query
     * are returned.
     *
     * @param information the search query; may be a movie ID, title, genre,
     *                    duration in minutes, age rating, showtime in
     *                    {@code yyyy-MM-dd HH:mm} format, or seat price value
     * @return an {@link ArrayList} of {@link Movie} objects matching the
     *         search query; empty if no matches are found
     */
    public static ArrayList<Movie> searchMovie(String information) {
        ArrayList<Movie> arr = new ArrayList<>();
        if (MovieRepository.movies.containsKey(information)) {
            arr.add(MovieRepository.movies.get(information));
        } else {
            for (Movie m : MovieRepository.movies.values()) {
                if (m.getTitle().equals(information) ||
                        m.getGenre().equals(information) ||
                        Integer.toString(m.getDuration()).equals(information) ||
                        m.getRating().equals(information) ||
                        m.getShowTime().format(Movie.getDatetimeFormat()).equals(information) ||
                        Integer.toString(m.getSeatPrice()).equals(information)) {
                    arr.add(m);
                }
            }
        }
        return arr;
    }
}