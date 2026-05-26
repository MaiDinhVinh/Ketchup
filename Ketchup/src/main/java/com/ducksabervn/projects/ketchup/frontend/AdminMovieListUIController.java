/******************************************************************************
 * Project Name:    Ketchup - A movie management system
 * Course:          COMP1020 - OOP and Data Structure
 * Semester:        Spring 2026
 * <p>
 * Members: Tran Phan Anh <25anh.tp@vinuni.edu.vn>,
 *          Nguyen Trong Khoi Nguyen <25nguyen.ntk@vinuni.edu.vn>,
 *          Nguyen Dinh Quy <25quy.nd@vinuni.edu.vn>,
 *          Hoang Duc Phat <25phat.hd@vinuni.edu.vn>,
 *          Mai Dinh Vinh <25vinh.md@vinuni.edu.vn>
 * <p>
 * File Name:       AdminMovieListUIController.java
 * Developers:       Hoang Duc Phat*, Mai Dinh Vinh* (* equal contributions)
 * Description:     JavaFX controller for AdminMovieListUI.fxml. Preserves all
 *                  original search, sort, CRUD, logout, and window-close logic
 *                  from the Swing AdminMovieListUI class. Migrated from Swing
 *                  JFrame / JTable / SwingWorker to JavaFX Stage / TableView / Task.
 ******************************************************************************/

package com.ducksabervn.projects.ketchup.frontend;

import com.ducksabervn.projects.ketchup.backend.model.Movie;
import com.ducksabervn.projects.ketchup.backend.repositories.MovieRepository;
import com.ducksabervn.projects.ketchup.frontend.util.DisplayMessage;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXComboBox;
import io.github.palexdev.materialfx.controls.MFXTextField;
import io.github.palexdev.materialfx.enums.FloatMode;
import javafx.application.Platform;
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
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import org.apache.commons.logging.Log;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

/**
 * Controller for the Admin Movie List screen (AdminMovieListUI.fxml).
 *
 * <p>Responsibilities mirror the original {@code AdminMovieListUI} Swing class:
 * <ul>
 *   <li>Populate the movie {@link TableView} from {@link MovieRepository} on load.</li>
 *   <li>Search movies via {@link MovieRepository#searchMovie}.</li>
 *   <li>Sort movies by 8 criteria using the same {@link Comparator} chain.</li>
 *   <li>Open {@link AdminMovieFormUIController} in ADD or EDIT mode.</li>
 *   <li>Delete the selected movie after confirmation.</li>
 *   <li>Logout: confirm → save CSVs on background thread → open Login.</li>
 *   <li>Window-close (X): save CSVs on background thread → {@link Platform#exit()}.</li>
 * </ul>
 *
 * <p>Provides {@link #addMovieRow(Movie)} and {@link #getInstance()} as static
 * helpers so that {@link AdminMovieFormUIController} can notify this screen after
 * a successful ADD, mirroring the original {@code AdminMovieListUI.addMovieRow(Movie)}.
 */
public class AdminMovieListUIController implements Initializable {

    private static final DateTimeFormatter DT_FMT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    /** Sole running instance — set in {@link #initialize(String)}. */
    private static AdminMovieListUIController instance;

    /** Live list of movies currently displayed in the table. */
    private final ObservableList<Movie> tableData = FXCollections.observableArrayList();

    private static final Comparator<Movie> SORT_BY_ID =
            Comparator.comparing(Movie::getMovieId);
    private static final Comparator<Movie> SORT_BY_TITLE =
            Comparator.comparing(Movie::getTitle);
    private static final Comparator<Movie> SORT_BY_GENRE =
            Comparator.comparing(Movie::getGenre);
    private static final Comparator<Movie> SORT_BY_DURATION =
            Comparator.comparing(Movie::getDuration);
    private static final Comparator<Movie> SORT_BY_RATING =
            Comparator.comparing(Movie::getRating);
    private static final Comparator<Movie> SORT_BY_SHOWTIME =
            Comparator.comparing(Movie::getShowTime);
    private static final Comparator<Movie> SORT_BY_OCCUPIED_SEATS =
            Comparator.comparingInt((Movie m) -> m.getOccupiedSeat().size());
    private static final Comparator<Movie> SORT_BY_SEAT_PRICE =
            Comparator.comparing(Movie::getSeatPrice);

    @FXML private Label           usernameLabel;
    @FXML private MFXButton       logoutButton;

    @FXML private MFXTextField    searchField;
    @FXML private MFXButton       searchButton;
    @FXML private MFXComboBox<String> sortComboBox;
    @FXML private MFXButton       sortButton;
    @FXML private MFXButton       refreshButton;

