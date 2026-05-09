package com.ducksabervn.projects.ketchup.backend.io;

import java.io.IOException;
import java.nio.file.Files;

public final class FileSystemInitializer {
    public static void initalize() throws IOException{
        try{
            if(!Files.exists(AppPath.APP_DIRECTORY.getAppPath())) {
                Files.createDirectory(AppPath.APP_DIRECTORY.getAppPath());
            }
            if(!Files.exists(AppPath.USER_CREDENTIALS.getAppPath())){
                Files.createFile(AppPath.USER_CREDENTIALS.getAppPath());
                Files.writeString(AppPath.USER_CREDENTIALS.getAppPath(), "USERNAME;EMAIL;PASSWORD;IS_ADMIN");
            }
            if(!Files.exists(AppPath.MOVIES.getAppPath())){
                Files.createFile(AppPath.MOVIES.getAppPath());
                Files.writeString(AppPath.MOVIES.getAppPath(), "MOVIEID;TITLE;GENRE;DURATION;RATING;SHOWTIME;SEAT;SPRICE");
            }
            if(!Files.exists(AppPath.BOOKINGS.getAppPath())){
                Files.createFile(AppPath.BOOKINGS.getAppPath());
                Files.writeString(AppPath.BOOKINGS.getAppPath(), "EMAIL;BOOKINGID;MOVIEID;SHOWTIME;SEATS;TOTAL_PRICE");
            }
        }catch (IOException e){
            throw e;
        }
    }
}
