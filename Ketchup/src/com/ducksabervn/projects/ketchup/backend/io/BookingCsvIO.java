package com.ducksabervn.projects.ketchup.backend.io;

import com.ducksabervn.projects.ketchup.backend.model.Booking;
import com.ducksabervn.projects.ketchup.backend.repositories.BookingRepository;
import com.ducksabervn.projects.ketchup.backend.model.Movie;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class BookingCsvIO implements FilteredCsvIO<String, Booking>{

    private static BookingCsvIO IO;

    private BookingCsvIO(){
    }

    public static BookingCsvIO getIO(){
        if(BookingCsvIO.IO == null){
            BookingCsvIO.IO = new BookingCsvIO();
        }
        return BookingCsvIO.IO;
    }

    @Override
    public LinkedHashMap<String, Booking> readCsvFile(String requiredInformation) throws IOException{
        return this.readBookingCsv(requiredInformation);
    }

    @Override
    public void updateLatestData() throws IOException{
        this.updateBookingCsv();
    }

    //why we need userEmail ? Because normal/admin user can't see other user's private data
    private LinkedHashMap<String, Booking> readBookingCsv(String userEmail) throws IOException{
        List<String> allBookings = Files.readAllLines(AppPath.BOOKINGS.getAppPath());
        allBookings.remove(0);
        LinkedHashMap<String, Booking> bookings = new LinkedHashMap<>();
        for(String b: allBookings){
            String[] split = b.split(";");
            if(split[0].equals(userEmail)){
                LocalDateTime showtime = LocalDateTime.parse(split[3], Movie.getDatetimeFormat());
                HashSet<String> chosenSeats = (split[4] == null || split[4].isBlank())
                        ? new HashSet<>()
                        : new HashSet<>(Arrays.asList(split[4].split(",")));
                int totalPrice = Integer.parseInt(split[5]);
                boolean isProcessed = Boolean.parseBoolean(split[6]);
                bookings.put(split[1], new Booking(split[0],
                        split[1], split[2], showtime, chosenSeats, totalPrice, isProcessed));
            }
        }
        return bookings;
    }

    private void updateBookingCsv() throws IOException{
        LinkedHashMap<String, Booking> allBookings = BookingRepository.getBookings();
        try(BufferedWriter bw3 = new BufferedWriter(new FileWriter(AppPath.BOOKINGS.getAppPath().toFile(), true))){
            for(Booking b: allBookings.values()){
                if(!b.isProcessed()){
                    b.setProcessed(true);
                    bw3.newLine();
                    bw3.write(generateDataAsString(b));
                }
            }
        }catch(IOException e){
            throw e;
        }
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
