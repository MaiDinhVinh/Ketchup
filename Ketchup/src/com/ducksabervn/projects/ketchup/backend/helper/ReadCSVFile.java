package com.ducksabervn.projects.ketchup.backend.helper;

import com.ducksabervn.projects.ketchup.backend.admin.MovieRepository;
import com.ducksabervn.projects.ketchup.backend.credientials.Credential;
import com.ducksabervn.projects.ketchup.backend.admin.Movie;
import com.ducksabervn.projects.ketchup.backend.credientials.CredentialRepository;
import com.ducksabervn.projects.ketchup.backend.user.Booking;
import com.ducksabervn.projects.ketchup.backend.user.BookingRepository;
import com.ducksabervn.projects.ketchup.frontend.AdminMovieListUI;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.LinkedHashMap;


public final class ReadCSVFile{

    private static final Path APP_DIRECTORY = Path.of(System.getProperty("user.home"), "Ketchup");
    private static final Path USER_CREDENTIALS = Path.of(System.getProperty("user.home"), "Ketchup",
                                    "USER_CREDENTIALS.csv");
    private static final Path MOVIES = Path.of(System.getProperty("user.home"), "Ketchup",
                                        "MOVIES.csv");
    private static final Path BOOKINGS = Path.of(System.getProperty("user.home"), "Ketchup",
                                        "BOOKINGS.csv");
    public static void initalize(){
        try{
            if(!Files.exists(APP_DIRECTORY)) {
                Files.createDirectory(APP_DIRECTORY);
            }
            if(!Files.exists(USER_CREDENTIALS)){
                Files.createFile(USER_CREDENTIALS);
                Files.writeString(USER_CREDENTIALS, "USERNAME;EMAIL;PASSWORD;IS_ADMIN");
            }
            if(!Files.exists(MOVIES)){
                Files.createFile(MOVIES);
                Files.writeString(MOVIES, "MOVIEID;TITLE;GENRE;DURATION;RATING;SHOWTIME;SEAT;SPRICE");
            }
            if(!Files.exists(BOOKINGS)){
                Files.createFile(BOOKINGS);
                Files.writeString(BOOKINGS, "EMAIL;BOOKINGID;MOVIEID;SHOWTIME;SEATS;TOTAL_PRICE");
            }
        }catch (IOException e){
            //I still cant figure out for which JFrame will responsible to display the
            //exception string, but this will work as a fallback for now
            DisplayMessage.displayError(AdminMovieListUI.getAdminMovieListUI().getMainFrame(),
                    e.getMessage());
        }
    }

    public static LinkedHashMap<String, Credential> readUserCredentialsCsv(){
        try{
            List<String> allCreds = Files.readAllLines(USER_CREDENTIALS);
            allCreds.remove(0);
            LinkedHashMap<String, Credential> credMap = new LinkedHashMap<>();
            for(String str: allCreds){
                String[] split = str.split(";");
                credMap.put(split[1], new Credential(split[0], split[1], split[2],Boolean.valueOf(split[3])));
            }
            return credMap;
        }catch(IOException e){
            //I still cant figure out for which JFrame will responsible to display the
            //exception string, but this will work as a fallback for now
            DisplayMessage.displayError(AdminMovieListUI.getAdminMovieListUI().getMainFrame(),
                    e.getMessage());
            return null;
        }
    }

    public static LinkedHashMap<String, Movie> readMoviesCsv(){
        try{
            List<String> allMovies = Files.readAllLines(MOVIES);
            allMovies.remove(0);
            LinkedHashMap<String, Movie> movies = new LinkedHashMap<>();
            for(String str: allMovies){
                String[] split = str.split(";");
                movies.put(split[0],
                        new Movie(split[0], split[1],
                                split[2],
                                Integer.parseInt(split[3]),
                                split[4],
                                split[5],
                                split[6],
                                Integer.parseInt(split[7])));
            }
            return movies;
        }catch(IOException e){
            //I still cant figure out for which JFrame will responsible to display the
            //exception string, but this will work as a fallback for now
            DisplayMessage.displayError(AdminMovieListUI.getAdminMovieListUI().getMainFrame(),
                    e.getMessage());
            return null;
        }
    }

