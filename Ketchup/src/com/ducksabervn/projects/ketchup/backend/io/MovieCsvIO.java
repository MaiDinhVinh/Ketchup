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
 * File Name:       MovieCsvIO.java
 * Developer:       Tran Phan Anh*, Nguyen The Khoi Nguyen*, Nguyen Dinh Quy*,
 *                  Mai Dinh Vinh* (* equal contributions)
 * Description:     Handles all CSV read and write operations for movie data,
 *                  including full file reads on startup, single-record appends
 *                  when a new movie is added, and full file overwrites on
 *                  logout or exit to reflect occupied seat changes.
 ******************************************************************************/

package com.ducksabervn.projects.ketchup.backend.io;

import com.ducksabervn.projects.ketchup.backend.model.Movie;
import com.ducksabervn.projects.ketchup.backend.repositories.MovieRepository;
import com.ducksabervn.projects.ketchup.backend.model.Booking;
import com.ducksabervn.projects.ketchup.backend.repositories.BookingRepository;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Singleton class responsible for reading and writing movie data to
 * {@code MOVIES.csv}. Implements {@link CsvIO} to support full file
 * reads on startup and full file overwrites on save. Also provides an
 * append-mode write for when a single new movie is added at runtime.
 */
public class MovieCsvIO implements CsvIO<String, Movie> {

    /**
     * The sole instance of {@code MovieCsvIO}, lazily initialized.
     */
    private static MovieCsvIO IO;

    /**
     * Private constructor to enforce the singleton pattern.
     */
    private MovieCsvIO() {
    }

    /**
     * Returns the singleton instance of {@code MovieCsvIO},
     * creating it on first call.
     *
     * @return the singleton {@code MovieCsvIO} instance
     */
    public static MovieCsvIO getIO() {
        if (MovieCsvIO.IO == null) {
            MovieCsvIO.IO = new MovieCsvIO();
        }
        return MovieCsvIO.IO;
    }

    /**
     * Reads all movie records from {@code MOVIES.csv} and returns them
     * as a map keyed by movie ID.
     *
     * @return a {@link LinkedHashMap} mapping movie ID → {@link Movie}
     *         for every record in the file
     * @throws IOException if the CSV file cannot be read
     */
    @Override
    public LinkedHashMap<String, Movie> readCsvFile() throws IOException {
        return this.readMoviesCsv();
    }

    /**
     * Overwrites {@code MOVIES.csv} with the current state of all movies
     * held in {@link MovieRepository}, merging in any unprocessed booking
     * seat data before writing.
     *
     * @throws IOException if the CSV file cannot be written to
     */
    @Override
    public void updateLatestData() throws IOException {
        this.updateMovieCsv();
    }

    /**
     * Appends a single pre-formatted movie CSV row to {@code MOVIES.csv}
     * without overwriting existing records. Used when a new movie is added
     * at runtime so that the full file rewrite can be deferred until logout
     * or exit.
     *
     * @param data the semicolon-delimited CSV row string to append
     * @throws IOException if the CSV file cannot be opened or written to
     */
    public void writeMovieData(String data) throws IOException {
        try (BufferedWriter bw = new BufferedWriter(
                new FileWriter(AppPath.MOVIES.getAppPath().toFile(), true))) {
            bw.newLine();
            bw.write(data);
        } catch (IOException e) {
            throw e;
        }
    }

    /**
     * Parses {@code MOVIES.csv} line by line, skipping the header row,
     * and constructs a {@link Movie} object for each record.
     *
     * @return a {@link LinkedHashMap} of movie ID → {@link Movie}
     *         preserving the order in which movies appear in the file
     * @throws IOException if the CSV file cannot be read
     */
    private LinkedHashMap<String, Movie> readMoviesCsv() throws IOException {
        List<String> allMovies = Files.readAllLines(AppPath.MOVIES.getAppPath());
        allMovies.remove(0);
        LinkedHashMap<String, Movie> movies = new LinkedHashMap<>();
        for (String str : allMovies) {
            String[] split = str.split(";");
            movies.put(split[0],
                    new Movie(split[0], split[1],
                            split[2],
                            Integer.parseInt(split[3]),
                            split[4],
                            split[5],
                            split[6],
                            Integer.parseInt(split[7])));
        }
        return movies;
    }

    /**
     * Rewrites {@code MOVIES.csv} from scratch using the current contents
     * of {@link MovieRepository}. Before writing, iterates over all
     * unprocessed bookings in {@link BookingRepository} and merges their
     * chosen seats into the corresponding movie's occupied seat set, so
     * that seat availability is accurately reflected in the output file.
     *
     * @throws IOException if the CSV file cannot be opened or written to
     */
    private void updateMovieCsv() throws IOException {
        try (BufferedWriter bw = new BufferedWriter(
                new FileWriter(AppPath.MOVIES.getAppPath().toFile()))) {
            bw.write("MOVIEID;TITLE;GENRE;DURATION;RATING;SHOWTIME;SEAT;SPRICE");
            if (BookingRepository.getBookings() != null) {
                for (Booking b : BookingRepository.getBookings().values()) {
                    if (!b.isProcessed()) {
                        Movie m = MovieRepository.getMovies().get(b.getMovieId());
                        if (m != null) {
                            m.getOccupiedSeat().addAll(b.getChosenSeats());
                        }
                    }
                }
            }
            for (Movie m : MovieRepository.getMovies().values()) {
                bw.newLine();
                bw.write(generateMovieDataAsString(m));
            }
        } catch (IOException e) {
            throw e;
        }
    }

    /**
     * Serializes a {@link Movie} object into a semicolon-delimited CSV row string.
     * The format is: {@code MOVIEID;TITLE;GENRE;DURATION;RATING;SHOWTIME;SEATS;SPRICE}
     *
     * @param m the {@link Movie} to serialize
     * @return a formatted CSV row representing the given movie
     */
    public static String generateMovieDataAsString(Movie m) {
        String occupiedSeats = m.getOccupiedSeat().stream()
                .collect(Collectors.joining(","));
        return "%s;%s;%s;%d;%s;%s;%s;%d".formatted(
                m.getMovieId(),
                m.getTitle(),
                m.getGenre(),
                m.getDuration(),
                m.getRating(),
                m.getShowTime().format(Movie.getDatetimeFormat()),
                occupiedSeats,
                m.getSeatPrice());
    }
}