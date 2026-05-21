/******************************************************************************
 * Project Name:    Ketchup - A movie management system
 * Course:          COMP1020 - OOP and Data Structure
 * Semester:        Spring 2026
 *
 * Members: Tran Phan Anh <25anh.tp@vinuni.edu.vn>,
 *          Nguyen Trong Khoi Nguyen <25nguyen.ntk@vinuni.edu.vn>,
 *          Nguyen Dinh Quy <25quy.nd@vinuni.edu.vn>,
 *          Hoang Duc Phat <25phat.hd@vinuni.edu.vn>,
 *          Mai Dinh Vinh <25vinh.md@vinuni.edu.vn>
 *
 * File Name:       CustomerHomeUIController.java
 * Developer:       Tran Phan Anh*, Nguyen Trong Khoi Nguyen*, Nguyen Dinh Quy*,
 *                  Mai Dinh Vinh* (* equal contributions)
 * Description:     JavaFX controller for CustomerHomeUI.fxml. Preserves all
 *                  original logic from the Swing CustomerHomeUI class including
 *                  two-tab layout, movie/booking search and sort, View Details,
 *                  Book Now, logout, and window-close with CSV persistence.
 *                  Migrated from Swing JFrame/JTabbedPane/SwingWorker to
 *                  JavaFX Stage/TabPane/Task.
 ******************************************************************************/

package com.ducksabervn.projects.ketchup.frontend;

import com.ducksabervn.projects.ketchup.backend.io.BookingCsvIO;
import com.ducksabervn.projects.ketchup.backend.io.CredentialCsvIO;
import com.ducksabervn.projects.ketchup.backend.io.MovieCsvIO;
import com.ducksabervn.projects.ketchup.backend.model.Booking;
import com.ducksabervn.projects.ketchup.backend.model.Movie;
import com.ducksabervn.projects.ketchup.backend.repositories.BookingRepository;
import com.ducksabervn.projects.ketchup.backend.repositories.MovieRepository;
import com.ducksabervn.projects.ketchup.frontend.util.DisplayMessage;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXComboBox;
import io.github.palexdev.materialfx.controls.MFXTextField;
import io.github.palexdev.materialfx.enums.FloatMode;
import javafx.application.Platform;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.IOException;
import java.net.URL;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

/**
 * Controller for the Customer Home screen (CustomerHomeUI.fxml).
 *
 * Provides two tabs:
 *   Movies  - searchable/sortable list of unbooked screenings, with
 *             View Details and Book Now actions.
 *   My Bookings - searchable/sortable personal booking history.
 *
 * Exposes static helpers removeMovie and addBookingRow so that
 * CustomerBookingConfirmUIController can update both tables after a booking.
 */
public class CustomerHomeUIController implements Initializable {

    private static final DateTimeFormatter DT_FMT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    // Static singleton reference
    private static CustomerHomeUIController instance;

    // Runtime state
    private String currentEmail;
    private final ObservableList<Movie>   movieData   = FXCollections.observableArrayList();
    private final ObservableList<Booking> bookingData = FXCollections.observableArrayList();

    // Movie sort comparators (same as original Swing class)
    private static final Comparator<Movie> SORT_MOVIE_BY_TITLE    = Comparator.comparing(Movie::getTitle);
    private static final Comparator<Movie> SORT_MOVIE_BY_GENRE    = Comparator.comparing(Movie::getGenre);
    private static final Comparator<Movie> SORT_MOVIE_BY_DURATION = Comparator.comparing(Movie::getDuration);
    private static final Comparator<Movie> SORT_MOVIE_BY_SHOWTIME = Comparator.comparing(Movie::getShowTime);

    // Booking sort comparators (same as original Swing class)
    private static final Comparator<Booking> SORT_BOOKING_BY_ID =
            Comparator.comparing(Booking::getBookingId);
    private static final Comparator<Booking> SORT_BOOKING_BY_MOVIE =
            Comparator.comparing(
                    (Booking b) -> MovieRepository.getMovies().get(b.getMovieId()).getTitle());
    private static final Comparator<Booking> SORT_BOOKING_BY_SHOWTIME =
            Comparator.comparing(Booking::getShowtime);
    private static final Comparator<Booking> SORT_BOOKING_BY_PRICE =
            Comparator.comparing(Booking::getTotalPrice);

    // FXML fields - navbar
    @FXML private Label       welcomeLabel;
    @FXML private MFXButton   logoutButton;

