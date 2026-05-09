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
 * File Name:       FileSystemInitializer.java
 * Developer:       Tran Phan Anh*, Nguyen The Khoi Nguyen*, Nguyen Dinh Quy*,
 *                  Mai Dinh Vinh* (* equal contributions)
 * Description:     Ensures all required application directories and CSV data
 *                  files exist on the local file system before the application
 *                  starts, creating them with default headers if absent.
 ******************************************************************************/

package com.ducksabervn.projects.ketchup.backend.io;

import java.io.IOException;
import java.nio.file.Files;

/**
 * Utility class responsible for bootstrapping the Ketchup application's
 * file system structure on startup. All methods are static; this class
 * is not meant to be instantiated.
 */
public final class FileSystemInitializer {

    /**
     * Verifies that the Ketchup application directory and all required CSV
     * data files exist, creating any that are missing along with their
     * corresponding header rows.
     *
     * @throws IOException if any directory or file cannot be created
     */
    public static void initalize() throws IOException {
        try {
            if (!Files.exists(AppPath.APP_DIRECTORY.getAppPath())) {
                Files.createDirectory(AppPath.APP_DIRECTORY.getAppPath());
            }
            if (!Files.exists(AppPath.USER_CREDENTIALS.getAppPath())) {
                Files.createFile(AppPath.USER_CREDENTIALS.getAppPath());
                Files.writeString(AppPath.USER_CREDENTIALS.getAppPath(), "USERNAME;EMAIL;PASSWORD;IS_ADMIN");
            }
            if (!Files.exists(AppPath.MOVIES.getAppPath())) {
                Files.createFile(AppPath.MOVIES.getAppPath());
                Files.writeString(AppPath.MOVIES.getAppPath(), "MOVIEID;TITLE;GENRE;DURATION;RATING;SHOWTIME;SEAT;SPRICE");
            }
            if (!Files.exists(AppPath.BOOKINGS.getAppPath())) {
                Files.createFile(AppPath.BOOKINGS.getAppPath());
                Files.writeString(AppPath.BOOKINGS.getAppPath(), "EMAIL;BOOKINGID;MOVIEID;SHOWTIME;SEATS;TOTAL_PRICE;IS_PROCESSED");
            }
        } catch (IOException e) {
            throw e;
        }
    }
}