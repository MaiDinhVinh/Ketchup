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
 * File Name:       AppPath.java
 * Developer:       Tran Phan Anh*, Nguyen The Khoi Nguyen*, Nguyen Dinh Quy*,
 *                  Mai Dinh Vinh* (* equal contributions)
 * Description:     Defines all file system paths used by the application,
 *                  including the app directory and all CSV data files.
 ******************************************************************************/

package com.ducksabervn.projects.ketchup.backend.io;

import java.nio.file.Path;

/**
 * Enum representing all file system paths required by the Ketchup application.
 * Each constant resolves its path relative to the current user's home directory.
 */
enum AppPath {

    /**
     * Path to the root Ketchup application directory ({@code ~/Ketchup}).
     * This directory is created on first launch if it does not already exist.
     */
    APP_DIRECTORY(Path.of(System.getProperty("user.home"), "Ketchup")),

    /**
     * Path to the user credentials CSV file ({@code ~/Ketchup/USER_CREDENTIALS.csv}).
     * Stores username, email, hashed password, and admin flag for all registered users.
     */
    USER_CREDENTIALS(Path.of(System.getProperty("user.home"), "Ketchup",
            "USER_CREDENTIALS.csv")),

    /**
     * Path to the movies CSV file ({@code ~/Ketchup/MOVIES.csv}).
     * Stores all movie records including showtimes, occupied seats, and pricing.
     */
    MOVIES(Path.of(System.getProperty("user.home"), "Ketchup",
            "MOVIES.csv")),

    /**
     * Path to the bookings CSV file ({@code ~/Ketchup/BOOKINGS.csv}).
     * Stores all booking records across all users.
     */
    BOOKINGS(Path.of(System.getProperty("user.home"), "Ketchup",
            "BOOKINGS.csv"));

    /**
     * The resolved {@link Path} object associated with this enum constant.
     */
    private final Path appPath;

    /**
     * Constructs an {@code AppPath} enum constant with the given file system path.
     *
     * @param appPath the {@link Path} this constant represents
     */
    private AppPath(Path appPath) {
        this.appPath = appPath;
    }

    /**
     * Returns the {@link Path} associated with this enum constant.
     *
     * @return the file system path for this application resource
     */
    public Path getAppPath() {
        return this.appPath;
    }
}