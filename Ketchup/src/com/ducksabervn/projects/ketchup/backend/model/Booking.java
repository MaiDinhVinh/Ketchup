/******************************************************************************
 * Project Name:    Ketchup - A movie management system
 * Course:          COMP1020 - OOP and Data Structure
 * Semester:        Spring 2026
 * <p>
 * Members: Tran Phan Anh <25anh.tp@vinuni.edu.vn>,
 *          Nguyen The Khoi Nguyen <25nguyen.ntk@vinuni.edu.vn>,
 *          Nguyen Dinh Quy <25quy.nd@vinuni.edu.vn>,
 *          Hoang Duc Phat <25phat.hd@vinuni.edu.vn>,
 *          Mai Dinh Vinh <25vinh.md@vinuni.edu.vn>
 * <p>
 * File Name:       Booking.java
 * Developer:       Tran Phan Anh*, Nguyen The Khoi Nguyen*, Nguyen Dinh Quy*,
 *                  Mai Dinh Vinh* (* equal contributions)
 * Description:     Model class representing a single ticket booking made by
 *                  a user, storing all relevant booking details including the
 *                  associated movie, chosen seats, total price, and processing
 *                  status.
 ******************************************************************************/

package com.ducksabervn.projects.ketchup.backend.model;

import java.time.LocalDateTime;
import java.util.HashSet;

/**
 * Represents a single movie ticket booking placed by a registered user.
 * Each booking captures the user's email, the target movie and showtime,
 * the seats selected, the total amount charged, and whether the booking
 * has already been written to the CSV file on disk.
 */
public class Booking {

    /**
     * The email address of the user who made this booking.
     * Used to associate the booking with a specific account.
     */
    private String bookingEmail;

    /**
     * The unique identifier for this booking, generated as a UUID string.
     */
    private String bookingId;

    /**
     * The unique identifier of the movie this booking is for,
     * corresponding to a key in {@link com.ducksabervn.projects.ketchup.backend.repositories.MovieRepository}.
     */
    private String movieId;

    /**
     * The scheduled showtime of the booked movie session.
     */
    private LocalDateTime showtime;

    /**
     * The set of seat IDs (e.g. {@code "A1"}, {@code "B3"}) selected by the user
     * for this booking. Uses a {@link HashSet} to guarantee uniqueness and
     * provide constant-time lookup.
     */
    private HashSet<String> chosenSeats;

    /**
     * The total price charged for this booking, calculated as
     * the number of chosen seats multiplied by the movie's seat price.
     */
    private int totalPrice;

    /**
     * Indicates whether this booking has been persisted to {@code BOOKINGS.csv}.
     * Set to {@code false} when the booking is first created in memory, and
     * flipped to {@code true} once it has been written to disk.
     */
    private boolean isProcessed;

    /**
     * Constructs a new {@code Booking} with all required fields.
     *
     * @param bookingEmail the email address of the user making the booking
     * @param bookingId    the unique UUID string identifying this booking
     * @param movieId      the ID of the movie being booked
     * @param showtime     the scheduled showtime of the movie session
     * @param chosenSeats  the set of seat IDs selected by the user
     * @param totalPrice   the total price for all selected seats
     * @param isProcessed  {@code true} if this booking has already been
     *                     written to disk, {@code false} otherwise
     */
    public Booking(String bookingEmail,
                   String bookingId,
                   String movieId,
                   LocalDateTime showtime,
                   HashSet<String> chosenSeats,
                   int totalPrice,
                   boolean isProcessed) {
        this.bookingEmail = bookingEmail;
        this.bookingId = bookingId;
        this.movieId = movieId;
        this.showtime = showtime;
        this.chosenSeats = chosenSeats;
        this.totalPrice = totalPrice;
        this.isProcessed = isProcessed;
    }

    /**
     * Returns the email address of the user who made this booking.
     *
     * @return the booking owner's email address
     */
    public String getBookingEmail() {
        return bookingEmail;
    }

    /**
     * Returns the unique identifier of this booking.
     *
     * @return the booking ID as a UUID string
     */
    public String getBookingId() {
        return bookingId;
    }

    /**
     * Returns the ID of the movie associated with this booking.
     *
     * @return the movie ID as a UUID string
     */
    public String getMovieId() {
        return movieId;
    }

    /**
     * Returns the scheduled showtime of the booked movie session.
     *
     * @return the showtime as a {@link LocalDateTime}
     */
    public LocalDateTime getShowtime() {
        return showtime;
    }

    /**
     * Returns the set of seat IDs selected by the user for this booking.
     *
     * @return a {@link HashSet} of seat ID strings (e.g. {@code "A1"}, {@code "B3"})
     */
    public HashSet<String> getChosenSeats() {
        return chosenSeats;
    }

    /**
     * Returns the total price charged for this booking.
     *
     * @return the total price in dollars
     */
    public int getTotalPrice() {
        return totalPrice;
    }

    /**
     * Returns whether this booking has been persisted to {@code BOOKINGS.csv}.
     *
     * @return {@code true} if the booking has been written to disk,
     *         {@code false} if it exists only in memory
     */
    public boolean isProcessed() {
        return isProcessed;
    }

    /**
     * Sets the processed flag for this booking, indicating whether it
     * has been written to {@code BOOKINGS.csv}.
     *
     * @param processed {@code true} to mark as persisted, {@code false} otherwise
     */
    public void setProcessed(boolean processed) {
        isProcessed = processed;
    }
}