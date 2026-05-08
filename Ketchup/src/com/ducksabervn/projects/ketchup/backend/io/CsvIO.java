package com.ducksabervn.projects.ketchup.backend.io;

import java.nio.file.Path;
import java.util.LinkedHashMap;

interface CsvIO<U, V>{
    Path APP_DIRECTORY = Path.of(System.getProperty("user.home"), "Ketchup");
    Path USER_CREDENTIALS = Path.of(System.getProperty("user.home"), "Ketchup",
            "USER_CREDENTIALS.csv");
    Path MOVIES = Path.of(System.getProperty("user.home"), "Ketchup",
            "MOVIES.csv");
    Path BOOKINGS = Path.of(System.getProperty("user.home"), "Ketchup",
            "BOOKINGS.csv");

    LinkedHashMap<U, V> readCsvFile();
    LinkedHashMap<U, V> readCsvFile(String requiredInformation);
    void updateLatestData();
}
