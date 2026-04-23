package com.ducksabervn.projects.ketchup.backend.admin;

import com.ducksabervn.projects.ketchup.backend.helper.ReadCSVFile;

import java.util.TreeMap;
import java.util.UUID;
import java.util.stream.Collectors;

public class MovieRepository {
    private static TreeMap<String, Movie> movies;

    public static TreeMap<String, Movie> getMovies() {
        return movies;
    }

    public static void setMovies(TreeMap<String, Movie> movies) {
        MovieRepository.movies = movies;
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
        MovieRepository.movies.put(movieId, m);
        String occupiedSeats = m.getOccupiedSeat().stream().
                collect(Collectors.joining(","));
        String data = "%s;%s;%s;%d;%s;%s;%s;%d".formatted(m.getMovieId(),
                m.getTitle(),
                m.getGenre(),
                m.getDuration(),
                m.getRating(),
                m.getShowTime(),
                occupiedSeats,
                m.getSeatPrice());
        ReadCSVFile.writeMovieData(data);
        return m;
    }

    public static void
}
