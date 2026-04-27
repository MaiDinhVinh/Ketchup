package com.ducksabervn.projects.ketchup.backend.user;

import com.ducksabervn.projects.ketchup.backend.admin.Movie;
import com.ducksabervn.projects.ketchup.backend.admin.MovieRepository;
import com.ducksabervn.projects.ketchup.backend.helper.ReadCSVFile;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class BookingRepository {
    //this motherfucker will maintain the insert input unlike HashMap, bro i want to kms
    private static LinkedHashMap<String, Booking> bookings;

    public static LinkedHashMap<String, Booking> getBookings() {
        return bookings;
    }

    public static void setBookings(LinkedHashMap<String, Booking> movies) {
        BookingRepository.bookings = movies;
    }

    public static Booking addBooking(String email,
                                     String movieId,
                                     String showtime,
                                     List<String> selectedSeats,
                                     int totalPrice) {
        String bookingId = UUID.randomUUID().toString();
        LocalDateTime st = LocalDateTime.parse(showtime, Movie.getDatetimeFormat());
        Booking b = new Booking(email,
                bookingId, movieId, st, new ArrayList<>(selectedSeats), totalPrice, false);
        BookingRepository.bookings.put(bookingId, b);
        return b;
    }

    public static ArrayList<Booking> searchBookings(String information){
        ArrayList<Booking> arr = new ArrayList<>();
        if(BookingRepository.bookings.containsKey(information)){
            arr.add(BookingRepository.bookings.get(information)); //searching by id
        }else{
            for(Booking b: BookingRepository.bookings.values()){
                if(MovieRepository.getMovies().get(b.getMovieId()).getTitle().equals(information) ||
                b.getShowtime().format(Movie.getDatetimeFormat()).equals(information) ||
                Integer.toString(b.getTotalPrice()).equals(information)){
                    arr.add(b);
                }
            }
        }
        return arr;
    }

    public static String generateDataAsString(Booking b){
        String selectedSeatIds = b.getChosenSeats().stream().
                collect(Collectors.joining(","));
        String data = "%s;%s;%s;%s;%s;%d;%b".formatted(b.getBookingEmail(),
                b.getBookingId(), b.getMovieId(), b.getShowtime().format(Movie.getDatetimeFormat()),
                selectedSeatIds, b.getTotalPrice(), b.isProcessed());
        return data;
    }
}