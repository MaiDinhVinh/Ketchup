package com.ducksabervn.projects.ketchup.backend.admin;

import java.util.TreeMap;
import java.util.UUID;

public class Movies {
    private static TreeMap<String, Movie> movies;

    public static TreeMap<String, Movie> getMovies() {
        return movies;
    }

    public static void setMovies(TreeMap<String, Movie> movies) {
        Movies.movies = movies;
    }

    public static Movie addMovies(String title,
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
        Movies.movies.put(movieId, m);
        return m;
    }
}
