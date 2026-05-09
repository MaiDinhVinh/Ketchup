package com.ducksabervn.projects.ketchup.backend.model;

import java.time.LocalDateTime;
import java.util.HashSet;

public class Booking {
    private String bookingEmail;
    private String bookingId;
    private String movieId;
    private LocalDateTime showtime;
    private HashSet<String> chosenSeats;
    private int totalPrice;
    private boolean isProcessed;

    public Booking(String bookingEmail,
                   String bookingId,
                   String movieId,
                   LocalDateTime showtime,
                   HashSet<String> chosenSeats,
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

    public HashSet<String> getChosenSeats() {
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
