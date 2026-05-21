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
 * File Name:       AdminMovieFormUIController.java
 * Developer:       Tran Phan Anh*, Nguyen Trong Khoi Nguyen*, Nguyen Dinh Quy*,
 *                  Mai Dinh Vinh* (* equal contributions)
 * Description:     JavaFX controller for AdminMovieFormUI.fxml. Preserves all
 *                  original input-validation and repository logic from the Swing
 *                  AdminMovieFormUI class. Displayed as an APPLICATION_MODAL
 *                  Stage, replacing the original JDialog modal.
 *                  Migrated from Swing JDialog to JavaFX Stage.
 ******************************************************************************/

package com.ducksabervn.projects.ketchup.frontend;

import com.ducksabervn.projects.ketchup.backend.model.Movie;
import com.ducksabervn.projects.ketchup.backend.repositories.MovieRepository;
import com.ducksabervn.projects.ketchup.frontend.util.DisplayMessage;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXComboBox;
import io.github.palexdev.materialfx.controls.MFXTextField;
import io.github.palexdev.materialfx.enums.FloatMode;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

/**
 * Controller for the Admin Movie Form modal (AdminMovieFormUI.fxml).
 *
 * <p>Operates in two modes — set via {@link #initialize(String, String, Window)}:
 * <ul>
 *   <li>{@code "ADD"} — empty form; on save, calls {@link MovieRepository#addMovie}
 *       and notifies {@link AdminMovieListUIController#addMovieRow}.</li>
 *   <li>{@code "EDIT"} — pre-filled form; on save, preserves the existing
 *       occupied-seats list and calls {@link MovieRepository#editMovie}.</li>
 * </ul>
 *
 * <p>Save validation order (mirrors original Swing logic exactly):
 * <ol>
 *   <li>Any field blank → inline error, abort.</li>
 *   <li>Duration not a valid integer → inline error, abort.</li>
 *   <li>Seat price not a valid integer → inline error, abort.</li>
 *   <li>Showtime does not match {@code yyyy-MM-dd HH:mm} → inline error, abort.</li>
 *   <li>All valid → commit to repository, close dialog.</li>
 * </ol>
 */
public class AdminMovieFormUIController implements Initializable {

    /** Expected showtime format — identical to the original Swing hint label. */
    private static final DateTimeFormatter SHOWTIME_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    /**
     * Operational mode — {@code "ADD"} or {@code "EDIT"}.
     * Set by {@link #initialize(String, String, Window)} before the Stage is shown.
     */
    private String mode;

    /**
     * Movie ID of the record being edited. {@code null} in {@code "ADD"} mode.
     * Set by {@link #initialize(String, String, Window)}.
     */
    private String currentMovieId;

    /** Heading label — "Add New Movie" or "Edit Movie" set at runtime. */
    @FXML private Label titleLabel;

    /** Movie title text field. */
    @FXML private MFXTextField movieTitleField;

    /** Genre text field. */
    @FXML private MFXTextField genreField;

    /** Duration (minutes) text field — must parse as a valid integer. */
    @FXML private MFXTextField durationField;

    /**
     * Age rating combo box — options: G, PG, PG-13, R, NC-17.
     * Populated in {@link #initialize}.
     */
    @FXML private MFXComboBox<String> ratingComboBox;

    /**
     * Showtime text field — must conform to {@code yyyy-MM-dd HH:mm}.
     * Validated against {@link #SHOWTIME_FORMATTER}.
     */
    @FXML private MFXTextField showtimeField;

    /** Seat price text field — must parse as a valid integer. */
    @FXML private MFXTextField seatPriceField;

    /** Inline error label shown on validation failure. */
    @FXML private Label errorLabel;

    /** Save / confirm button. */
    @FXML private MFXButton saveButton;

    /** Cancel / discard button. */
    @FXML private MFXButton cancelButton;

