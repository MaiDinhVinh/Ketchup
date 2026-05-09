package com.ducksabervn.projects.ketchup.backend.io;

import com.ducksabervn.projects.ketchup.backend.ui.DisplayMessage;
import com.ducksabervn.projects.ketchup.frontend.AdminMovieListUI;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public final class FileSystemInitializer {
    public static void initalize(){
        try{
            if(!Files.exists(CsvPersistable.APP_DIRECTORY)) {
                Files.createDirectory(CsvPersistable.APP_DIRECTORY);
            }
            if(!Files.exists(CsvPersistable.USER_CREDENTIALS)){
                Files.createFile(CsvPersistable.USER_CREDENTIALS);
                Files.writeString(CsvPersistable.USER_CREDENTIALS, "USERNAME;EMAIL;PASSWORD;IS_ADMIN");
            }
            if(!Files.exists(CsvPersistable.MOVIES)){
                Files.createFile(CsvPersistable.MOVIES);
                Files.writeString(CsvPersistable.MOVIES, "MOVIEID;TITLE;GENRE;DURATION;RATING;SHOWTIME;SEAT;SPRICE");
            }
            if(!Files.exists(CsvPersistable.BOOKINGS)){
                Files.createFile(CsvPersistable.BOOKINGS);
                Files.writeString(CsvPersistable.BOOKINGS, "EMAIL;BOOKINGID;MOVIEID;SHOWTIME;SEATS;TOTAL_PRICE");
            }
        }catch (IOException e){
            //I still cant figure out for which JFrame will responsible to display the
            //exception string, but this will work as a fallback for now
            DisplayMessage.displayError(AdminMovieListUI.getAdminMovieListUI().getMainFrame(),
                    e.getMessage());
        }
    }
}
