/******************************************************************************
 * Project Name:    Ketchup — Movie Management System
 * Course:          COMP1020 — OOP and Data Structure
 * Semester:        Spring 2026
 *
 * Members: Tran Phan Anh <25anh.tp@vinuni.edu.vn>,
 *          Nguyen Trong Khoi Nguyen <25nguyen.ntk@vinuni.edu.vn>,
 *          Nguyen Dinh Quy <25quy.nd@vinuni.edu.vn>,
 *          Hoang Duc Phat <25phat.hd@vinuni.edu.vn>,
 *          Mai Dinh Vinh <25vinh.md@vinuni.edu.vn>
 *
 * File Name:       DatasetSeeder.java
 * Developer:       Tran Phan Anh*, Nguyen Trong Khoi Nguyen*, Nguyen Dinh Quy*,
 *                  Mai Dinh Vinh* (* equal contributions)
 * Description:     Inserts all test data (users, movies, bookings, booking seats)
 *                  into the Ketchup database on the first launch. Converted from
 *                  Dataset.sql to Java PreparedStatements to eliminate SQL
 *                  injection risk, avoid SQL file parsing, and remove the
 *                  MariaDB4j-incompatible USE statement from the original script.
 ******************************************************************************/

package com.ducksabervn.projects.ketchup.backend.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;

/**
 * Utility class that populates all Ketchup database tables with test data.
 * All methods are static; this class is not meant to be instantiated.
 *
 * <p>Called once by {@link DatabaseService#startup()} on the very first
 * launch, after {@link SchemaInitializer} has created the tables.</p>
 *
 * <p>All inserts use {@link PreparedStatement} for type safety.
 * The entire seed operation runs inside a single transaction so that a
 * partial failure leaves the database in a clean, empty state rather than
 * in an inconsistent half-seeded state.</p>
 */
final class DatasetSeeder {

    // -------------------------------------------------------------------------
    // SQL templates
    // -------------------------------------------------------------------------

    private static final String INSERT_USER = """
            INSERT INTO users (email, username, password, is_admin)
            VALUES (?, ?, ?, ?)
            """;

    private static final String INSERT_MOVIE = """
            INSERT INTO movies (movie_id, title, genre, duration, rating, showtime, seat_price)
            VALUES (?, ?, ?, ?, ?, ?, ?)
            """;

    private static final String INSERT_BOOKING = """
            INSERT INTO bookings (booking_id, user_email, movie_id, showtime, total_price, is_processed)
            VALUES (?, ?, ?, ?, ?, ?)
            """;

    private static final String INSERT_SEAT = """
            INSERT INTO booking_seats (booking_id, seat_id)
            VALUES (?, ?)
            """;

    // -------------------------------------------------------------------------
    // Constructor
    // -------------------------------------------------------------------------

    /** Private — this class is not meant to be instantiated. */
    private DatasetSeeder() {}

    // -------------------------------------------------------------------------
    // Public API
    // -------------------------------------------------------------------------

    /**
     * Inserts all test records into the database inside a single transaction.
     * If any insert fails the transaction is rolled back automatically and the
     * exception is propagated to the caller ({@link DatabaseService}).
     *
     * @param conn an open {@link Connection} to the {@code ketchup} database
     * @throws SQLException if any insert fails or the transaction cannot be
     *                      committed
     */
    static void seed(Connection conn) throws SQLException {
        boolean previousAutoCommit = conn.getAutoCommit();
        conn.setAutoCommit(false);

        try {
            seedUsers(conn);
            seedMovies(conn);
            seedBookings(conn);
            conn.commit();
        } catch (SQLException e) {
            conn.rollback();
            throw e;
        } finally {
            conn.setAutoCommit(previousAutoCommit);
        }
    }

    // -------------------------------------------------------------------------
    // Private seeders
    // -------------------------------------------------------------------------

