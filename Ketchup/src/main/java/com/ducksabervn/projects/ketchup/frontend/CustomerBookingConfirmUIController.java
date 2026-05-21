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
 * File Name:       CustomerBookingConfirmUIController.java
 * Developer:       Tran Phan Anh*, Nguyen Trong Khoi Nguyen*, Nguyen Dinh Quy*,
 *                  Mai Dinh Vinh* (* equal contributions)
 * Description:     JavaFX controller for CustomerBookingConfirmUI.fxml.
 *                  Preserves all original booking-commit and cancel logic from
 *                  the Swing CustomerBookingConfirmUI class. Displayed as an
 *                  APPLICATION_MODAL Stage, replacing the original JDialog.
 ******************************************************************************/

package com.ducksabervn.projects.ketchup.frontend;

import com.ducksabervn.projects.ketchup.backend.model.Booking;
import com.ducksabervn.projects.ketchup.backend.model.Movie;
import com.ducksabervn.projects.ketchup.backend.repositories.BookingRepository;
import com.ducksabervn.projects.ketchup.backend.repositories.MovieRepository;
import com.ducksabervn.projects.ketchup.frontend.util.DisplayMessage;
import io.github.palexdev.materialfx.controls.MFXButton;
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
import java.sql.SQLException;
import java.util.HashSet;
import java.util.ResourceBundle;

/**
 * Controller for the Booking Confirmation modal (CustomerBookingConfirmUI.fxml).
 *
 * Responsibilities mirror the original Swing class:
 *
 * On confirm: calculates total price, creates a Booking via
 * BookingRepository.addBooking, removes the movie from the customer home list
 * via CustomerHomeUIController.removeMovie, appends the new booking row via
 * CustomerHomeUIController.addBookingRow, then closes the dialog.
 *
 * On cancel: asks the customer for confirmation. If confirmed, closes this dialog
 * and reopens CustomerSeatSelectionUIController so the customer can revise seats.
 */
public class CustomerBookingConfirmUIController implements Initializable {

    // Runtime state set before the Stage is shown

    /** ID of the movie being booked. */
    private String currentMovieId;

    /** Email of the currently logged-in customer. */
    private String currentEmail;

    /** Seat IDs chosen in CustomerSeatSelectionUI. */
    private HashSet<String> currentSelectedSeatIds;

    // FXML injected fields

    /** Displays the movie title. */
    @FXML private Label movieTitleValue;

    /** Displays the showtime. */
    @FXML private Label showtimeValue;

    /** Displays the comma-separated seat IDs. */
    @FXML private Label seatsValue;

    /** Displays the total number of selected seats. */
    @FXML private Label seatCountValue;

    /** Displays the per-seat ticket price. */
    @FXML private Label pricePerSeatValue;

    /** Displays the computed total price, highlighted in green. */
    @FXML private Label totalPriceValue;

    /** Inline message label for errors or status text. */
    @FXML private Label messageLabel;

    /** Primary action — confirms and finalizes the booking. */
    @FXML private MFXButton confirmButton;

    /** Secondary action — cancels and reopens seat selection. */
    @FXML private MFXButton cancelButton;

    // Initializable

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        messageLabel.setText("");
    }

    // Entry point

    /**
     * Static factory — loads CustomerBookingConfirmUI.fxml, populates the
     * booking summary, and shows it as an APPLICATION_MODAL dialog.
     *
     * Replaces the original CustomerBookingConfirmUI.initialize(String, HashSet, String).
     *
     * @param movieId         the ID of the movie being booked
     * @param selectedSeatIds the seat IDs chosen in CustomerSeatSelectionUI
     * @param email           the email of the currently logged-in customer
     */
    public static void initialize(String movieId,
                                  HashSet<String> selectedSeatIds,
                                  String email) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    CustomerBookingConfirmUIController.class.getResource(
                            "/fxml/CustomerBookingConfirmUI.fxml"));
            Parent root = loader.load();

            CustomerBookingConfirmUIController controller = loader.getController();
            controller.currentMovieId        = movieId;
            controller.currentEmail          = email;
            controller.currentSelectedSeatIds = selectedSeatIds;
            controller.loadBookingSummary();

            Stage dialog = new Stage();
            dialog.setTitle("Ketchup - Confirm Booking");
            dialog.setScene(new Scene(root));
            dialog.setResizable(false);
            dialog.initModality(Modality.APPLICATION_MODAL);
            dialog.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Action handlers

    /**
     * Handles the "Confirm Booking" button click.
     *
     * Mirrors original confirm listener:
     * 1. Get the movie and calculate total price.
     * 2. Create a Booking via BookingRepository.addBooking.
     * 3. Remove the movie from CustomerHomeUIController.
     * 4. Add the booking row to CustomerHomeUIController.
     * 5. Close the dialog.
     */
    @FXML
    private void handleConfirm() {
        Movie m = MovieRepository.getMovies().get(currentMovieId);
        int ticketPrice = m.getSeatPrice();
        int total = BookingRepository.calculateTotalPrice(currentSelectedSeatIds, ticketPrice);

        try{
            Booking b = BookingRepository.addBooking(
                    currentEmail,
                    currentMovieId,
                    m.getShowTime().format(Movie.getDatetimeFormat()),
                    currentSelectedSeatIds,
                    total);
            CustomerHomeUIController.removeMovie(currentMovieId);
            CustomerHomeUIController.addBookingRow(b);
            getStage().close();
        }catch(SQLException e){
            DisplayMessage.displayError(e.getMessage());
        }
    }

    /**
     * Handles the "Cancel" button click.
     *
     * Mirrors original cancel listener:
     * If the customer confirms the cancellation, closes this dialog and
     * reopens CustomerSeatSelectionUIController to allow seat revision.
     */
    @FXML
    private void handleCancel() {
        boolean confirmed = DisplayMessage.displayConfirmationDialog("Are you sure you want to cancel this booking?");
        if (confirmed) {
            getStage().close();
            CustomerSeatSelectionUIController.initialize(currentMovieId, currentEmail, null);
        }
    }

    // Private helpers

    /**
     * Retrieves movie and seat data for the current booking context and
     * populates all summary labels in the dialog.
     *
     * Mirrors original loadBookingSummary() method exactly.
     */
    private void loadBookingSummary() {
        Movie m = MovieRepository.getMovies().get(currentMovieId);
        int ticketPrice = m.getSeatPrice();
        int total = BookingRepository.calculateTotalPrice(currentSelectedSeatIds, ticketPrice);

        movieTitleValue.setText(m.getTitle());
        showtimeValue.setText(m.getShowTime().format(Movie.getDatetimeFormat()));
        seatsValue.setText(String.join(", ", currentSelectedSeatIds));
        seatCountValue.setText(String.valueOf(currentSelectedSeatIds.size()));
        pricePerSeatValue.setText("$" + ticketPrice);
        totalPriceValue.setText("$" + total);
    }

    /**
     * Returns the Stage that owns this controller's scene.
     *
     * @return the current dialog Stage
     */
    private Stage getStage() {
        return (Stage) confirmButton.getScene().getWindow();
    }
}