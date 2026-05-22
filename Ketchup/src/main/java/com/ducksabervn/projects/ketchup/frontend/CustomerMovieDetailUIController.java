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
 * File Name:       CustomerMovieDetailUIController.java
 * Developers:       Hoang Duc Phat*, Mai Dinh Vinh* (* equal contributions)
 * Description:     JavaFX controller for CustomerMovieDetailUI.fxml.
 *                  Presents a read-only view of a movie's full details,
 *                  populated from MovieRepository using the provided movie ID.
 *                  Migrated from Swing JFrame to a non-modal JavaFX Stage.
 ******************************************************************************/

package com.ducksabervn.projects.ketchup.frontend;

import com.ducksabervn.projects.ketchup.backend.model.Movie;
import com.ducksabervn.projects.ketchup.backend.repositories.MovieRepository;
import io.github.palexdev.materialfx.controls.MFXButton;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * Controller for the Movie Detail screen (CustomerMovieDetailUI.fxml).
 *
 * Displays a read-only summary of a selected movie screening.
 * All fields are populated from MovieRepository via loadMovieDetail().
 * The Back button closes this Stage.
 *
 * Mirrors the original CustomerMovieDetailUI Swing class, replacing
 * JFrame with a non-modal JavaFX Stage.
 */
public class CustomerMovieDetailUIController implements Initializable {

    // Runtime state
    private String currentMovieId;

    // FXML injected fields

    /** Displays the movie title prominently at the top of the card. */
    @FXML private Label movieTitleValue;

    /** Displays the movie genre. */
    @FXML private Label genreValue;

    /** Displays the runtime in minutes. */
    @FXML private Label durationValue;

    /** Displays the age rating (G, PG, PG-13, R, NC-17). */
    @FXML private Label ratingValue;

    /** Displays the formatted showtime. */
    @FXML private Label showtimeValue;

    /**
     * Displays the seat price highlighted in Netflix green.
     * Mirrors the original green bold text on seatPriceValue.
     */
    @FXML private Label seatPriceValue;

    /** Closes this Stage. Mirrors the original backButton dispose listener. */
    @FXML private MFXButton backButton;

    // Initializable

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Fields are populated after the stage is created via loadMovieDetail(),
        // called by the static initialize() factory before show().
    }

    // Entry point

    /**
     * Static factory — loads CustomerMovieDetailUI.fxml, populates all detail
     * fields, and shows the Stage as a non-modal window.
     *
     * Replaces the original CustomerMovieDetailUI.initialize(String).
     *
     * @param movieId the ID of the movie to display
     * @param owner   the owning Window; used to position the detail window
     *                relative to CustomerHomeUI
     */
    public static void initialize(String movieId, Window owner) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    CustomerMovieDetailUIController.class.getResource(
                            "/fxml/CustomerMovieDetailUI.fxml"));
            Parent root = loader.load();

            CustomerMovieDetailUIController controller = loader.getController();
            controller.currentMovieId = movieId;
            controller.loadMovieDetail();

            Stage stage = new Stage();
            stage.setTitle("Ketchup - Movie Detail");
            stage.setScene(new Scene(root, 600, 500));
            stage.setResizable(false);
            if (owner != null) stage.initOwner(owner);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Action handlers

    /**
     * Handles the Back button click.
     * Closes this Stage and returns focus to CustomerHomeUI.
     * Mirrors the original backButton listener: mainFrame.dispose().
     */
    @FXML
    private void handleBack() {
        getStage().close();
    }

    // Private helpers

    /**
     * Retrieves the movie from MovieRepository and populates all label fields.
     * Mirrors the original loadMovieDetail() method, including the green bold
     * text for seat price (handled via CSS class price-value).
     */
    private void loadMovieDetail() {
        Movie m = MovieRepository.getMovies().get(currentMovieId);
        if (m == null) return;

        movieTitleValue.setText(m.getTitle());
        genreValue.setText(m.getGenre());
        durationValue.setText(m.getDuration() + " min");
        ratingValue.setText(m.getRating());
        showtimeValue.setText(m.getShowTime().format(Movie.getDatetimeFormat()));
        seatPriceValue.setText("$" + m.getSeatPrice());
    }

    /**
     * Returns the Stage that owns this controller's scene.
     *
     * @return the current Stage
     */
    private Stage getStage() {
        return (Stage) backButton.getScene().getWindow();
    }
}