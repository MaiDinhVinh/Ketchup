package com.ducksabervn.projects.ketchup.backend.movie;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class Movie {

    private String movieId;
    private String title;
    private String genre;
    private int duration;
    private String rating;
    private LocalDateTime showTime;
    private ArrayList<String> occupiedSeat;
    private int seatPrice;

    private static final DateTimeFormatter DATETIME_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public Movie(String movieId,
                 String title,
                 String genre,
                 int duration,
                 String rating,
                 String showTime,
                 String occupiedSeat,
                 int seatPrice){
        this.movieId = movieId;
        this.title = title;
        this.genre = genre;
        this.duration = duration;
        this.rating = rating;
        this.showTime = LocalDateTime.parse(showTime, DATETIME_FORMAT);
        this.seatPrice = seatPrice;

        String[] allOccupiedSeats = occupiedSeat.split(",");
        this.occupiedSeat = new ArrayList<>();
        for(String str: allOccupiedSeats){
            this.occupiedSeat.add(str);
        }
    }

    public String getMovieId() {
        return movieId;
    }

    public int getDuration() {
        return duration;
    }

    public ArrayList<String> getOccupiedSeat() {
        return occupiedSeat;
    }

    public int getSeatPrice() {
        return seatPrice;
    }

    public String getGenre() {
        return genre;
    }

    public String getRating() {
        return rating;
    }

    public LocalDateTime getShowTime() {
        return showTime;
    }

    public String getTitle() {
        return title;
    }

    @Override
    public String toString() {
        return "Movie{" +
                "movieId='" + movieId + '\'' +
                ", title='" + title + '\'' +
                ", genre='" + genre + '\'' +
                ", duration=" + duration +
                ", rating='" + rating + '\'' +
                ", showTime='" + showTime + '\'' +
                ", occupiedSeat=" + occupiedSeat +
                ", seatPrice=" + seatPrice +
                '}';
    }

    public static DateTimeFormatter getDatetimeFormat() {
        return DATETIME_FORMAT;
    }
}