    /** Inserts the single admin test user (converted from Dataset.sql). */
    private static void seedUsers(Connection conn) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(INSERT_USER)) {
            // email,   username,  password,  is_admin
            insertUser(ps, "a@a.com", "a", "a", true);
        }
    }

    /** Inserts all 14 test movies across 5 titles (converted from Dataset.sql). */
    private static void seedMovies(Connection conn) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(INSERT_MOVIE)) {

            // Inception — 3 showtimes
            insertMovie(ps, "b7d3844f-dc91-4663-913e-8ae458965368", "Inception",       "Sci-Fi",   148, "PG-13", "2025-06-01 10:00:00", 10);
            insertMovie(ps, "d3233e95-229e-46e1-940a-924ca8ba9d28", "Inception",       "Sci-Fi",   148, "PG-13", "2025-06-01 13:00:00", 10);
            insertMovie(ps, "2456c134-2193-4c1c-8be1-eaf4d29c7f77", "Inception",       "Sci-Fi",   148, "PG-13", "2025-06-01 16:00:00", 10);

            // The Dark Knight — 3 showtimes
            insertMovie(ps, "9f980c86-6bfc-4f51-b58c-3ca677e60e4c", "The Dark Knight", "Action",   152, "PG-13", "2025-06-01 11:00:00", 12);
            insertMovie(ps, "4bee7387-7e9d-4b10-8bb5-41e1b130cdfb", "The Dark Knight", "Action",   152, "PG-13", "2025-06-01 14:30:00", 12);
            insertMovie(ps, "b0b11c54-65e2-4ae0-be05-8003436f94a3", "The Dark Knight", "Action",   152, "PG-13", "2025-06-01 18:00:00", 12);

            // Interstellar — 3 showtimes
            insertMovie(ps, "da58841a-25af-4174-a880-f4a899a2a2fd", "Interstellar",    "Sci-Fi",   169, "PG",    "2025-06-01 09:00:00", 11);
            insertMovie(ps, "99a4ba2f-c99e-4770-b5d8-35653f2c6552", "Interstellar",    "Sci-Fi",   169, "PG",    "2025-06-01 13:30:00", 11);
            insertMovie(ps, "52653a7b-7f75-4843-b95d-cb7b1fc26d41", "Interstellar",    "Sci-Fi",   169, "PG",    "2025-06-01 18:00:00", 11);

            // The Godfather — 2 showtimes
            insertMovie(ps, "381ecce1-8016-4810-be83-781533e4516b", "The Godfather",   "Crime",    175, "R",     "2025-06-01 10:30:00", 13);
            insertMovie(ps, "3c9cc47b-846b-4519-b221-1f30d2fde005", "The Godfather",   "Crime",    175, "R",     "2025-06-01 15:00:00", 13);

            // Parasite — 3 showtimes
            insertMovie(ps, "b8058330-4457-4143-9c0a-9604dfe4dd3d", "Parasite",        "Thriller", 132, "R",     "2025-06-01 12:00:00", 10);
            insertMovie(ps, "469c4ea8-4d5c-4418-8db2-4a5aefb81a9f", "Parasite",        "Thriller", 132, "R",     "2025-06-01 15:30:00", 10);
            insertMovie(ps, "310424e1-77da-47c5-b558-b02ea8491b37", "Parasite",        "Thriller", 132, "R",     "2025-06-01 19:00:00", 10);
        }
    }

    /** Inserts all 9 test bookings and their associated seats (converted from Dataset.sql). */
    private static void seedBookings(Connection conn) throws SQLException {
        try (PreparedStatement bps = conn.prepareStatement(INSERT_BOOKING);
             PreparedStatement sps = conn.prepareStatement(INSERT_SEAT)) {

            // Booking 1 — Inception 10:00, 3 seats
            insertBooking(bps, "35862bc5-2471-4544-bdc2-62e1031017b2", "a@a.com",
                          "b7d3844f-dc91-4663-913e-8ae458965368", "2025-06-01 10:00:00", 30, true);
            insertSeats(sps, "35862bc5-2471-4544-bdc2-62e1031017b2", "A1", "A2", "B5");

            // Booking 2 — Inception 13:00, 3 seats
            insertBooking(bps, "80325f11-409d-4678-8487-3815e353b8a1", "a@a.com",
                          "d3233e95-229e-46e1-940a-924ca8ba9d28", "2025-06-01 13:00:00", 30, true);
            insertSeats(sps, "80325f11-409d-4678-8487-3815e353b8a1", "C3", "D7", "D8");

            // Booking 3 — The Dark Knight 11:00, 4 seats
            insertBooking(bps, "10b5df25-48d1-467d-aeac-c2f22346ad78", "a@a.com",
                          "9f980c86-6bfc-4f51-b58c-3ca677e60e4c", "2025-06-01 11:00:00", 48, true);
            insertSeats(sps, "10b5df25-48d1-467d-aeac-c2f22346ad78", "A1", "A2", "A3", "B1");

            // Booking 4 — The Dark Knight 14:30, 2 seats
            insertBooking(bps, "a0ed3b06-1899-494f-bc29-01b2ea109417", "a@a.com",
                          "4bee7387-7e9d-4b10-8bb5-41e1b130cdfb", "2025-06-01 14:30:00", 24, true);
            insertSeats(sps, "a0ed3b06-1899-494f-bc29-01b2ea109417", "E5", "E6");

            // Booking 5 — Interstellar 09:00, 3 seats
            insertBooking(bps, "cb01ff99-71a7-47b1-ac98-fb664fa325bd", "a@a.com",
                          "da58841a-25af-4174-a880-f4a899a2a2fd", "2025-06-01 09:00:00", 33, true);
            insertSeats(sps, "cb01ff99-71a7-47b1-ac98-fb664fa325bd", "F10", "F11", "F12");

            // Booking 6 — Interstellar 13:30, 3 seats
            insertBooking(bps, "ea5ebede-b61d-42bf-befd-23c302f0299c", "a@a.com",
                          "99a4ba2f-c99e-4770-b5d8-35653f2c6552", "2025-06-01 13:30:00", 33, true);
            insertSeats(sps, "ea5ebede-b61d-42bf-befd-23c302f0299c", "G1", "G2", "H5");

            // Booking 7 — The Godfather 10:30, 4 seats
            insertBooking(bps, "34a29185-1690-4ea2-aec0-5bafeb71a1ce", "a@a.com",
                          "381ecce1-8016-4810-be83-781533e4516b", "2025-06-01 10:30:00", 52, true);
            insertSeats(sps, "34a29185-1690-4ea2-aec0-5bafeb71a1ce", "A4", "A5", "B6", "C6");

            // Booking 8 — Parasite 12:00, 2 seats
            insertBooking(bps, "fa918753-89a8-495f-abe6-040ef5218585", "a@a.com",
                          "b8058330-4457-4143-9c0a-9604dfe4dd3d", "2025-06-01 12:00:00", 20, true);
            insertSeats(sps, "fa918753-89a8-495f-abe6-040ef5218585", "B3", "B4");

            // Booking 9 — Parasite 15:30, 2 seats
            insertBooking(bps, "93386c55-8a94-4610-ba14-2ca38cb42f67", "a@a.com",
                          "469c4ea8-4d5c-4418-8db2-4a5aefb81a9f", "2025-06-01 15:30:00", 20, true);
            insertSeats(sps, "93386c55-8a94-4610-ba14-2ca38cb42f67", "D9", "D10");
        }
    }

    // -------------------------------------------------------------------------
    // Row-level helpers  (keep seeders above readable)
    // -------------------------------------------------------------------------

    private static void insertUser(PreparedStatement ps,
                                   String email, String username,
                                   String password, boolean isAdmin) throws SQLException {
        ps.setString (1, email);
        ps.setString (2, username);
        ps.setString (3, password);
        ps.setBoolean(4, isAdmin);
        ps.executeUpdate();
    }

    private static void insertMovie(PreparedStatement ps,
                                    String movieId, String title, String genre,
                                    int duration, String rating,
                                    String showtime, int seatPrice) throws SQLException {
        ps.setString   (1, movieId);
        ps.setString   (2, title);
        ps.setString   (3, genre);
        ps.setInt      (4, duration);
        ps.setString   (5, rating);
        ps.setTimestamp(6, Timestamp.valueOf(LocalDateTime.parse(
                showtime.replace(' ', 'T'))));
        ps.setInt      (7, seatPrice);
        ps.executeUpdate();
    }

    private static void insertBooking(PreparedStatement ps,
                                      String bookingId, String userEmail,
                                      String movieId, String showtime,
                                      int totalPrice, boolean isProcessed) throws SQLException {
        ps.setString   (1, bookingId);
        ps.setString   (2, userEmail);
        ps.setString   (3, movieId);
        ps.setTimestamp(4, Timestamp.valueOf(LocalDateTime.parse(
                showtime.replace(' ', 'T'))));
        ps.setInt      (5, totalPrice);
        ps.setBoolean  (6, isProcessed);
        ps.executeUpdate();
    }

    private static void insertSeats(PreparedStatement ps,
                                    String bookingId, String... seats) throws SQLException {
        for (String seat : seats) {
            ps.setString(1, bookingId);
            ps.setString(2, seat);
            ps.executeUpdate();
        }
    }
}
