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
 * File Name:       CustomerSeatSelectionUIController.java
 * Developer:       Tran Phan Anh*, Nguyen Trong Khoi Nguyen*, Nguyen Dinh Quy*,
 *                  Mai Dinh Vinh* (* equal contributions)
 * Description:     JavaFX controller for CustomerSeatSelectionUI.fxml.
 *                  Reproduces the original 8x12 cinema seat grid with aisle gap,
 *                  seat toggle logic, booked-seat marking, and live summary update.
 *                  Migrated from Swing JDialog / JButton grid to JavaFX Stage /
 *                  GridPane / Button.
 ******************************************************************************/

package com.ducksabervn.projects.ketchup.frontend;

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
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Region;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.io.IOException;
import java.net.URL;
import java.util.HashSet;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Controller for the Seat Selection modal (CustomerSeatSelectionUI.fxml).
 *
 * Reproduces the original Swing seat map behavior:
 *   - Fixed 8 rows (A-H) x 12 columns, with a visual aisle gap after column 6.
 *   - Occupied seats are pre-marked as booked (red, disabled).
 *   - Clicking an available seat toggles it between available and selected.
 *   - The summary bar updates after every toggle showing selected seats and price.
 *   - Proceed validates at least one seat is selected then opens
 *     CustomerBookingConfirmUIController.
 *   - Cancel closes this dialog.
 */
public class CustomerSeatSelectionUIController implements Initializable {

    // Grid constants — identical to the original Swing class
    private static final String[] ROW_LABELS  = {"A", "B", "C", "D", "E", "F", "G", "H"};
    private static final int SEATS_PER_ROW    = 12;
    private static final int AISLE_AFTER      = 6;

    // Runtime state
    private String currentMovieId;
    private String currentEmail;
    private final HashSet<String> selectedSeatIds = new HashSet<>();

    // 2D array of seat buttons mirrors original seatGrid[row][col]
    private final Button[][] seatGrid = new Button[ROW_LABELS.length][SEATS_PER_ROW];

    // FXML injected fields
    @FXML private Label      movieTitleLabel;
    @FXML private GridPane   seatGridPane;
    @FXML private Label      selectedSeatsLabel;
    @FXML private Label      totalPriceLabel;
    @FXML private MFXButton  cancelButton;
    @FXML private MFXButton  proceedButton;

