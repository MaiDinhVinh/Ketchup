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
 * File Name:       BookingRepository.java
 * Description:     In-memory repository for the currently logged-in user's
 *                  booking records, backed by the MySQL `bookings` and
 *                  `booking_seats` tables. Replaces the previous CSV-based
 *                  implementation; all reads and writes now go directly to
 *                  the database via JDBC.
 *******************************************************************************/

package com.ducksabervn.projects.ketchup.backend.repositories;

import com.ducksabervn.projects.ketchup.backend.database.DatabaseService;
import com.ducksabervn.projects.ketchup.backend.model.Booking;
import com.ducksabervn.projects.ketchup.backend.model.Movie;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.*;

/**
 * Static in-memory repository that holds all {@link Booking} records for the
 * currently logged-in user. The in-memory map is loaded on login via
 * {@link #loadBookingsForUser(String)} and kept in sync with the
 * {@code bookings} and {@code booking_seats} tables for every write.
 */
public class BookingRepository {

    /**
     * In-memory booking store for the current session, keyed by booking ID.
     * Loaded per-user on login; cleared on logout.
     */
    private static LinkedHashMap<String, Booking> bookings = new LinkedHashMap<>();

    /**
     * Loads all booking records for the given user from the database into
     * the in-memory map. Each booking's seat list is assembled from the
     * corresponding {@code booking_seats} rows.
     *
     * Must be called once at login, replacing the previous
     * {@code BookingCsvIO.getIO().readCsvFile(email)} call in
     * {@code LoginUIController}.
     *
     * @param userEmail the email address of the currently logged-in user
     * @throws SQLException if the database cannot be queried
     */
    public static void loadBookingsForUser(String userEmail) throws SQLException {
        LinkedHashMap<String, Booking> result = new LinkedHashMap<>();

        /*
         * Retrieve all bookings for this user together with their seat IDs in
         * one query, then assemble the HashSet<String> per booking in Java.
         */
        String sql = """
                SELECT b.booking_id,
                       b.user_email,
                       b.movie_id,
                       DATE_FORMAT(b.showtime, '%Y-%m-%d %H:%i') AS showtime,
                       b.total_price,
                       b.is_processed,
                       bs.seat_id
                FROM bookings b
                LEFT JOIN booking_seats bs ON bs.booking_id = b.booking_id
                WHERE b.user_email = ?
                ORDER BY b.booking_id
                """;

        try (PreparedStatement ps = DatabaseService.getConnection().prepareStatement(sql)) {
            ps.setString(1, userEmail);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String bookingId = rs.getString("booking_id");

                    // Accumulate seats across multiple rows for the same booking
                    Booking existing = result.get(bookingId);
                    if (existing == null) {
                        LocalDateTime showtime = LocalDateTime.parse(
                                rs.getString("showtime"), Movie.getDatetimeFormat());
                        existing = new Booking(
                                rs.getString("user_email"),
                                bookingId,
                                rs.getString("movie_id"),
                                showtime,
                                new HashSet<>(),
                                rs.getInt("total_price"),
                                rs.getBoolean("is_processed")
                        );
                        result.put(bookingId, existing);
                    }

                    String seatId = rs.getString("seat_id");
                    if (seatId != null) {
                        existing.getChosenSeats().add(seatId);
                    }
                }
            }
        }
        bookings = result;
    }

    /**
     * Returns the entire in-memory booking map for the current session.
     *
     * @return a {@link LinkedHashMap} mapping booking ID → {@link Booking}
     */
    public static LinkedHashMap<String, Booking> getBookings() {
        return bookings;
    }

    /**
     * Creates a new booking, persists it to the {@code bookings} and
     * {@code booking_seats} tables in a single transaction, and adds it to
     * the in-memory map. Also updates the in-memory {@link Movie} record so
     * that the chosen seats are immediately reflected as occupied.
     *
     * @param email         the email address of the user making the booking
     * @param movieId       the ID of the movie being booked
     * @param showtime      the showtime string in {@code yyyy-MM-dd HH:mm} format
     * @param selectedSeats the set of seat IDs chosen by the user
     * @param totalPrice    the total price for all selected seats
     * @return the newly created and persisted {@link Booking} instance
     * @throws SQLException if the database INSERT fails
     */
    public static Booking addBooking(String email,
                                     String movieId,
                                     String showtime,
                                     HashSet<String> selectedSeats,
                                     int totalPrice) throws SQLException {
        String bookingId = UUID.randomUUID().toString();
        LocalDateTime st = LocalDateTime.parse(showtime, Movie.getDatetimeFormat());
        Booking b = new Booking(email, bookingId, movieId, st, selectedSeats, totalPrice, true);

        Connection conn = DatabaseService.getConnection();
        conn.setAutoCommit(false);

        try {
            // 1 — Insert the booking header row
            String insertBooking = """
                    INSERT INTO bookings
                        (booking_id, user_email, movie_id, showtime, total_price, is_processed)
                    VALUES (?, ?, ?, STR_TO_DATE(?, '%Y-%m-%d %H:%i'), ?, ?)
                    """;
            try (PreparedStatement ps = conn.prepareStatement(insertBooking)) {
                ps.setString(1, bookingId);
                ps.setString(2, email);
                ps.setString(3, movieId);
                ps.setString(4, showtime);
                ps.setInt(5, totalPrice);
                ps.setBoolean(6, true);
                ps.executeUpdate();
            }

            // 2 — Insert one row per chosen seat into booking_seats
            String insertSeat = "INSERT INTO booking_seats (booking_id, seat_id) VALUES (?, ?)";
            try (PreparedStatement ps = conn.prepareStatement(insertSeat)) {
                for (String seatId : selectedSeats) {
                    ps.setString(1, bookingId);
                    ps.setString(2, seatId);
                    ps.addBatch();
                }
                ps.executeBatch();
            }

            conn.commit();

        } catch (SQLException e) {
            conn.rollback();
            throw e;
        } finally {
            conn.setAutoCommit(true);
        }

        // 3 — Keep the in-memory movie record up-to-date
        Movie m = MovieRepository.getMovies().get(movieId);
        if (m != null) {
            m.getOccupiedSeat().addAll(selectedSeats);
        }

        bookings.put(bookingId, b);
        return b;
    }

    /**
     * Searches the in-memory booking store for records matching the given query.
     * If the query exactly matches a booking ID, that single booking is returned.
     * Otherwise, all bookings whose associated movie title, showtime string, or
     * total price string match the query are returned.
     *
     * @param information the search query string
     * @return an {@link ArrayList} of matching {@link Booking} objects; empty if
     *         no matches are found
     */
    public static ArrayList<Booking> searchBooking(String information) {
        ArrayList<Booking> result = new ArrayList<>();

        if (bookings.containsKey(information)) {
            result.add(bookings.get(information));
            return result;
        }

        for (Booking b : bookings.values()) {
            Movie m = MovieRepository.getMovies().get(b.getMovieId());
            String movieTitle = (m != null) ? m.getTitle() : "";
            String showtimeStr = b.getShowtime().format(Movie.getDatetimeFormat());

            if (movieTitle.equals(information) ||
                    showtimeStr.equals(information) ||
                    Integer.toString(b.getTotalPrice()).equals(information)) {
                result.add(b);
            }
        }
        return result;
    }

    /**
     * Cancels the booking with the given ID:
     * <ol>
     *   <li>Deletes all {@code booking_seats} rows for this booking.</li>
     *   <li>Deletes the {@code bookings} header row.</li>
     *   <li>Removes the cancelled seats from the in-memory
     *       {@link Movie#getOccupiedSeat()} set so the seat map reflects
     *       availability immediately without a full reload.</li>
     *   <li>Removes the {@link Booking} from the in-memory map.</li>
     * </ol>
     *
     * Both DELETE statements run inside a single transaction so the database
     * stays consistent if either fails.
     *
     * @param bookingId the UUID string of the booking to cancel
     * @throws SQLException if the database DELETEs fail
     */
    public static void cancelBooking(String bookingId) throws SQLException {
        Booking b = bookings.get(bookingId);
        if (b == null) return;

        Connection conn = DatabaseService.getConnection();
        conn.setAutoCommit(false);
        try {
            // 1 — Delete child rows first (guards against missing ON DELETE CASCADE)
            try (PreparedStatement ps = conn.prepareStatement(
                    "DELETE FROM booking_seats WHERE booking_id = ?")) {
                ps.setString(1, bookingId);
                ps.executeUpdate();
            }

            // 2 — Delete booking header
            try (PreparedStatement ps = conn.prepareStatement(
                    "DELETE FROM bookings WHERE booking_id = ?")) {
                ps.setString(1, bookingId);
                ps.executeUpdate();
            }

            conn.commit();
        } catch (SQLException e) {
            conn.rollback();
            throw e;
        } finally {
            conn.setAutoCommit(true);
        }

        // 3 — Restore occupied seats on the in-memory Movie record
        Movie m = MovieRepository.getMovies().get(b.getMovieId());
        if (m != null) {
            m.getOccupiedSeat().removeAll(b.getChosenSeats());
        }

        // 4 — Remove from the in-memory booking map
        bookings.remove(bookingId);
    }

    public static int calculateTotalPrice(HashSet<String> selectedSeatId, int ticketPrice){
        return selectedSeatId.size() * ticketPrice;
    }
}