    // FXML fields - Movies tab
    @FXML private MFXTextField        movieSearchField;
    @FXML private MFXButton           movieSearchButton;
    @FXML private MFXComboBox<String> movieSortComboBox;
    @FXML private MFXButton           movieSortButton;
    @FXML private MFXButton           movieRefreshButton;

    @FXML private TableView<Movie>       movieTable;
    @FXML private TableColumn<Movie, String>  movieColTitle;
    @FXML private TableColumn<Movie, String>  movieColGenre;
    @FXML private TableColumn<Movie, Integer> movieColDuration;
    @FXML private TableColumn<Movie, String>  movieColShowtime;

    @FXML private MFXButton viewDetailsButton;
    @FXML private MFXButton bookNowButton;

    // FXML fields - My Bookings tab
    @FXML private MFXTextField        bookingSearchField;
    @FXML private MFXButton           bookingSearchButton;
    @FXML private MFXComboBox<String> bookingSortComboBox;
    @FXML private MFXButton           bookingSortButton;
    @FXML private MFXButton           bookingRefreshButton;

    @FXML private TableView<Booking>        bookingTable;
    @FXML private TableColumn<Booking, String>  bookingColId;
    @FXML private TableColumn<Booking, String>  bookingColMovie;
    @FXML private TableColumn<Booking, String>  bookingColShowtime;
    @FXML private TableColumn<Booking, String>  bookingColSeats;
    @FXML private TableColumn<Booking, Integer> bookingColPrice;

