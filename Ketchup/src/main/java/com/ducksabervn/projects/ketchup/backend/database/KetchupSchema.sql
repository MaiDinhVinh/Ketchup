-- =============================================================================
-- Project  : Ketchup — Movie Management System
-- Course   : COMP1020 — OOP and Data Structure, Spring 2026
-- File     : ketchup_schema.sql
-- =============================================================================

CREATE DATABASE IF NOT EXISTS ketchup;
USE ketchup;
CREATE TABLE IF NOT EXISTS users (
                                     email       VARCHAR(320)    NOT NULL,
                                     username    VARCHAR(100)    NOT NULL,
                                     password    VARCHAR(255)    NOT NULL,
                                     is_admin    BOOLEAN         NOT NULL DEFAULT FALSE,

                                     PRIMARY KEY (email)
);
CREATE TABLE IF NOT EXISTS movies (
                                      movie_id    CHAR(36)        NOT NULL,
                                      title       VARCHAR(255)    NOT NULL,
                                      genre       VARCHAR(100)    NOT NULL,
                                      duration    INT UNSIGNED    NOT NULL,
                                      rating      VARCHAR(10)     NOT NULL,
                                      showtime    DATETIME        NOT NULL,
                                      seat_price  INT UNSIGNED    NOT NULL,

                                      PRIMARY KEY (movie_id)
);
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
);
CREATE TABLE IF NOT EXISTS booking_seats (
                                             booking_id  CHAR(36)    NOT NULL,
                                             seat_id     VARCHAR(4)  NOT NULL,

                                             PRIMARY KEY (booking_id, seat_id),
                                             FOREIGN KEY (booking_id) REFERENCES bookings (booking_id)
);