    // Initializable

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Grid and data are populated after movieId is set by the static factory.
    }

    // Entry point

    /**
     * Static factory — loads CustomerSeatSelectionUI.fxml, sets runtime state,
     * builds the seat grid, and shows the Stage as APPLICATION_MODAL.
     *
     * Replaces the original CustomerSeatSelectionUI.initialize(String, String).
     *
     * @param movieId      the ID of the movie screening
     * @param currentEmail the email of the currently logged-in customer
     * @param owner        the owning Window; pass null to center on screen
     */
    public static void initialize(String movieId, String currentEmail, Window owner) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    CustomerSeatSelectionUIController.class.getResource(
                            "/fxml/CustomerSeatSelectionUI.fxml"));
            Parent root = loader.load();

            CustomerSeatSelectionUIController controller = loader.getController();
            controller.currentMovieId = movieId;
            controller.currentEmail   = currentEmail;
            controller.setup();

            Stage dialog = new Stage();
            dialog.setTitle("Ketchup - Select Seat");
            dialog.setScene(new Scene(root, 900, 620));
            dialog.setResizable(false);
            dialog.initModality(Modality.APPLICATION_MODAL);
            if (owner != null) dialog.initOwner(owner);
            dialog.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Action handlers

    /**
     * Handles the Proceed button click.
     *
     * Validates at least one seat is selected, then closes this dialog
     * and opens CustomerBookingConfirmUIController.
     * Mirrors the original proceedButton listener exactly.
     */
    @FXML
    private void handleProceed() {
        if (selectedSeatIds.isEmpty()) {
            DisplayMessage.displayWarning("Please select at least one seat.");
            return;
        }
        getStage().close();
        CustomerBookingConfirmUIController.initialize(
                currentMovieId,
                new HashSet<>(selectedSeatIds),
                currentEmail);
    }

    /**
     * Handles the Cancel button click.
     * Closes this dialog without proceeding.
     * Mirrors the original cancelButton listener: mainFrame.dispose().
     */
    @FXML
    private void handleCancel() {
        getStage().close();
    }

    // Private helpers

    /**
     * Populates the movie heading, builds the full seat grid, then marks
     * already-occupied seats. Called by the static factory after movieId is set.
     * Mirrors the combined behavior of the original initializeAllElements().
     */
    private void setup() {
        Movie m = MovieRepository.getMovies().get(currentMovieId);
        movieTitleLabel.setText(
                m.getTitle() + "  |  Showtime: " + m.getShowTime().format(Movie.getDatetimeFormat()));
        buildSeatGrid();
        markBookedSeats(m.getOccupiedSeat());
    }

    /**
     * Constructs the 8x12 seat grid inside seatGridPane.
     *
     * Layout per row:
     *   Column 0       : row label (A-H)
     *   Columns 1-6    : seat buttons for seats 1-6
     *   Column 7       : visual aisle gap (empty Region)
     *   Columns 8-13   : seat buttons for seats 7-12
     *
     * Mirrors the original buildFixedSeatGrid() method exactly, using
     * JavaFX GridPane instead of Swing GridBagLayout.
     */
    private void buildSeatGrid() {
        seatGridPane.getChildren().clear();

        for (int row = 0; row < ROW_LABELS.length; row++) {
            int gridCol = 0;

            // Row label
            Label rowLabel = new Label(ROW_LABELS[row]);
            rowLabel.getStyleClass().add("row-label");
            seatGridPane.add(rowLabel, gridCol++, row);

            for (int col = 1; col <= SEATS_PER_ROW; col++) {
                // Insert aisle gap after column AISLE_AFTER
                if (col == AISLE_AFTER + 1) {
                    Region aisle = new Region();
                    aisle.setMinWidth(14);
                    aisle.setMaxWidth(14);
                    seatGridPane.add(aisle, gridCol++, row);
                }

                String seatId = ROW_LABELS[row] + col;
                Button seatBtn = createSeatButton(seatId);
                seatGrid[row][col - 1] = seatBtn;
                seatGridPane.add(seatBtn, gridCol++, row);
            }
        }
    }

    /**
     * Creates a single styled seat button for the given seat ID.
     * Initial state is available (dark green).
     * Registers a click handler via onSeatClicked().
     *
     * Mirrors the original createSeatButton(String seatId) method.
     *
     * @param seatId the seat identifier, e.g. "A1", "B7"
     * @return a configured JavaFX Button representing the seat
     */
    private Button createSeatButton(String seatId) {
        Button btn = new Button(seatId);
        btn.getStyleClass().addAll("seat-btn", "seat-available");
        btn.setOnAction(e -> onSeatClicked(btn, seatId));
        return btn;
    }

    /**
     * Marks all seats in bookedSeatIds as booked by switching their CSS class
     * to seat-booked and disabling them.
     *
     * Mirrors the original markBookedSeats(Set<String>) method.
     *
     * @param bookedSeatIds the set of already-occupied seat IDs from Movie.getOccupiedSeat()
     */
    private void markBookedSeats(Set<String> bookedSeatIds) {
        for (int row = 0; row < ROW_LABELS.length; row++) {
            for (int col = 0; col < SEATS_PER_ROW; col++) {
                String seatId = ROW_LABELS[row] + (col + 1);
                if (bookedSeatIds.contains(seatId)) {
                    Button btn = seatGrid[row][col];
                    btn.getStyleClass().remove("seat-available");
                    btn.getStyleClass().add("seat-booked");
                    btn.setDisable(true);
                }
            }
        }
    }

    /**
     * Toggles the selection state of the clicked seat button.
     *
     * If selected: removes from selectedSeatIds, switches CSS class back to
     * seat-available.
     * If not selected: adds to selectedSeatIds, switches CSS class to
     * seat-selected.
     *
     * Updates the summary bar after every toggle via updateSummary().
     * Mirrors the original onSeatClicked(JButton, String) method.
     *
     * @param seatButton the Button that was clicked
     * @param seatId     the seat identifier associated with the button
     */
    private void onSeatClicked(Button seatButton, String seatId) {
        if (selectedSeatIds.contains(seatId)) {
            selectedSeatIds.remove(seatId);
            seatButton.getStyleClass().remove("seat-selected");
            seatButton.getStyleClass().add("seat-available");
        } else {
            selectedSeatIds.add(seatId);
            seatButton.getStyleClass().remove("seat-available");
            seatButton.getStyleClass().add("seat-selected");
        }
        updateSummary();
    }

    /**
     * Refreshes the selected-seats label and total price label to reflect
     * the current contents of selectedSeatIds.
     *
     * Displays "None" and "$0" when no seats are selected.
     * Mirrors the original updateSummary() method.
     */
    private void updateSummary() {
        if (selectedSeatIds.isEmpty()) {
            selectedSeatsLabel.setText("Selected Seats: None");
            totalPriceLabel.setText("Total Price: $0");
            return;
        }

        String seats = selectedSeatIds.stream()
                .sorted()
                .collect(Collectors.joining(", "));
        selectedSeatsLabel.setText("Selected Seats: " + seats);

        Movie m = MovieRepository.getMovies().get(currentMovieId);
        int total = BookingRepository.calculateTotalPrice(selectedSeatIds, m.getSeatPrice());
        totalPriceLabel.setText("Total Price: $" + total);
    }

    /**
     * Returns the Stage that owns this controller's scene.
     *
     * @return the current dialog Stage
     */
    private Stage getStage() {
        return (Stage) proceedButton.getScene().getWindow();
    }
}