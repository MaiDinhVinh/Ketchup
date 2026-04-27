package com.ducksabervn.projects.ketchup.backend.admin;

import com.ducksabervn.projects.ketchup.backend.helper.ReadCSVFile;
import com.ducksabervn.projects.ketchup.frontend.AdminMovieFormUI;

import java.util.*;
import java.util.stream.Collectors;

public class MovieRepository {
    //this motherfucker will maintain the insert input unlike HashMap, bro i want to kms
    private static LinkedHashMap<String, Movie> movies;

    public static LinkedHashMap<String, Movie> getMovies() {
        return movies;
    }

    public static void setMovies(LinkedHashMap<String, Movie> movies) {
        MovieRepository.movies = movies;
    }

    public static Movie addMovie(String title,
                                 String genre,
                                 int duration,
                                 String rating,
                                 String showTime,
                                 String occupiedSeat,
                                 int seatPrice){
        String movieId = UUID.randomUUID().toString();
        Movie m = new Movie(movieId,
                title,
                genre,
                duration,
                rating,
                showTime,
                occupiedSeat,
                seatPrice);
        MovieRepository.movies.put(movieId, m);
        ReadCSVFile.writeMovieData(generateMovieDataAsString(m));
        return m;
    }

    public static void editMovie(String id, Movie edited){
        MovieRepository.movies.put(id, edited);
    }

    public static void deleteMovie(String id){
        MovieRepository.movies.remove(id);
    }

    public static ArrayList<Movie> searchMovie(String information){
        ArrayList<Movie> arr = new ArrayList<>();
        if(MovieRepository.movies.containsKey(information)){
            arr.add(MovieRepository.movies.get(information)); //searching by id
        }else{
            for(Movie m: MovieRepository.movies.values()){
                if(m.getTitle().equals(information) ||
                m.getGenre().equals(information) ||
                 Integer.toString(m.getDuration()).equals(information) ||
                m.getRating().equals(information) ||
                m.getShowTime().format(Movie.getDatetimeFormat()).equals(information) ||
                Integer.toString(m.getSeatPrice()).equals(information)){
                    arr.add(m);
                }
            }
        }
        return arr;
    }

    public static String generateMovieDataAsString(Movie m){
        String occupiedSeats = m.getOccupiedSeat().stream().
                collect(Collectors.joining(","));
        String data = "%s;%s;%s;%d;%s;%s;%s;%d".formatted(m.getMovieId(),
                m.getTitle(),
                m.getGenre(),
                m.getDuration(),
                m.getRating(),
                m.getShowTime().format(Movie.getDatetimeFormat()),
                occupiedSeats,
                m.getSeatPrice());
        return data;
    }
}
