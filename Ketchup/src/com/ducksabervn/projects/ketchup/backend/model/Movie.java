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
 * File Name:       Movie.java
 * Developer:       Tran Phan Anh*, Nguyen The Khoi Nguyen*, Nguyen Dinh Quy*,
 *                  Mai Dinh Vinh* (* equal contributions)
 * Description:     Model class representing a movie screening entry, storing
 *                  all relevant details including title, genre, duration,
 *                  rating, showtime, occupied seats, and seat pricing.
 ******************************************************************************/

package com.ducksabervn.projects.ketchup.backend.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.HashSet;

/**
 * Represents a single movie screening entry in the Ketchup application.
 * Each instance corresponds to one showtime of a movie, tracking which
 * seats have already been booked and the price per seat. The shared
 * {@link DateTimeFormatter} is used consistently across all layers of
 * the application for parsing and formatting showtime values.
 */
public class Movie {

    /**
     * The unique identifier for this movie screening, generated as a UUID string.
     */
    private String movieId;

    /**
     * The title of the movie (e.g. {@code "Inception"}).
     */
    private String title;

    /**
     * The genre of the movie (e.g. {@code "Sci-Fi"}, {@code "Action"}).
     */
    private String genre;

    /**
     * The runtime of the movie in minutes.
     */
    private int duration;

    /**
     * The age rating of the movie (e.g. {@code "G"}, {@code "PG-13"}, {@code "R"}).
     */
    private String rating;

    /**
     * The scheduled date and time of this movie's screening.
     */
    private LocalDateTime showTime;

    /**
     * The set of seat IDs (e.g. {@code "A1"}, {@code "B3"}) that have already
     * been booked for this screening. Uses a {@link HashSet} to guarantee
     * uniqueness and provide constant-time membership checks.
     */
    private HashSet<String> occupiedSeat;

    /**
     * The price in dollars charged per seat for this screening.
     */
    private int seatPrice;

    /**
     * The shared {@link DateTimeFormatter} used across the entire application
     * to parse and format showtime values in {@code yyyy-MM-dd HH:mm} format.
     */
    private static final DateTimeFormatter DATETIME_FORMAT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    /**
     * Constructs a new {@code Movie} from raw string data, as typically read
     * from a CSV row. The {@code showTime} string is parsed using
     * {@link #DATETIME_FORMAT}, and {@code occupiedSeat} is split on commas
     * into a {@link HashSet}. An empty or blank {@code occupiedSeat} string
     * results in an empty seat set.
     *
     * @param movieId      the unique UUID string identifying this screening
     * @param title        the title of the movie
     * @param genre        the genre of the movie
     * @param duration     the runtime of the movie in minutes
     * @param rating       the age rating of the movie
     * @param showTime     the showtime string in {@code yyyy-MM-dd HH:mm} format
     * @param occupiedSeat a comma-separated string of already-booked seat IDs,
     *                     or an empty/blank string if no seats are booked
     * @param seatPrice    the price in dollars per seat for this screening
     */
    public Movie(String movieId,
                 String title,
                 String genre,
                 int duration,
                 String rating,
                 String showTime,
                 String occupiedSeat,
                 int seatPrice) {
        this.movieId = movieId;
        this.title = title;
        this.genre = genre;
        this.duration = duration;
        this.rating = rating;
        this.showTime = LocalDateTime.parse(showTime, DATETIME_FORMAT);
        this.seatPrice = seatPrice;
        if (occupiedSeat == null || occupiedSeat.isBlank()) {
            this.occupiedSeat = new HashSet<>();
        } else {
            this.occupiedSeat = new HashSet<>(Arrays.asList(occupiedSeat.split(",")));
        }
    }

    /**
     * Returns the unique identifier of this movie screening.
     *
     * @return the movie ID as a UUID string
     */
    public String getMovieId() {
        return movieId;
    }

    /**
     * Returns the runtime of this movie in minutes.
     *
     * @return the duration in minutes
     */
    public int getDuration() {
        return duration;
    }

    /**
     * Returns the set of seat IDs that have already been booked for this screening.
     * The returned set is live; callers may add seat IDs directly to it.
     *
     * @return a {@link HashSet} of occupied seat ID strings
     */
    public HashSet<String> getOccupiedSeat() {
        return occupiedSeat;
    }

    /**
     * Returns the price per seat for this screening.
     *
     * @return the seat price in dollars
     */
    public int getSeatPrice() {
        return seatPrice;
    }

    /**
     * Returns the genre of this movie.
     *
     * @return the genre string (e.g. {@code "Sci-Fi"})
     */
    public String getGenre() {
        return genre;
    }

    /**
     * Returns the age rating of this movie.
     *
     * @return the rating string (e.g. {@code "PG-13"})
     */
    public String getRating() {
        return rating;
    }

    /**
     * Returns the scheduled showtime of this screening.
     *
     * @return the showtime as a {@link LocalDateTime}
     */
    public LocalDateTime getShowTime() {
        return showTime;
    }

    /**
     * Returns the title of this movie.
     *
     * @return the movie title string
     */
    public String getTitle() {
        return title;
    }

    /**
     * Returns a string representation of this {@code Movie} object,
     * listing all field values. Intended for debugging purposes.
     *
     * @return a formatted string containing all movie fields
     */
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

    /**
     * Returns the shared {@link DateTimeFormatter} used throughout the application
     * for parsing and formatting showtime values in {@code yyyy-MM-dd HH:mm} format.
     *
     * @return the application-wide datetime formatter
     */
    public static DateTimeFormatter getDatetimeFormat() {
        return DATETIME_FORMAT;
    }
}