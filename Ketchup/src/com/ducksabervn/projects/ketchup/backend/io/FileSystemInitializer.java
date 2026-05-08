package com.ducksabervn.projects.ketchup.backend.io;

import com.ducksabervn.projects.ketchup.backend.ui.DisplayMessage;
import com.ducksabervn.projects.ketchup.frontend.AdminMovieListUI;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public final class FileSystemInitializer {
    static Path getAppDirectory() {
        return CsvIO.APP_DIRECTORY;
    }

    static Path getBOOKINGS() {
        return CsvIO.BOOKINGS;
    }

    static Path getMOVIES() {
        return CsvIO.MOVIES;
    }

    static Path getUserCredentials() {
        return CsvIO.USER_CREDENTIALS;
    }

    public static void initalize(){
        try{
            if(!Files.exists(CsvIO.APP_DIRECTORY)) {
                Files.createDirectory(CsvIO.APP_DIRECTORY);
            }
            if(!Files.exists(CsvIO.USER_CREDENTIALS)){
                Files.createFile(CsvIO.USER_CREDENTIALS);
                Files.writeString(CsvIO.USER_CREDENTIALS, "USERNAME;EMAIL;PASSWORD;IS_ADMIN");
            }
            if(!Files.exists(CsvIO.MOVIES)){
                Files.createFile(CsvIO.MOVIES);
                Files.writeString(CsvIO.MOVIES, "MOVIEID;TITLE;GENRE;DURATION;RATING;SHOWTIME;SEAT;SPRICE");
            }
            if(!Files.exists(CsvIO.BOOKINGS)){
                Files.createFile(CsvIO.BOOKINGS);
                Files.writeString(CsvIO.BOOKINGS, "EMAIL;BOOKINGID;MOVIEID;SHOWTIME;SEATS;TOTAL_PRICE");
            }
        }catch (IOException e){
            //I still cant figure out for which JFrame will responsible to display the
            //exception string, but this will work as a fallback for now
            DisplayMessage.displayError(AdminMovieListUI.getAdminMovieListUI().getMainFrame(),
                    e.getMessage());
        }
    }
}
