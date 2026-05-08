package com.ducksabervn.projects.ketchup.backend.booking;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Booking {
    private String bookingEmail;
    private String bookingId;
    private String movieId;
    private LocalDateTime showtime;
    private ArrayList<String> chosenSeats;
    private int totalPrice;
    private boolean isProcessed;

    public Booking(String bookingEmail,
                   String bookingId,
                   String movieId,
                   LocalDateTime showtime,
                   ArrayList<String> chosenSeats,
                   int totalPrice,
                   boolean isProcessed){
        this.bookingEmail = bookingEmail;
        this.bookingId = bookingId;
        this.movieId = movieId;
        this.showtime = showtime;
        this.chosenSeats = chosenSeats;
        this.totalPrice = totalPrice;
        this.isProcessed = isProcessed;
    }

    public String getBookingEmail() {
        return bookingEmail;
    }

    public String getBookingId() {
        return bookingId;
    }

    public String getMovieId() {
        return movieId;
    }

    public LocalDateTime getShowtime() {
        return showtime;
    }

    public ArrayList<String> getChosenSeats() {
        return chosenSeats;
    }

    public int getTotalPrice() {
        return totalPrice;
    }

    public boolean isProcessed() {
        return isProcessed;
    }

    public void setProcessed(boolean processed) {
        isProcessed = processed;
    }
}
