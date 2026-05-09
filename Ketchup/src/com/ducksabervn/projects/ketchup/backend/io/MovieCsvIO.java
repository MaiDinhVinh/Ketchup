package com.ducksabervn.projects.ketchup.backend.io;

import com.ducksabervn.projects.ketchup.backend.movie.Movie;
import com.ducksabervn.projects.ketchup.backend.movie.MovieRepository;
import com.ducksabervn.projects.ketchup.backend.ui.DisplayMessage;
import com.ducksabervn.projects.ketchup.backend.booking.Booking;
import com.ducksabervn.projects.ketchup.backend.booking.BookingRepository;
import com.ducksabervn.projects.ketchup.frontend.AdminMovieListUI;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.stream.Collectors;

public class MovieCsvIO implements CsvIO<String, Movie>{

    private static MovieCsvIO IO;

    private MovieCsvIO(){
    }

    public static MovieCsvIO getIO(){
        if(MovieCsvIO.IO == null){
            MovieCsvIO.IO = new MovieCsvIO();
        }
        return MovieCsvIO.IO;
    }

    @Override
    public LinkedHashMap<String, Movie> readCsvFile() {
        return this.readMoviesCsv();
    }

    @Override
    public void updateLatestData() {
        this.updateMovieCsv();
    }

    public void writeMovieData(String data){
        //adding true to FileWriter will turn on append mode and won't override the CSV file
        //fuck ass i forgor this shit for 3 times
        try(BufferedWriter bw = new BufferedWriter(new FileWriter(FileSystemInitializer.getMOVIES().toFile(), true))){
            bw.newLine();
            bw.write(data);
        }catch(IOException e){
            //I still cant figure out for which JFrame will responsible to display the
            //exception string, but this will work as a fallback for now
            DisplayMessage.displayError(AdminMovieListUI.getAdminMovieListUI().getMainFrame(),
                    e.getMessage());
        }
    }

    private LinkedHashMap<String, Movie> readMoviesCsv(){
        try{
            List<String> allMovies = Files.readAllLines(FileSystemInitializer.getMOVIES());
            allMovies.remove(0);
            LinkedHashMap<String, Movie> movies = new LinkedHashMap<>();
            for(String str: allMovies){
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
        }catch(IOException e){
            //I still cant figure out for which JFrame will responsible to display the
            //exception string, but this will work as a fallback for now
            DisplayMessage.displayError(AdminMovieListUI.getAdminMovieListUI().getMainFrame(),
                    e.getMessage());
            return null;
        }
    }

    private void updateMovieCsv(){
        try(BufferedWriter bw = new BufferedWriter(new FileWriter(FileSystemInitializer.getMOVIES().toFile()));){
            bw.write("MOVIEID;TITLE;GENRE;DURATION;RATING;SHOWTIME;SEAT;SPRICE");
            if(BookingRepository.getBookings() != null) {
                for (Booking b : BookingRepository.getBookings().values()) {
                    if (!b.isProcessed()) {
                        Movie m = MovieRepository.getMovies().get(b.getMovieId());
                        if (m != null) {
                            m.getOccupiedSeat().addAll(b.getChosenSeats());
                        }
                    }
                }
            }
            for(Movie m: MovieRepository.getMovies().values()){
                bw.newLine();
                bw.write(generateMovieDataAsString(m));
            }
        }catch(IOException e){
            //I still cant figure out for which JFrame will responsible to display the
            //exception string, but this will work as a fallback for now
            DisplayMessage.displayError(AdminMovieListUI.getAdminMovieListUI().getMainFrame(),
                    e.getMessage());
        }
    }

    public static String generateMovieDataAsString(Movie m){
        String occupiedSeats = m.getOccupiedSeat().stream().
                collect(Collectors.joining(","));
        String data = "%s;%s;%s;%d;%s;%s;%s;%d".formatted(m.getMovieId(),
                m.getTitle(),
                m.getGenre(),
                m.getDuration(),
                m.getRating(),
                m.getShowTime().format(Movie.getDatetimeFormat()),
                occupiedSeats,
                m.getSeatPrice());
        return data;
    }
}
