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
 * File Name:       BookingRepository.java
 * Developer:       Tran Phan Anh*, Nguyen The Khoi Nguyen*, Nguyen Dinh Quy*,
 *                  Mai Dinh Vinh* (* equal contributions)
 * Description:     In-memory repository for all booking records belonging to
 *                  the currently logged-in user, providing methods to add,
 *                  search, and calculate pricing for bookings during a session.
 ******************************************************************************/

package com.ducksabervn.projects.ketchup.backend.repositories;

import com.ducksabervn.projects.ketchup.backend.model.Booking;
import com.ducksabervn.projects.ketchup.backend.model.Movie;

import java.time.LocalDateTime;
import java.util.*;

/**
 * Static in-memory repository that holds all {@link Booking} records for the
 * currently logged-in user. Uses a {@link LinkedHashMap} to preserve insertion
 * order, which is important for consistent display in the booking history table.
 * The repository is populated on login and flushed to disk on logout or exit
 * via {@link com.ducksabervn.projects.ketchup.backend.io.BookingCsvIO}.
 */
public class BookingRepository {

    /**
     * The in-memory store of bookings for the current user session,
     * keyed by booking ID. A {@link LinkedHashMap} is used to maintain
     * the order in which bookings were inserted.
     */
    private static LinkedHashMap<String, Booking> bookings;

    /**
     * Returns the entire in-memory booking map for the current session.
     *
     * @return a {@link LinkedHashMap} mapping booking ID → {@link Booking}
     */
    public static LinkedHashMap<String, Booking> getBookings() {
        return bookings;
    }

    /**
     * Replaces the current in-memory booking store with the given map.
     * Called after login to load the current user's bookings from the CSV file.
     *
     * @param movies the {@link LinkedHashMap} of booking ID → {@link Booking}
     *               to set as the active booking store
     */
    public static void setBookings(LinkedHashMap<String, Booking> movies) {
        BookingRepository.bookings = movies;
    }

    /**
     * Creates a new {@link Booking} from the supplied parameters, stores it
     * in the in-memory repository, and returns the created instance.
     * The booking ID is automatically generated as a UUID, and the showtime
     * string is parsed using {@link Movie#getDatetimeFormat()}.
     * The new booking is marked as unprocessed ({@code isProcessed = false})
     * so that it will be written to disk on the next save operation.
     *
     * @param email         the email address of the user making the booking
     * @param movieId       the ID of the movie being booked
     * @param showtime      the showtime string in {@code yyyy-MM-dd HH:mm} format
     * @param selectedSeats the set of seat IDs chosen by the user
     * @param totalPrice    the total price for all selected seats
     * @return the newly created and stored {@link Booking} instance
     */
    public static Booking addBooking(String email,
                                     String movieId,
                                     String showtime,
                                     HashSet<String> selectedSeats,
                                     int totalPrice) {
        String bookingId = UUID.randomUUID().toString();
        LocalDateTime st = LocalDateTime.parse(showtime, Movie.getDatetimeFormat());
        Booking b = new Booking(email,
                bookingId, movieId, st, selectedSeats, totalPrice, false);
        BookingRepository.bookings.put(bookingId, b);
        return b;
    }

    /**
     * Searches the in-memory booking store for records matching the given
     * information string. If the string exactly matches a booking ID, that
     * single booking is returned. Otherwise, all bookings whose associated
     * movie title, showtime string, or total price string match the query
     * are returned.
     *
     * @param information the search query; may be a booking ID, movie title,
     *                    showtime in {@code yyyy-MM-dd HH:mm} format,
     *                    or a total price value
     * @return an {@link ArrayList} of {@link Booking} objects matching
     *         the search query; empty if no matches are found
     */
    public static ArrayList<Booking> searchBookings(String information) {
        ArrayList<Booking> arr = new ArrayList<>();
        if (BookingRepository.bookings.containsKey(information)) {
            arr.add(BookingRepository.bookings.get(information));
        } else {
            for (Booking b : BookingRepository.bookings.values()) {
                if (MovieRepository.getMovies().get(b.getMovieId()).getTitle().equals(information) ||
                        b.getShowtime().format(Movie.getDatetimeFormat()).equals(information) ||
                        Integer.toString(b.getTotalPrice()).equals(information)) {
                    arr.add(b);
                }
            }
        }
        return arr;
    }

    /**
     * Calculates the total booking price based on the number of chosen seats
     * and the per-seat price of the movie.
     *
     * @param chosenSeats  the set of seat IDs selected by the user
     * @param pricePerSeat the price in dollars charged per seat
     * @return the total price as the product of seat count and per-seat price
     */
    public static int calculateTotalPrice(HashSet<String> chosenSeats, int pricePerSeat) {
        return chosenSeats.size() * pricePerSeat;
    }
}