package com.ducksabervn.projects.ketchup.backend.io;

import java.nio.file.Path;

interface CsvPersistable {
    Path APP_DIRECTORY = Path.of(System.getProperty("user.home"), "Ketchup");
    Path USER_CREDENTIALS = Path.of(System.getProperty("user.home"), "Ketchup",
            "USER_CREDENTIALS.csv");
    Path MOVIES = Path.of(System.getProperty("user.home"), "Ketchup",
            "MOVIES.csv");
    Path BOOKINGS = Path.of(System.getProperty("user.home"), "Ketchup",
            "BOOKINGS.csv");

    void updateLatestData();
}
