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
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

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
    public LinkedHashMap<String, Movie> readCsvFile(String requiredInformation) {
        throw new UnsupportedOperationException("Unsupported Operation");
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
        LinkedHashMap<String, Movie> updatedMovies = MovieRepository.getMovies();
        LinkedHashMap<String, Booking> allBookings = BookingRepository.getBookings();
        try(BufferedWriter bw = new BufferedWriter(new FileWriter(FileSystemInitializer.getMOVIES().toFile()));){
            bw.write("MOVIEID;TITLE;GENRE;DURATION;RATING;SHOWTIME;SEAT;SPRICE");
            if(allBookings != null){
                ArrayList<Booking> bookings = new ArrayList<>(allBookings.values());
                ArrayList<Movie> allMovies = new ArrayList<>(updatedMovies.values());
                int bookingCount = bookings.size();
                int movieCount = allMovies.size();
                for(int i = 0; i < bookingCount; i++){
                    for(int j = 0; j < movieCount; j++){
                        if(bookings.get(i).getMovieId().equals(allMovies.get(j).getMovieId()) &&
                                !bookings.get(i).isProcessed()){
                            Movie m = allMovies.get(j);
                            Booking b = bookings.get(i);
                            m.getOccupiedSeat().addAll(b.getChosenSeats());
                            allMovies.set(j, m);
                        }
                    }
                }
                for(Movie m: allMovies){
                    bw.newLine();
                    bw.write(MovieRepository.generateMovieDataAsString(m));
                }
            }else{
                for(Movie m: updatedMovies.values()){
                    bw.newLine();
                    bw.write(MovieRepository.generateMovieDataAsString(m));
                }
            }
        }catch(IOException e){
            //I still cant figure out for which JFrame will responsible to display the
            //exception string, but this will work as a fallback for now
            DisplayMessage.displayError(AdminMovieListUI.getAdminMovieListUI().getMainFrame(),
                    e.getMessage());
        }
    }
}
