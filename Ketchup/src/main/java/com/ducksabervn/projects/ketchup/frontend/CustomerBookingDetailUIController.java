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
 * File Name:       CustomerBookingDetailUIController.java
 * Developer:       Tran Phan Anh*, Nguyen Trong Khoi Nguyen*, Nguyen Dinh Quy*,
 *                  Mai Dinh Vinh* (* equal contributions)
 * Description:     JavaFX controller for CustomerBookingDetailUI.fxml.
 *                  Re-uses the 8x12 cinema seat grid from CustomerSeatSelectionUI
 *                  in a fully read-only mode. The customer's own seats are
 *                  highlighted in gold (seat-mine), other occupied seats are red
 *                  (seat-booked), and free seats are green (seat-available).
 *                  No seat interaction is possible — every button is disabled.
 *                  Shown as APPLICATION_MODAL from CustomerHomeUIController.
 ******************************************************************************/

package com.ducksabervn.projects.ketchup.frontend;

import com.ducksabervn.projects.ketchup.backend.model.Booking;
import com.ducksabervn.projects.ketchup.backend.model.Movie;
import com.ducksabervn.projects.ketchup.backend.repositories.BookingRepository;
import com.ducksabervn.projects.ketchup.backend.repositories.MovieRepository;
import com.ducksabervn.projects.ketchup.backend.services.TicketPdfExportService;
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
import javafx.stage.FileChooser;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.HashSet;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Controller for the Booking Detail modal (CustomerBookingDetailUI.fxml).
 *
 * Behaviour:
 *   - Builds the same 8-row × 12-column grid used by CustomerSeatSelectionUI.
 *   - Customer's own booked seats → styled seat-mine (gold), disabled.
 *   - Other occupied seats         → styled seat-booked (red),  disabled.
 *   - Free seats                   → styled seat-available (green), disabled.
 *   - Summary bar shows Booking ID, chosen seats, and total price.
 *   - Only action is Close (no seat toggling, no proceed).
 */
public class CustomerBookingDetailUIController implements Initializable {

    // Grid constants — identical to CustomerSeatSelectionUIController
    private static final String[] ROW_LABELS = {"A", "B", "C", "D", "E", "F", "G", "H"};
    private static final int SEATS_PER_ROW   = 12;
    private static final int AISLE_AFTER     = 6;

    // Runtime state
    private String currentBookingId;

    // 2D array mirrors the grid layout
    private final Button[][] seatGrid = new Button[ROW_LABELS.length][SEATS_PER_ROW];

    // FXML injected fields
    @FXML private Label     movieTitleLabel;
    @FXML private GridPane  seatGridPane;
    @FXML private Label     bookingIdLabel;
    @FXML private Label     selectedSeatsLabel;
    @FXML private Label     totalPriceLabel;
    @FXML private MFXButton closeButton;
    @FXML private MFXButton exportPdfButton;

