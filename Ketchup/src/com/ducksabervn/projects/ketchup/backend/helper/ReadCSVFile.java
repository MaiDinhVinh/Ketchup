package com.ducksabervn.projects.ketchup.backend.helper;

import com.ducksabervn.projects.ketchup.backend.admin.MovieRepository;
import com.ducksabervn.projects.ketchup.backend.credientials.Credential;
import com.ducksabervn.projects.ketchup.backend.admin.Movie;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.LinkedHashMap;


public final class ReadCSVFile{

    private static final Path APP_DIRECTORY = Path.of(System.getProperty("user.home"), "Ketchup");
    private static final Path USER_CREDENTIALS = Path.of(System.getProperty("user.home"), "Ketchup",
                                    "USER_CREDENTIALS.csv");
    private static final Path MOVIES = Path.of(System.getProperty("user.home"), "Ketchup",
                                        "MOVIES.csv");
    public static void initalize(){
        try{
            if(!Files.exists(APP_DIRECTORY)) {
                Files.createDirectory(APP_DIRECTORY);
            }
            if(!Files.exists(USER_CREDENTIALS)){
                Files.createFile(USER_CREDENTIALS);
                Files.writeString(USER_CREDENTIALS, "EMAIL;PASSWORD");
            }

            if(!Files.exists(MOVIES)){
                Files.createFile(MOVIES);
                Files.writeString(MOVIES, "MOVIEID;TITLE;GENRE;DURATION;RATING;SHOWTIME;SEAT;SPRICE");
            }
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    public static LinkedHashMap<String, Credential> readUserCredentialsCsv(){
        try{
            List<String> allCreds = Files.readAllLines(USER_CREDENTIALS);
            allCreds.remove(0);
            LinkedHashMap<String, Credential> credMap = new LinkedHashMap<>();
            for(String str: allCreds){
                String[] split = str.split(";");
                credMap.put(split[0], new Credential(split[0], split[1], Boolean.valueOf(split[2])));
            }
            return credMap;
        }catch(IOException e){
            e.printStackTrace();
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
            e.printStackTrace();
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
            e.printStackTrace();
        }
    }

    public static void updateDataBackground() {
        LinkedHashMap<String, Movie> updatedMovies = MovieRepository.getMovies();
        try(BufferedWriter bw = new BufferedWriter(new FileWriter(MOVIES.toFile()))){
            bw.write("MOVIEID;TITLE;GENRE;DURATION;RATING;SHOWTIME;SEAT;SPRICE");
            for(Movie m: updatedMovies.values()){
                bw.newLine();
                bw.write(MovieRepository.generateMovieDataAsString(m));
            }
        }catch(IOException e){
            e.printStackTrace();
        }
    }
}