    @FXML private TableView<Movie>       movieTable;
    @FXML private TableColumn<Movie, String>  colId;
    @FXML private TableColumn<Movie, String>  colTitle;
    @FXML private TableColumn<Movie, String>  colGenre;
    @FXML private TableColumn<Movie, Integer> colDuration;
    @FXML private TableColumn<Movie, String>  colRating;
    @FXML private TableColumn<Movie, String>  colShowtime;
    @FXML private TableColumn<Movie, String>  colSeats;
    @FXML private TableColumn<Movie, Integer> colSeatPrice;

    @FXML private MFXButton addMovieButton;
    @FXML private MFXButton editMovieButton;
    @FXML private MFXButton deleteMovieButton;

    /**
     * Called automatically by FXMLLoader after all @FXML fields are injected.
     * Sets up table columns and populates the sort combo box.
     * Username is set later by {@link #initialize(String)} before the Stage is shown.
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        instance = this;

        colId.setCellValueFactory(
                data -> new javafx.beans.property.SimpleStringProperty(
                        data.getValue().getMovieId()));
        colTitle.setCellValueFactory(
                data -> new javafx.beans.property.SimpleStringProperty(
                        data.getValue().getTitle()));
        colGenre.setCellValueFactory(
                data -> new javafx.beans.property.SimpleStringProperty(
                        data.getValue().getGenre()));
        colDuration.setCellValueFactory(
                new PropertyValueFactory<>("duration"));
        colRating.setCellValueFactory(
                data -> new javafx.beans.property.SimpleStringProperty(
                        data.getValue().getRating()));
        colShowtime.setCellValueFactory(
                data -> new javafx.beans.property.SimpleStringProperty(
                        data.getValue().getShowTime().format(DT_FMT)));
        colSeats.setCellValueFactory(
                data -> new javafx.beans.property.SimpleStringProperty(
                        data.getValue().getOccupiedSeat()
                                .stream().collect(Collectors.joining(","))));
        colSeatPrice.setCellValueFactory(
                new PropertyValueFactory<>("seatPrice"));

        movieTable.setItems(tableData);
        sortComboBox.getItems().addAll(
                "ID", "Title", "Genre", "Duration (min)", "Rating",
                "Showtime", "Number of selected seats", "Price/Seat");
        sortComboBox.selectFirst();
        searchField.setFloatMode(FloatMode.DISABLED);
        sortComboBox.setFloatMode(FloatMode.DISABLED);
    }

    /**
     * Static factory — loads AdminMovieListUI.fxml, passes the admin username,
     * loads all movies into the table, and shows the Stage.
     * Replaces the original {@code AdminMovieListUI.initialize(String username)}.
     *
     * @param username the display name of the logged-in administrator
     */
    public static void initialize(String username) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    AdminMovieListUIController.class.getResource(
                            "/fxml/AdminMovieListUI.fxml"));
            Parent root = loader.load();

            AdminMovieListUIController controller = loader.getController();
            controller.usernameLabel.setText("Welcome, " + username);
            controller.loadMoviesAsync();

            Stage stage = new Stage();
            stage.setTitle("Ketchup – Admin");
            stage.setScene(new Scene(root, 1100, 650));
            stage.setResizable(true);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Returns the current singleton controller instance.
     * Mirrors original {@code AdminMovieListUI.getAdminMovieListUI()}.
     *
     * @return the active {@link AdminMovieListUIController}
     */
    public static AdminMovieListUIController getInstance() {
        return instance;
    }

    /**
     * Adds a newly created {@link Movie} to the live table.
     * Called by {@link AdminMovieFormUIController} after a successful ADD.
     * Mirrors original {@code AdminMovieListUI.addMovieRow(Movie)}.
     *
     * @param m the newly created {@link Movie} to append to the table
     */
    public static void addMovieRow(Movie m) {
        if (instance != null) {
            Platform.runLater(() -> instance.tableData.add(m));
        }
    }

    /**
     * Returns the {@link Stage} of this screen.
     * Used by {@link AdminMovieFormUIController} as the dialog owner.
     *
     * @return the admin list {@link Stage}
     */
    public Stage getStage() {
        return (Stage) movieTable.getScene().getWindow();
    }

    /**
     * Handles the Search button click.
     * Calls {@link MovieRepository#searchMovie} and refreshes the table.
     * Mirrors original SwingWorker-based search listener.
     */
    @FXML
    private void handleSearch() {
        String query = searchField.getText().trim();
        if (query.isEmpty()) {
            loadMoviesAsync();
            return;
        }
        Task<ArrayList<Movie>> task = new Task<>() {
            @Override
            protected ArrayList<Movie> call() {
                return MovieRepository.searchMovie(query);
            }
        };
        task.setOnSucceeded(e -> {
            tableData.setAll(task.getValue());
        });
        new Thread(task, "search-thread").start();
    }

    /**
     * Handles the Sort button click.
     * Sorts {@link #tableData} using the selected comparator criterion.
     * Mirrors original {@code sortMovieByCriterion(int)} switch.
     */
    @FXML
    private void handleSort() {
        String criterion = sortComboBox.getValue();
        if (criterion == null) return;
        switch (criterion) {
            case "ID"                        -> sortByCriterion(SORT_BY_ID);
            case "Title"                     -> sortByCriterion(SORT_BY_TITLE);
            case "Genre"                     -> sortByCriterion(SORT_BY_GENRE);
            case "Duration (min)"            -> sortByCriterion(SORT_BY_DURATION);
            case "Rating"                    -> sortByCriterion(SORT_BY_RATING);
            case "Showtime"                  -> sortByCriterion(SORT_BY_SHOWTIME);
            case "Number of selected seats"  -> sortByCriterion(SORT_BY_OCCUPIED_SEATS);
            case "Price/Seat"                -> sortByCriterion(SORT_BY_SEAT_PRICE);
        }
    }

    /**
     * Handles the Refresh button click.
     * Reloads all movies from {@link MovieRepository} asynchronously.
     */
    @FXML
    private void handleRefresh() {
        loadMoviesAsync();
    }

    /**
     * Handles the "Add Movie" button click.
     * Opens {@link AdminMovieFormUIController} in ADD mode.
     * Mirrors original: {@code AdminMovieFormUI.initialize("ADD", null);}.
     */
    @FXML
    private void handleAddMovie() {
        AdminMovieFormUIController.initialize("ADD", null, getStage());
    }

    /**
     * Handles the "Edit Movie" button click.
     * Opens {@link AdminMovieFormUIController} in EDIT mode for the selected row,
     * then refreshes that row in the table from the repository.
     * Mirrors original edit listener logic exactly.
     */
    @FXML
    private void handleEditMovie() {
        Movie selected = movieTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            DisplayMessage.displayWarning("Please select a movie to edit.");
            return;
        }
        String selectedMovieId = selected.getMovieId();
        AdminMovieFormUIController.initialize("EDIT", selectedMovieId, getStage());

        // Refresh the edited row from the repository (mirrors original tableModel.setValueAt calls)
        Movie updated = MovieRepository.getMovies().get(selectedMovieId);
        if (updated != null) {
            int idx = tableData.indexOf(selected);
            if (idx >= 0) {
                tableData.set(idx, updated);
                movieTable.getSelectionModel().select(idx);
            }
        }
    }

    /**
     * Handles the "Delete Movie" button click.
     * Confirms with the user, deletes from {@link MovieRepository}, and removes
     * the row from the table.
     * Mirrors original delete listener logic exactly.
     */
    @FXML
    private void handleDeleteMovie() {
        Movie selected = movieTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            DisplayMessage.displayWarning("Please select a movie to delete.");
            return;
        }
        if (DisplayMessage.displayConfirmationDialog("Are you sure you want to delete this movie?")) {
            try{
                MovieRepository.deleteMovie(selected.getMovieId());
            }catch(SQLException e){
                DisplayMessage.displayError(e.getMessage());
            }
            tableData.remove(selected);
        }
    }

    /**
     * Handles the Logout button click.
     * Confirms with the user, then saves both CSVs on a background thread
     * before navigating back to the Login screen.
     * Mirrors original SwingWorker logout logic.
     */
    @FXML
    private void handleLogout() {
        if (!DisplayMessage.displayConfirmationDialog("Are you sure you want to logout?")) {
            return;
        }
        getStage().close();
        Platform.runLater(LoginUIController::initialize);
    }

    /**
     * Loads all movies from {@link MovieRepository} on a background thread
     * and populates {@link #tableData} on the JavaFX Application Thread.
     * Replaces the original {@code SwingWorker}-based {@code loadMovies()}.
     */
    private void loadMoviesAsync() {
        Task<ArrayList<Movie>> task = new Task<>() {
            @Override
            protected ArrayList<Movie> call() {
                return new ArrayList<>(MovieRepository.getMovies().values());
            }
        };
        task.setOnSucceeded(e -> tableData.setAll(task.getValue()));
        new Thread(task, "load-movies-thread").start();
    }

    /**
     * Sorts {@link #tableData} in-place using the given {@link Comparator}.
     * Mirrors original {@code sortMovieByCriterion(int criterion)} method.
     *
     * @param comparator the sort criterion to apply
     */
    private void sortByCriterion(Comparator<Movie> comparator) {
        FXCollections.sort(tableData, comparator);
    }
}