    // Initializable

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        instance = this;
        setupMovieTable();
        setupBookingTable();
        setupCombos();
        movieSearchField.setFloatMode(FloatMode.DISABLED);
        bookingSortComboBox.setFloatMode(FloatMode.DISABLED);
        movieSortComboBox.setFloatMode(FloatMode.DISABLED);
        bookingSearchField.setFloatMode(FloatMode.DISABLED);
    }

    // Entry point

    /**
     * Static factory — loads CustomerHomeUI.fxml, wires the username and email,
     * loads both tables asynchronously, and shows the Stage.
     * Replaces the original CustomerHomeUI.initialize(String, String).
     *
     * @param username display name shown in the navbar
     * @param email    email address used for booking association
     */
    public static void initialize(String username, String email) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    CustomerHomeUIController.class.getResource(
                            "/fxml/CustomerHomeUI.fxml"));
            Parent root = loader.load();

            CustomerHomeUIController controller = loader.getController();
            controller.welcomeLabel.setText("Welcome, " + username + "!");
            controller.currentEmail = email;
            controller.loadMoviesAsync();
            controller.loadBookingHistoryAsync();

            Stage stage = new Stage();
            stage.setTitle("Ketchup");
            stage.setScene(new Scene(root, 1050, 650));
            stage.setResizable(true);
            stage.setOnCloseRequest(event -> controller.handleWindowClose(event));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Static helpers (mirror original static methods)

    /**
     * Removes the movie with the given ID from the movie table.
     * Called by CustomerBookingConfirmUIController after a booking is confirmed.
     * Mirrors original CustomerHomeUI.removeMovie(String).
     *
     * @param movieId the ID of the movie to remove
     */
    public static void removeMovie(String movieId) {
        if (instance != null) {
            Platform.runLater(() ->
                    instance.movieData.removeIf(m -> m.getMovieId().equals(movieId)));
        }
    }

    /**
     * Appends a confirmed Booking to the booking history table.
     * Called by CustomerBookingConfirmUIController after a booking is confirmed.
     * Mirrors original CustomerHomeUI.addBookingRow(Booking).
     *
     * @param b the newly confirmed Booking to add
     */
    public static void addBookingRow(Booking b) {
        if (instance != null) {
            Platform.runLater(() -> instance.bookingData.add(b));
        }
    }

    // Action handlers - Movies tab

    /** Searches movies via MovieRepository.searchMovie and refreshes the table. */
    @FXML
    private void handleMovieSearch() {
        String query = movieSearchField.getText().trim();
        if (query.isEmpty()) {
            loadMoviesAsync();
            return;
        }
        Task<ArrayList<Movie>> task = new Task<>() {
            @Override
            protected ArrayList<Movie> call() {
                HashSet<String> bookedIds = getBookedMovieIds();
                return MovieRepository.searchMovie(query).stream()
                        .filter(m -> !bookedIds.contains(m.getMovieId()))
                        .collect(Collectors.toCollection(ArrayList::new));
            }
        };
        task.setOnSucceeded(e -> movieData.setAll(task.getValue()));
        new Thread(task, "movie-search-thread").start();
    }

    /** Sorts the movie table by the selected criterion. Mirrors original sortMovieByCriterion. */
    @FXML
    private void handleMovieSort() {
        String criterion = movieSortComboBox.getValue();
        if (criterion == null) return;
        switch (criterion) {
            case "Title"         -> FXCollections.sort(movieData, SORT_MOVIE_BY_TITLE);
            case "Genre"         -> FXCollections.sort(movieData, SORT_MOVIE_BY_GENRE);
            case "Duration (min)"-> FXCollections.sort(movieData, SORT_MOVIE_BY_DURATION);
            case "Showtime"      -> FXCollections.sort(movieData, SORT_MOVIE_BY_SHOWTIME);
        }
    }

    /** Reloads all unbooked movies from MovieRepository. */
    @FXML
    private void handleMovieRefresh() {
        loadMoviesAsync();
    }

    /**
     * Opens CustomerMovieDetailUIController for the selected movie.
     * Mirrors original viewDetailsButton listener.
     */
    @FXML
    private void handleViewDetails() {
        Movie selected = movieTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            DisplayMessage.displayWarning("Please select a movie to view details.");
            return;
        }
        CustomerMovieDetailUIController.initialize(selected.getMovieId(), getStage());
    }

    /**
     * Opens CustomerSeatSelectionUIController for the selected movie.
     * Mirrors original bookNowButton listener.
     */
    @FXML
    private void handleBookNow() {
        Movie selected = movieTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            DisplayMessage.displayWarning("Please select a movie to book.");
            return;
        }
        CustomerSeatSelectionUIController.initialize(selected.getMovieId(), currentEmail, getStage());
    }

    // Action handlers - My Bookings tab

    /** Searches bookings by query string and refreshes the booking table. */
    @FXML
    private void handleBookingSearch() {
        String query = bookingSearchField.getText().trim();
        if (query.isEmpty()) {
            loadBookingHistoryAsync();
            return;
        }
        Task<ArrayList<Booking>> task = new Task<>() {
            @Override
            protected ArrayList<Booking> call() {
                return BookingRepository.getBookings().values().stream()
                        .filter(b -> b.getBookingId().equals(query)
                                || MovieRepository.getMovies().get(b.getMovieId())
                                .getTitle().contains(query)
                                || b.getShowtime().format(DT_FMT).equals(query)
                                || String.valueOf(b.getTotalPrice()).equals(query))
                        .collect(Collectors.toCollection(ArrayList::new));
            }
        };
        task.setOnSucceeded(e -> bookingData.setAll(task.getValue()));
        new Thread(task, "booking-search-thread").start();
    }

    /** Sorts the booking table by the selected criterion. Mirrors original sortBookingByCriterion. */
    @FXML
    private void handleBookingSort() {
        String criterion = bookingSortComboBox.getValue();
        if (criterion == null) return;
        switch (criterion) {
            case "Booking ID"  -> FXCollections.sort(bookingData, SORT_BOOKING_BY_ID);
            case "Movie"       -> FXCollections.sort(bookingData, SORT_BOOKING_BY_MOVIE);
            case "Showtime"    -> FXCollections.sort(bookingData, SORT_BOOKING_BY_SHOWTIME);
            case "Total Price" -> FXCollections.sort(bookingData, SORT_BOOKING_BY_PRICE);
        }
    }

    /** Reloads all bookings from BookingRepository. */
    @FXML
    private void handleBookingRefresh() {
        loadBookingHistoryAsync();
    }

    // Logout / window close

    /**
     * Handles Logout button click. Confirms, then saves all 3 CSVs on a
     * background thread before returning to the Login screen.
     * Mirrors original SwingWorker logout logic.
     */
    @FXML
    private void handleLogout() {
        if (!DisplayMessage.displayConfirmationDialog("Are you sure you want to logout?")) {
            return;
        }
        getStage().close();
        saveAndThen(() -> Platform.runLater(LoginUIController::initialize));
    }

    /**
     * Handles the window "X" close button. Saves all 3 CSVs on a background
     * thread then exits. Mirrors original WindowAdapter.windowClosing logic.
     *
     * @param event consumed to prevent premature closure
     */
    private void handleWindowClose(WindowEvent event) {
        event.consume();
        saveAndThen(() -> Platform.runLater(Platform::exit));
    }

    // Private helpers

    /** Wires movie TableView columns to Movie properties. */
    private void setupMovieTable() {
        movieColTitle.setCellValueFactory(
                data -> new SimpleStringProperty(data.getValue().getTitle()));
        movieColGenre.setCellValueFactory(
                data -> new SimpleStringProperty(data.getValue().getGenre()));
        movieColDuration.setCellValueFactory(
                data -> new SimpleIntegerProperty(data.getValue().getDuration()).asObject());
        movieColShowtime.setCellValueFactory(
                data -> new SimpleStringProperty(
                        data.getValue().getShowTime().format(DT_FMT)));
        movieTable.setItems(movieData);
    }

    /** Wires booking TableView columns to Booking properties. */
    private void setupBookingTable() {
        bookingColId.setCellValueFactory(
                data -> new SimpleStringProperty(data.getValue().getBookingId()));
        bookingColMovie.setCellValueFactory(
                data -> new SimpleStringProperty(
                        MovieRepository.getMovies().get(data.getValue().getMovieId()).getTitle()));
        bookingColShowtime.setCellValueFactory(
                data -> new SimpleStringProperty(
                        data.getValue().getShowtime().format(DT_FMT)));
        bookingColSeats.setCellValueFactory(
                data -> new SimpleStringProperty(
                        String.join(", ", data.getValue().getChosenSeats())));
        bookingColPrice.setCellValueFactory(
                data -> new SimpleIntegerProperty(data.getValue().getTotalPrice()).asObject());
        bookingTable.setItems(bookingData);
    }

    /** Populates both combo boxes with the same options as the original Swing class. */
    private void setupCombos() {
        movieSortComboBox.getItems().addAll("Title", "Genre", "Duration (min)", "Showtime");
        movieSortComboBox.selectFirst();

        bookingSortComboBox.getItems().addAll("Booking ID", "Movie", "Showtime", "Total Price");
        bookingSortComboBox.selectFirst();
    }

    /**
     * Loads movies on a background thread, filtering out already-booked ones.
     * Mirrors original SwingWorker-based loadMovies().
     */
    private void loadMoviesAsync() {
        Task<ArrayList<Movie>> task = new Task<>() {
            @Override
            protected ArrayList<Movie> call() {
                HashSet<String> bookedIds = getBookedMovieIds();
                return MovieRepository.getMovies().values().stream()
                        .filter(m -> !bookedIds.contains(m.getMovieId()))
                        .collect(Collectors.toCollection(ArrayList::new));
            }
        };
        task.setOnSucceeded(e -> movieData.setAll(task.getValue()));
        new Thread(task, "load-movies-thread").start();
    }

    /**
     * Loads booking history on a background thread.
     * Mirrors original SwingWorker-based loadBookingHistory().
     */
    private void loadBookingHistoryAsync() {
        Task<ArrayList<Booking>> task = new Task<>() {
            @Override
            protected ArrayList<Booking> call() {
                return new ArrayList<>(BookingRepository.getBookings().values());
            }
        };
        task.setOnSucceeded(e -> bookingData.setAll(task.getValue()));
        new Thread(task, "load-bookings-thread").start();
    }

    /**
     * Returns the set of movie IDs that the current user has already booked.
     * Used to filter the movie list so booked movies are not shown.
     */
    private HashSet<String> getBookedMovieIds() {
        return BookingRepository.getBookings().values().stream()
                .map(Booking::getMovieId)
                .collect(Collectors.toCollection(HashSet::new));
    }

    /**
     * Saves MovieCsvIO, CredentialCsvIO, and BookingCsvIO on a background thread,
     * then runs andThen on the JavaFX Application Thread.
     * Mirrors the original SwingWorker used in logout and window-close handlers.
     *
     * @param andThen action to run after a successful save
     */
    private void saveAndThen(Runnable andThen) {
        Task<Void> task = new Task<>() {
            @Override
            protected Void call() throws Exception {
                MovieCsvIO.getIO().updateLatestData();
                CredentialCsvIO.getIO().updateLatestData();
                BookingCsvIO.getIO().updateLatestData();
                return null;
            }
        };
        task.setOnSucceeded(e -> andThen.run());
        task.setOnFailed(e -> Platform.runLater(() ->
                DisplayMessage.displayError(task.getException().getMessage())));
        new Thread(task, "save-csv-thread").start();
    }

    /** Returns the Stage that owns this controller's scene. */
    private Stage getStage() {
        return (Stage) welcomeLabel.getScene().getWindow();
    }
}