    // -------------------------------------------------------------------------
    // Initializable
    // -------------------------------------------------------------------------

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Grid and data are populated after bookingId is set by the static factory.
    }

    // -------------------------------------------------------------------------
    // Entry point
    // -------------------------------------------------------------------------

    /**
     * Static factory — loads CustomerBookingDetailUI.fxml, wires the booking
     * data, builds the read-only seat grid, and shows the Stage as
     * APPLICATION_MODAL.
     *
     * @param bookingId the UUID string of the booking to display
     * @param owner     the owning Window; pass null to centre on screen
     */
    public static void initialize(String bookingId, Window owner) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    CustomerBookingDetailUIController.class.getResource(
                            "/fxml/CustomerBookingDetailUI.fxml"));
            Parent root = loader.load();

            CustomerBookingDetailUIController controller = loader.getController();
            controller.currentBookingId = bookingId;
            controller.setup();

            Stage dialog = new Stage();
            dialog.setTitle("Ketchup - Booking Detail");
            dialog.setScene(new Scene(root, 900, 650));
            dialog.setResizable(false);
            dialog.initModality(Modality.APPLICATION_MODAL);
            if (owner != null) dialog.initOwner(owner);
            dialog.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // -------------------------------------------------------------------------
    // Action handler
    // -------------------------------------------------------------------------

    /**
     * Closes the detail dialog.
     */
    @FXML
    private void handleClose() {
        getStage().close();
    }

    // -------------------------------------------------------------------------
    // Private helpers
    // -------------------------------------------------------------------------

    /**
     * Populates the header, builds the seat grid, marks seat states, and
     * fills in the summary bar. Called by the static factory after bookingId
     * is set.
     */
    private void setup() {
        Booking b = BookingRepository.getBookings().get(currentBookingId);
        Movie   m = MovieRepository.getMovies().get(b.getMovieId());

        // Header
        movieTitleLabel.setText(
                m.getTitle() + "  |  Showtime: " + m.getShowTime().format(Movie.getDatetimeFormat()));

        // Seat grid
        buildSeatGrid();
        markSeats(m.getOccupiedSeat(), b.getChosenSeats());

        // Summary bar
        bookingIdLabel.setText("Booking ID: " + b.getBookingId());
        String seatsStr = b.getChosenSeats().stream()
                .sorted()
                .collect(Collectors.joining(", "));
        selectedSeatsLabel.setText("Your Seats: " + (seatsStr.isEmpty() ? "None" : seatsStr));
        totalPriceLabel.setText("Total Price: $" + b.getTotalPrice());
    }

    /**
     * Constructs the 8x12 read-only seat grid inside seatGridPane.
     * Layout is identical to CustomerSeatSelectionUIController.buildSeatGrid(),
     * except every button is disabled (no click handler registered).
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
                // Visual aisle gap after column AISLE_AFTER
                if (col == AISLE_AFTER + 1) {
                    Region aisle = new Region();
                    aisle.setMinWidth(14);
                    aisle.setMaxWidth(14);
                    seatGridPane.add(aisle, gridCol++, row);
                }

                String seatId = ROW_LABELS[row] + col;
                Button btn = new Button(seatId);
                btn.getStyleClass().addAll("seat-btn", "seat-available");
                btn.setDisable(true);           // read-only — no interaction
                seatGrid[row][col - 1] = btn;
                seatGridPane.add(btn, gridCol++, row);
            }
        }
    }

    /**
     * Applies CSS classes to each seat button based on its state:
     * <ul>
     *   <li>{@code seat-mine}    — seat belongs to this booking (gold)</li>
     *   <li>{@code seat-booked}  — seat occupied by someone else (red)</li>
     *   <li>{@code seat-available} — free seat (green) — default, unchanged</li>
     * </ul>
     *
     * Note: {@code occupiedSeat} from Movie already includes {@code mySeats},
     * so we check {@code mySeats} first to avoid colouring them red.
     *
     * @param occupiedSeat all seats currently occupied for this movie
     * @param mySeats      the seats belonging to this specific booking
     */
    private void markSeats(Set<String> occupiedSeat, HashSet<String> mySeats) {
        for (int row = 0; row < ROW_LABELS.length; row++) {
            for (int col = 0; col < SEATS_PER_ROW; col++) {
                String seatId = ROW_LABELS[row] + (col + 1);
                Button btn    = seatGrid[row][col];

                if (mySeats.contains(seatId)) {
                    // Customer's own seats — highlight in gold
                    btn.getStyleClass().remove("seat-available");
                    btn.getStyleClass().add("seat-mine");
                } else if (occupiedSeat.contains(seatId)) {
                    // Occupied by another customer
                    btn.getStyleClass().remove("seat-available");
                    btn.getStyleClass().add("seat-booked");
                }
                // else: stays seat-available (green, disabled)
            }
        }
    }

    /**
     * Opens a Save dialog and exports the current booking as a PDF ticket.
     */
    @FXML
    private void handleExportPdf() {
        Booking b = BookingRepository.getBookings().get(currentBookingId);
        Movie   m = MovieRepository.getMovies().get(b.getMovieId());

        FileChooser chooser = new FileChooser();
        chooser.setTitle("Save Ticket as PDF");
        chooser.setInitialFileName(
                "ticket_" + b.getBookingId().substring(0, 8) + ".pdf");
        chooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("PDF Files", "*.pdf"));

        File dest = chooser.showSaveDialog(getStage());
        if (dest == null) return; // user cancelled

        try {
            TicketPdfExportService.export(currentBookingId, dest);
            DisplayMessage.displayInformation(
                    "Ticket exported successfully!\n" + dest.getAbsolutePath());
        } catch (Exception e) {
            DisplayMessage.displayError("Failed to export PDF: " + e.getMessage());
        }
    }

    /**
     * Returns the Stage that owns this controller's scene.
     *
     * @return the current dialog Stage
     */
    private Stage getStage() {
        return (Stage) closeButton.getScene().getWindow();
    }
}
