package com.ducksabervn.projects.ketchup.backend.admin;

import java.util.ArrayList;
import java.util.TreeMap;

public class Movie {

    private String movieId;
    private String title;
    private String genre;
    private int duration;
    private String rating;
    private String showTime;
    private ArrayList<String> occupiedSeat;
    private int seatPrice;

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
        this.showTime = showTime;
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

    public String getShowTime() {
        return showTime;
    }

    public String getTitle() {
        return title;
    }
}