    //why we need userEmail ? Because normal/admin user can't see other user's private data
    public static LinkedHashMap<String, Booking> readBookingCsv(String userEmail){
        try{
            List<String> allBookings = Files.readAllLines(BOOKINGS);
            allBookings.remove(0);
            LinkedHashMap<String, Booking> bookings = new LinkedHashMap<>();
            for(String b: allBookings){
                String[] split = b.split(";");
                if(split[0].equals(userEmail)){
                    LocalDateTime showtime = LocalDateTime.parse(split[3], Movie.getDatetimeFormat());
                    ArrayList<String> chosenSeats = new ArrayList<>(Arrays.asList(split[4].split(",")));
                    int totalPrice = Integer.parseInt(split[5]);
                    boolean isProcessed = Boolean.parseBoolean(split[6]);
                    bookings.put(split[1], new Booking(split[0],
                            split[1], split[2], showtime, chosenSeats, totalPrice, isProcessed));
                }
            }
            return bookings;
        }catch(IOException e){
            //I still cant figure out for which JFrame will responsible to display the
            //exception string, but this will work as a fallback for now
            DisplayMessage.displayError(AdminMovieListUI.getAdminMovieListUI().getMainFrame(),
                    e.getMessage());
            return null;
        }
    }

    public static void writeMovieData(String data){
        //adding true to FileWriter will turn on append mode and won't override the CSV file
        //fuck ass i forgor this shit for 3 times
        try(BufferedWriter bw = new BufferedWriter(new FileWriter(MOVIES.toFile(), true))){
            bw.newLine();
            bw.write(data);
        }catch(IOException e){
            //I still cant figure out for which JFrame will responsible to display the
            //exception string, but this will work as a fallback for now
            DisplayMessage.displayError(AdminMovieListUI.getAdminMovieListUI().getMainFrame(),
                    e.getMessage());
        }
    }

    public static void updateDataBackground() {
        LinkedHashMap<String, Movie> updatedMovies = MovieRepository.getMovies();
        LinkedHashMap<String, Booking> allBookings = BookingRepository.getBookings();
        LinkedHashMap<String, Credential> updatedCredentials = CredentialRepository.getCredentials();
        try(BufferedWriter bw = new BufferedWriter(new FileWriter(MOVIES.toFile()));
            BufferedWriter bw2 = new BufferedWriter(new FileWriter(USER_CREDENTIALS.toFile()));
            BufferedWriter bw3 = new BufferedWriter(new FileWriter(BOOKINGS.toFile(), true))){
            bw.write("MOVIEID;TITLE;GENRE;DURATION;RATING;SHOWTIME;SEAT;SPRICE");
            if(allBookings != null){
                ArrayList<Booking> bookings = new ArrayList<>(allBookings.values());
                ArrayList<Movie> allMovies = new ArrayList<>(updatedMovies.values());
                int bookingCount = bookings.size();
                int movieCount = allMovies.size();
                for(int i = 0; i < bookingCount; i++){
                    for(int j = 0; j < movieCount; j++){
                        if(bookings.get(i).getMovieId().equals(allMovies.get(j).getMovieId()) &&
                        !bookings.get(i).isProcessed()){
                            Movie m = allMovies.get(j);
                            Booking b = bookings.get(i);
                            m.getOccupiedSeat().addAll(b.getChosenSeats());
                            allMovies.set(j, m);
                        }
                    }
                }
                for(Movie m: allMovies){
                    bw.newLine();
                    bw.write(MovieRepository.generateMovieDataAsString(m));
                }
            }else{
                for(Movie m: updatedMovies.values()){
                    bw.newLine();
                    bw.write(MovieRepository.generateMovieDataAsString(m));
                }
            }
            bw2.write("USERNAME;EMAIL;PASSWORD;IS_ADMIN");
            for(Credential c: updatedCredentials.values()){
                bw2.newLine();
                bw2.write(CredentialRepository.generateCredentialDataAsString(c));
            }
            for(Booking b: allBookings.values()){
                if(!b.isProcessed()){
                    b.setProcessed(true);
                    bw3.newLine();
                    bw3.write(BookingRepository.generateDataAsString(b));
                }
            }
        }catch(IOException e){
            //I still cant figure out for which JFrame will responsible to display the
            //exception string, but this will work as a fallback for now
            DisplayMessage.displayError(AdminMovieListUI.getAdminMovieListUI().getMainFrame(),
                    e.getMessage());
        }
    }
}
