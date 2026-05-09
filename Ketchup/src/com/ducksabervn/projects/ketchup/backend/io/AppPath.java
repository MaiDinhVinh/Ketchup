package com.ducksabervn.projects.ketchup.backend.io;

import java.nio.file.Path;

enum AppPath {
    APP_DIRECTORY(Path.of(System.getProperty("user.home"), "Ketchup")),
    USER_CREDENTIALS(Path.of(System.getProperty("user.home"), "Ketchup",
            "USER_CREDENTIALS.csv")),
    MOVIES(Path.of(System.getProperty("user.home"), "Ketchup",
            "MOVIES.csv")),
    BOOKINGS(Path.of(System.getProperty("user.home"), "Ketchup",
            "BOOKINGS.csv"));

    private final Path appPath;

    private AppPath(Path appPath){
        this.appPath = appPath;
    }

    public Path getAppPath(){
        return this.appPath;
    }
}