    /**
     * Called automatically by the FXMLLoader after all @FXML fields are injected.
     * Populates the rating combo box with the same options as the original Swing version.
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        ratingComboBox.getItems().addAll("G", "PG", "PG-13", "R", "NC-17");
        ratingComboBox.selectFirst();
        errorLabel.setText("");
        movieTitleField.setFloatMode(FloatMode.DISABLED);
        genreField.setFloatMode(FloatMode.DISABLED);
        durationField.setFloatMode(FloatMode.DISABLED);
        ratingComboBox.setFloatMode(FloatMode.DISABLED);
        showtimeField.setFloatMode(FloatMode.DISABLED);
        seatPriceField.setFloatMode(FloatMode.DISABLED);
        ratingComboBox.setFloatMode(FloatMode.DISABLED);
    }

    /**
     * Static factory — loads AdminMovieFormUI.fxml into a new APPLICATION_MODAL
     * {@link Stage} and blocks until the dialog is closed.
     *
     * <p>Replaces the original:
     * <pre>{@code
     * AdminMovieFormUI.adminMovieFormUI.mainFrame.setModalityType(
     *         Dialog.ModalityType.APPLICATION_MODAL);
     * }</pre>
     *
     * <p>In {@code "EDIT"} mode the form is pre-filled with the current data of
     * the movie identified by {@code movieId} via {@link #prefillFields()}.
     *
     * @param mode    {@code "ADD"} or {@code "EDIT"}
     * @param movieId the ID of the movie to edit; {@code null} for {@code "ADD"} mode
     * @param owner   the owning {@link Window} used to center the dialog
     */
    public static void initialize(String mode, String movieId, Window owner) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    AdminMovieFormUIController.class.getResource(
                            "/fxml/AdminMovieFormUI.fxml"));
            Parent root = loader.load();

            AdminMovieFormUIController controller = loader.getController();
            controller.mode = mode;
            controller.currentMovieId = movieId;

            // Update heading label and pre-fill fields when in EDIT mode
            controller.titleLabel.setText("ADD".equals(mode) ? "Add New Movie" : "Edit Movie");
            if ("EDIT".equals(mode)) {
                controller.prefillFields();
            }

            Stage dialog = new Stage();
            dialog.setTitle("Ketchup");
            dialog.setScene(new Scene(root));
            dialog.setResizable(false);
            dialog.initModality(Modality.APPLICATION_MODAL);
            if (owner != null) dialog.initOwner(owner);

            dialog.showAndWait(); // blocks — mirrors original JDialog modal behaviour
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Handles the "Save" button click.
     *
     * <p>Validation (identical order to original Swing code):
     * <ol>
     *   <li>Any field blank → inline error.</li>
     *   <li>Duration not parseable as {@code int} → inline error.</li>
     *   <li>Seat price not parseable as {@code int} → inline error.</li>
     *   <li>Showtime does not match {@code yyyy-MM-dd HH:mm} → inline error.</li>
     *   <li>All valid →
     *     <ul>
     *       <li>ADD: {@link MovieRepository#addMovie} + notify list controller.</li>
     *       <li>EDIT: preserve occupied seats, {@link MovieRepository#editMovie}.</li>
     *     </ul>
     *     Then close the dialog.
     *   </li>
     * </ol>
     */
    @FXML
    private void handleSave() {
        String title        = movieTitleField.getText().trim();
        String genre        = genreField.getText().trim();
        String durationStr  = durationField.getText().trim();
        String rating       = ratingComboBox.getValue();
        String showtimeStr  = showtimeField.getText().trim();
        String seatPriceStr = seatPriceField.getText().trim();

        // ── Step 1: empty-field guard ──
        if (title.isEmpty() || genre.isEmpty() || durationStr.isEmpty()
                || showtimeStr.isEmpty() || seatPriceStr.isEmpty()) {
            showError("All fields must be non-empty");
            return;
        }

        // ── Step 2: duration must be integer ──
        int duration;
        try {
            duration = Integer.parseInt(durationStr);
        } catch (NumberFormatException e) {
            showError("Duration must be a valid integer");
            return;
        }

        // ── Step 3: seat price must be integer ──
        int seatPrice;
        try {
            seatPrice = Integer.parseInt(seatPriceStr);
        } catch (NumberFormatException e) {
            showError("Seat price must be a valid integer");
            return;
        }

        // ── Step 4: showtime format ──
        LocalDateTime showtime;
        try {
            showtime = LocalDateTime.parse(showtimeStr, SHOWTIME_FORMATTER);
        } catch (DateTimeParseException e) {
            showError("Please use the format: yyyy-MM-dd HH:mm (e.g. 2026-07-04 19:30)");
            return;
        }

        clearError();

        // ── Step 5: commit to repository ──
        if ("ADD".equals(mode)) {
            try {
                Movie m = MovieRepository.addMovie(
                        title, genre, duration, rating, showtimeStr, "", seatPrice);
                AdminMovieListUIController.addMovieRow(m);
            } catch (IOException ex) {
                DisplayMessage.displayError(ex.getMessage());
                return;
            }
        } else {
            // EDIT — preserve the existing occupied-seats list
            Movie old = MovieRepository.getMovies().get(currentMovieId);
            String occupiedSeats = old.getOccupiedSeat()
                    .stream()
                    .collect(Collectors.joining(","));
            Movie edited = new Movie(
                    currentMovieId, title, genre, duration,
                    rating, showtimeStr, occupiedSeats, seatPrice);
            MovieRepository.editMovie(currentMovieId, edited);
        }

        getStage().close();
    }

    /**
     * Handles the "Cancel" button click.
     * Discards all changes and closes the dialog without modifying any data.
     * Mirrors original: {@code mainFrame.dispose();}.
     */
    @FXML
    private void handleCancel() {
        getStage().close();
    }

    /**
     * Pre-fills all form fields with the current data of the movie identified
     * by {@link #currentMovieId}. Called only in {@code "EDIT"} mode.
     * Mirrors the original Swing pre-fill block inside {@code initializeAllElements()}.
     */
    private void prefillFields() {
        Movie m = MovieRepository.getMovies().get(currentMovieId);
        if (m == null) return;

        movieTitleField.setText(m.getTitle());
        genreField.setText(m.getGenre());
        durationField.setText(String.valueOf(m.getDuration()));
        ratingComboBox.selectItem(m.getRating());
        showtimeField.setText(m.getShowTime().format(SHOWTIME_FORMATTER));
        seatPriceField.setText(String.valueOf(m.getSeatPrice()));
    }

    /**
     * Displays an inline error message below the form fields.
     *
     * @param message the validation error to display
     */
    private void showError(String message) {
        errorLabel.setText(message);
    }

    /** Clears the inline error label. */
    private void clearError() {
        errorLabel.setText("");
    }

    /**
     * Retrieves the {@link Stage} that owns this controller's scene.
     *
     * @return the current dialog {@link Stage}
     */
    private Stage getStage() {
        return (Stage) saveButton.getScene().getWindow();
    }
}