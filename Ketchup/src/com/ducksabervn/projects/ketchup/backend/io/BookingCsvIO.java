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
 * File Name:       BookingCsvIO.java
 * Developer:       Tran Phan Anh*, Nguyen The Khoi Nguyen*, Nguyen Dinh Quy*,
 *                  Mai Dinh Vinh* (* equal contributions)
 * Description:     Handles all CSV read and write operations for booking data,
 *                  including user-scoped reads and append-only persistence of
 *                  new unprocessed bookings.
 ******************************************************************************/

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

/**
 * Singleton class responsible for reading and writing booking data to
 * {@code BOOKINGS.csv}. Implements {@link FilteredCsvIO} to support
 * user-scoped reads, ensuring each user only loads their own booking records.
 */
public class BookingCsvIO implements FilteredCsvIO<String, Booking> {

    /**
     * The sole instance of {@code BookingCsvIO}, lazily initialized.
     */
    private static BookingCsvIO IO;

    /**
     * Private constructor to enforce the singleton pattern.
     */
    private BookingCsvIO() {
    }

    /**
     * Returns the singleton instance of {@code BookingCsvIO},
     * creating it on first call.
     *
     * @return the singleton {@code BookingCsvIO} instance
     */
    public static BookingCsvIO getIO() {
        if (BookingCsvIO.IO == null) {
            BookingCsvIO.IO = new BookingCsvIO();
        }
        return BookingCsvIO.IO;
    }

    /**
     * Reads all booking records from the CSV file that belong to the specified user.
     *
     * @param requiredInformation the email address of the user whose bookings to load
     * @return a {@link LinkedHashMap} mapping booking ID to {@link Booking},
     *         containing only records belonging to the given user
     * @throws IOException if the CSV file cannot be read
     */
    @Override
    public LinkedHashMap<String, Booking> readCsvFile(String requiredInformation) throws IOException {
        return this.readBookingCsv(requiredInformation);
    }

    /**
     * Persists all unprocessed bookings in memory to {@code BOOKINGS.csv}
     * by appending them to the file and marking each as processed.
     *
     * @throws IOException if the CSV file cannot be written to
     */
    @Override
    public void updateLatestData() throws IOException {
        this.updateBookingCsv();
    }

    /**
     * Reads {@code BOOKINGS.csv} and returns only the records whose email
     * field matches {@code userEmail}. This enforces data privacy so that
     * a user cannot access another user's booking history.
     *
     * @param userEmail the email address used to filter booking records
     * @return a {@link LinkedHashMap} of booking ID → {@link Booking} for the given user
     * @throws IOException if the CSV file cannot be read
     */
    private LinkedHashMap<String, Booking> readBookingCsv(String userEmail) throws IOException {
        List<String> allBookings = Files.readAllLines(AppPath.BOOKINGS.getAppPath());
        allBookings.remove(0);
        LinkedHashMap<String, Booking> bookings = new LinkedHashMap<>();
        for (String b : allBookings) {
            String[] split = b.split(";");
            if (split[0].equals(userEmail)) {
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

    /**
     * Appends all bookings currently held in {@link BookingRepository} that have
     * not yet been written to disk (i.e. {@code isProcessed == false}) to
     * {@code BOOKINGS.csv}. Each written booking is then marked as processed
     * to prevent duplicate entries on subsequent saves.
     *
     * @throws IOException if the CSV file cannot be opened or written to
     */
    private void updateBookingCsv() throws IOException {
        LinkedHashMap<String, Booking> allBookings = BookingRepository.getBookings();
        try (BufferedWriter bw3 = new BufferedWriter(
                new FileWriter(AppPath.BOOKINGS.getAppPath().toFile(), true))) {
            for (Booking b : allBookings.values()) {
                if (!b.isProcessed()) {
                    b.setProcessed(true);
                    bw3.newLine();
                    bw3.write(generateDataAsString(b));
                }
            }
        } catch (IOException e) {
            throw e;
        }
    }

    /**
     * Serializes a {@link Booking} object into a semicolon-delimited CSV row string.
     * The format is: {@code EMAIL;BOOKINGID;MOVIEID;SHOWTIME;SEATS;TOTAL_PRICE;IS_PROCESSED}
     *
     * @param b the {@link Booking} to serialize
     * @return a formatted CSV row representing the given booking
     */
    public static String generateDataAsString(Booking b) {
        String selectedSeatIds = b.getChosenSeats().stream()
                .collect(Collectors.joining(","));
        return "%s;%s;%s;%s;%s;%d;%b".formatted(
                b.getBookingEmail(),
                b.getBookingId(),
                b.getMovieId(),
                b.getShowtime().format(Movie.getDatetimeFormat()),
                selectedSeatIds,
                b.getTotalPrice(),
                b.isProcessed());
    }
}