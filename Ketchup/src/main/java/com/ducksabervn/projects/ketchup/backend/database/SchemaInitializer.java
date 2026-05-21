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
 * File Name:       SchemaInitializer.java
 * Developer:       Tran Phan Anh*, Nguyen Trong Khoi Nguyen*, Nguyen Dinh Quy*,
 *                  Mai Dinh Vinh* (* equal contributions)
 * Description:     Executes all DDL statements required to set up the Ketchup
 *                  database schema (tables: users, movies, bookings,
 *                  booking_seats). Converted from KetchupSchema.sql to Java so
 *                  that no classpath resource loading or SQL file parsing is
 *                  needed, and so that MariaDB4j lifecycle statements
 *                  (CREATE DATABASE, USE) are handled separately by
 *                  DatabaseService rather than inside a raw script.
 ******************************************************************************/

package com.ducksabervn.projects.ketchup.backend.database;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Utility class that creates all Ketchup database tables if they do not
 * already exist. All methods are static; this class is not meant to be
 * instantiated.
 *
 * <p>Called once by {@link DatabaseService#startup()} on the very first
 * launch, before {@link DatasetSeeder} populates the tables with test data.</p>
 */
final class SchemaInitializer {

    // -------------------------------------------------------------------------
    // DDL statements  (converted from KetchupSchema.sql)
    // CREATE DATABASE and USE are intentionally omitted — DatabaseService
    // handles database creation via MariaDB4j's createDB() API, and the
    // JDBC URL already targets the correct schema.
    // -------------------------------------------------------------------------

    private static final String CREATE_USERS = """
            CREATE TABLE IF NOT EXISTS users (
                email       VARCHAR(320)    NOT NULL,
                username    VARCHAR(100)    NOT NULL,
                password    VARCHAR(255)    NOT NULL,
                is_admin    BOOLEAN         NOT NULL DEFAULT FALSE,
                PRIMARY KEY (email)
            )
            """;

    private static final String CREATE_MOVIES = """
            CREATE TABLE IF NOT EXISTS movies (
                movie_id    CHAR(36)        NOT NULL,
                title       VARCHAR(255)    NOT NULL,
                genre       VARCHAR(100)    NOT NULL,
                duration    INT UNSIGNED    NOT NULL,
                rating      VARCHAR(10)     NOT NULL,
                showtime    DATETIME        NOT NULL,
                seat_price  INT UNSIGNED    NOT NULL,
                PRIMARY KEY (movie_id)
            )
            """;

    private static final String CREATE_BOOKINGS = """
            CREATE TABLE IF NOT EXISTS bookings (
                booking_id      CHAR(36)        NOT NULL,
                user_email      VARCHAR(320)    NOT NULL,
                movie_id        CHAR(36)        NOT NULL,
                showtime        DATETIME        NOT NULL,
                total_price     INT UNSIGNED    NOT NULL,
                is_processed    BOOLEAN         NOT NULL DEFAULT FALSE,
                PRIMARY KEY (booking_id),
                FOREIGN KEY (user_email)  REFERENCES users  (email),
                FOREIGN KEY (movie_id)    REFERENCES movies (movie_id)
            )
            """;

    private static final String CREATE_BOOKING_SEATS = """
            CREATE TABLE IF NOT EXISTS booking_seats (
                booking_id  CHAR(36)    NOT NULL,
                seat_id     VARCHAR(4)  NOT NULL,
                PRIMARY KEY (booking_id, seat_id),
                FOREIGN KEY (booking_id) REFERENCES bookings (booking_id)
            )
            """;

    /** Execution order matters: parent tables must be created before children. */
    private static final String[] DDL_STATEMENTS = {
            CREATE_USERS,
            CREATE_MOVIES,
            CREATE_BOOKINGS,
            CREATE_BOOKING_SEATS
    };

    // -------------------------------------------------------------------------
    // Constructor
    // -------------------------------------------------------------------------

    /** Private — this class is not meant to be instantiated. */
    private SchemaInitializer() {}

    // -------------------------------------------------------------------------
    // Public API
    // -------------------------------------------------------------------------

    /**
     * Executes all DDL statements against the provided connection.
     * Each statement uses {@code IF NOT EXISTS}, so calling this method on an
     * already-initialized database is safe and has no side effects.
     *
     * @param conn an open {@link Connection} to the {@code ketchup} database
     * @throws SQLException if any DDL statement fails to execute
     */
    static void initialize(Connection conn) throws SQLException {
        try (Statement stmt = conn.createStatement()) {
            for (String ddl : DDL_STATEMENTS) {
                stmt.execute(ddl);
            }
        }
    }
}
