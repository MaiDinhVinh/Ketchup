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
 * File Name:       CustomerSeatSelectionUI.java
 * Developer:       Tran Phan Anh*, Nguyen The Khoi Nguyen*, Nguyen Dinh Quy*,
 *                  Mai Dinh Vinh* (* equal contributions)
 * Description:     Modal dialog presenting an interactive cinema seat map to
 *                  the customer, allowing them to select available seats for
 *                  a chosen movie screening before proceeding to booking
 *                  confirmation.
 ******************************************************************************/

package com.ducksabervn.projects.ketchup.frontend;

import com.ducksabervn.projects.ketchup.backend.repositories.BookingRepository;
import com.ducksabervn.projects.ketchup.backend.model.Movie;
import com.ducksabervn.projects.ketchup.backend.repositories.MovieRepository;
import com.ducksabervn.projects.ketchup.frontend.util.DisplayMessage;

import javax.swing.*;
import java.awt.*;
import java.util.HashSet;
import java.util.Set;

/**
 * Modal dialog that renders an interactive cinema seat map for a selected
 * movie screening, allowing the customer to toggle individual seat selections
 * before proceeding to {@link CustomerBookingConfirmUI}. The seat grid is
 * fixed at 8 rows × 12 columns with a center aisle after column 6. Seats
 * already occupied (as recorded in the {@link Movie} object) are displayed
 * in red and disabled. Selected seats are highlighted in blue, and a live
 * summary of selected seats and total price is updated after every toggle.
 */
public class CustomerSeatSelectionUI {

    /**
     * The sole instance of {@code CustomerSeatSelectionUI}, replaced on each
     * call to {@link #initialize(String, String)}.
     */
    private static CustomerSeatSelectionUI customerSeatSelectionUI;

    /**
     * Row label characters for the seat grid, representing rows A through H.
     * Each element corresponds to one row of seat buttons.
     */
    private static final String[] ROW_LABELS = {"A", "B", "C", "D", "E", "F", "G", "H"};

    /**
     * The total number of seats per row in the cinema layout.
     */
    private static final int SEATS_PER_ROW = 12;

    /**
     * The column index after which the center aisle gap is inserted.
     * A visual gap is rendered between column 6 and column 7.
     */
    private static final int AISLE_AFTER = 6;

    /**
     * The ID of the movie screening for which seats are being selected,
     * used to retrieve the corresponding {@link Movie} from
     * {@link MovieRepository}.
     */
    private String currentMovieId;

    /**
     * The set of seat IDs (e.g. {@code "A1"}, {@code "B3"}) currently
     * selected by the customer. Uses a {@link HashSet} for constant-time
     * membership checks during toggle operations.
     */
    private HashSet<String> selectedSeatIds;

    /**
     * The email address of the currently logged-in customer, passed through
     * to {@link CustomerBookingConfirmUI} when the customer proceeds.
     */
    private String currentEmail;

    /** The main modal dialog window hosting all seat selection UI components. */
    private JDialog mainFrame;

    /** The root content panel using {@link BorderLayout}. */
    private JPanel mainPanel;

    /** Panel containing the movie title and showtime heading at the top. */
    private JPanel topPanel;

    /**
     * Label displaying the selected movie's title and showtime,
     * formatted as {@code "<Title> (Showtime: yyyy-MM-dd HH:mm)"}.
     */
    private JLabel titleLabel;

    /**
     * Panel grouping the screen indicator, seat grid, and color legend
     * into the central seat layout section.
     */
    private JPanel seatSectionPanel;

    /**
     * Label rendered as a dark banner above the seat grid to visually
     * represent the cinema screen that all seats face.
     */
    private JLabel screenLabel;

    /**
     * Two-dimensional array of {@link JButton} objects representing the
     * physical seat grid. Indexed as {@code seatGrid[row][col]} where
     * row ∈ [0, 7] and col ∈ [0, 11].
     */
    private JButton[][] seatGrid;

    /**
     * Panel that contains the seat button grid, laid out using
     * {@link GridBagLayout} to accommodate row labels and the center aisle.
     */
    private JPanel seatGridPanel;

    /**
     * Panel containing the color legend labels that explain the meaning
     * of each seat button color to the customer.
     */
    private JPanel legendPanel;

    /**
     * Legend label with a green background indicating an available seat
     * that can be selected by the customer.
     */
    private JLabel legendAvailable;

    /**
     * Legend label with a red background indicating a seat that has
     * already been booked and cannot be selected.
     */
    private JLabel legendBooked;

    /**
     * Legend label with a blue background indicating a seat that has
     * been selected by the customer in the current session.
     */
    private JLabel legendSelected;

    /**
     * Panel at the bottom of the dialog displaying the current seat
     * selection summary and the Proceed and Cancel action buttons.
     */
    private JPanel bottomPanel;

    /**
     * Label displaying the comma-separated list of currently selected
     * seat IDs, updated after every seat toggle.
     */
    private JLabel selectedSeatsLabel;

    /**
     * Label displaying the running total price for all currently selected
     * seats, updated after every seat toggle.
     */
    private JLabel totalPriceLabel;

    /**
     * Panel containing the Cancel and Proceed to Confirm action buttons,
     * aligned to the right of the summary section.
     */
    private JPanel actionButtonPanel;

    /**
     * Button that discards the current seat selection and closes the
     * dialog without proceeding to booking confirmation.
     */
    private JButton cancelButton;

    /**
     * Button that validates that at least one seat is selected, then
     * closes this dialog and opens {@link CustomerBookingConfirmUI}
     * with the current seat selection.
     */
    private JButton proceedButton;

    /**
     * Private constructor that initializes all Swing components with default
     * values and allocates the seat grid array. Customization and layout are
     * handled by {@link #initializeAllElements()}.
     */
    private CustomerSeatSelectionUI() {
        this.selectedSeatIds = new HashSet<>();
        this.seatGrid = new JButton[ROW_LABELS.length][SEATS_PER_ROW];
        this.mainFrame = new JDialog();
        this.mainPanel = new JPanel();
        this.topPanel = new JPanel(new BorderLayout());
        this.titleLabel = new JLabel("", SwingConstants.LEFT);
        this.seatSectionPanel = new JPanel(new BorderLayout(0, 5));
        this.screenLabel = new JLabel("[ SCREEN ]", SwingConstants.CENTER);
        this.seatGridPanel = new JPanel(new GridBagLayout());
        this.legendPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 5));
        this.legendAvailable = new JLabel("  Available  ");
        this.legendBooked = new JLabel("  Booked  ");
        this.legendSelected = new JLabel("  Selected  ");
        this.bottomPanel = new JPanel(new BorderLayout(10, 5));
        this.selectedSeatsLabel = new JLabel("Selected Seats: None");
        this.totalPriceLabel = new JLabel("Total Price: $0");
        this.actionButtonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        this.cancelButton = new JButton("Cancel");
        this.proceedButton = new JButton("Proceed to Confirm");
    }

    /**
     * Creates a new {@code CustomerSeatSelectionUI} instance for the specified
     * movie and customer, then displays it as an application-modal dialog.
     * Occupied seats are marked immediately on startup.
     *
     * @param movieId      the ID of the movie screening for which to display
     *                     the seat map, corresponding to a key in
     *                     {@link MovieRepository}
     * @param currentEmail the email address of the currently logged-in customer,
     *                     forwarded to {@link CustomerBookingConfirmUI} on proceed
     */
    public static void initialize(String movieId, String currentEmail) {
        CustomerSeatSelectionUI.customerSeatSelectionUI = new CustomerSeatSelectionUI();
        CustomerSeatSelectionUI.customerSeatSelectionUI.currentMovieId = movieId;
        CustomerSeatSelectionUI.customerSeatSelectionUI.currentEmail = currentEmail;
        CustomerSeatSelectionUI.customerSeatSelectionUI.initializeAllElements();
        CustomerSeatSelectionUI.customerSeatSelectionUI.mainFrame.add(
                CustomerSeatSelectionUI.customerSeatSelectionUI.mainPanel);
        CustomerSeatSelectionUI.customerSeatSelectionUI.mainFrame.setModalityType(
                Dialog.ModalityType.APPLICATION_MODAL);
        CustomerSeatSelectionUI.customerSeatSelectionUI.mainFrame.setVisible(true);
    }

    /**
     * Configures and lays out all UI components within the dialog. Builds the
     * fixed seat grid, styles the color legend, populates the movie title and
     * showtime heading, marks all currently occupied seats as booked, and
     * attaches action listeners to the Proceed and Cancel buttons.
     * <p>
     * On proceed: validates that at least one seat is selected, then closes
     * this dialog and opens {@link CustomerBookingConfirmUI} with the current
     * selection.
     * <p>
     * On cancel: closes the dialog without making any changes.
     */
    private void initializeAllElements() {
        mainFrame.setTitle("Ketchup - Select Seat");
        mainFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        mainFrame.setSize(720, 600);
        mainFrame.setLocationRelativeTo(null);
        mainFrame.setResizable(false);

        mainPanel.setLayout(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        topPanel.add(titleLabel, BorderLayout.WEST);
        topPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 5, 0));
        mainPanel.add(topPanel, BorderLayout.NORTH);

        screenLabel.setFont(new Font("Arial", Font.BOLD, 13));
        screenLabel.setOpaque(true);
        screenLabel.setBackground(Color.DARK_GRAY);
        screenLabel.setForeground(Color.WHITE);
        screenLabel.setBorder(BorderFactory.createEmptyBorder(4, 0, 4, 0));

        buildFixedSeatGrid();

        legendAvailable.setOpaque(true);
        legendAvailable.setBackground(new Color(144, 238, 144));
        legendAvailable.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        legendBooked.setOpaque(true);
        legendBooked.setBackground(new Color(200, 200, 200));
        legendBooked.setForeground(Color.WHITE);
        legendBooked.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        legendSelected.setOpaque(true);
        legendSelected.setBackground(new Color(100, 149, 237));
        legendSelected.setForeground(Color.WHITE);
        legendSelected.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        legendPanel.add(new JLabel("Legend:"));
        legendPanel.add(legendAvailable);
        legendPanel.add(legendBooked);
        legendPanel.add(legendSelected);

        JPanel seatGridWrapper = new JPanel(new BorderLayout(0, 8));
        seatGridWrapper.add(screenLabel, BorderLayout.NORTH);
        seatGridWrapper.add(seatGridPanel, BorderLayout.CENTER);
        seatGridWrapper.add(legendPanel, BorderLayout.SOUTH);
        seatSectionPanel.setBorder(BorderFactory.createTitledBorder("Seat Layout"));
        seatSectionPanel.add(seatGridWrapper, BorderLayout.CENTER);
        mainPanel.add(seatSectionPanel, BorderLayout.CENTER);

        selectedSeatsLabel.setFont(new Font("Arial", Font.PLAIN, 13));
        totalPriceLabel.setFont(new Font("Arial", Font.BOLD, 13));
        cancelButton.setPreferredSize(new Dimension(100, 30));
        proceedButton.setPreferredSize(new Dimension(170, 30));
        actionButtonPanel.add(cancelButton);
        actionButtonPanel.add(proceedButton);
        JPanel summaryLeft = new JPanel(new GridLayout(2, 1, 0, 4));
        summaryLeft.add(selectedSeatsLabel);
        summaryLeft.add(totalPriceLabel);
        bottomPanel.setBorder(BorderFactory.createTitledBorder("Summary"));
        bottomPanel.add(summaryLeft, BorderLayout.CENTER);
        bottomPanel.add(actionButtonPanel, BorderLayout.EAST);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        Movie m = MovieRepository.getMovies().get(this.currentMovieId);
        titleLabel.setText(m.getTitle() + " (Showtime: " + m.getShowTime().format(Movie.getDatetimeFormat()) + ")");
        this.markBookedSeats(m.getOccupiedSeat());

        ////SUBSECTION - ADDING LISTENER TO THE PROCEED BUTTON
        this.proceedButton.addActionListener(e -> {
            if (selectedSeatIds.isEmpty()) {
                DisplayMessage.displayWarning(this.mainFrame, "Please select at least one seat.");
                return;
            }
            mainFrame.dispose();
            CustomerBookingConfirmUI.initialize(this.currentMovieId, this.selectedSeatIds, this.currentEmail);
        });

        ////SUBSECTION - ADDING LISTENER TO THE CANCEL BUTTON
        this.cancelButton.addActionListener(e -> {
            mainFrame.dispose();
        });
    }

    /**
     * Constructs the fixed 8×12 seat grid and adds all seat buttons to
     * {@link #seatGridPanel} using {@link GridBagLayout}. Each row begins
     * with a row label (e.g. {@code "A"}, {@code "B"}), followed by seat
     * buttons for columns 1 through 6, a visual aisle gap, then seat buttons
     * for columns 7 through 12. Each button is initialized in the available
     * (green) state and registered with a click listener via
     * {@link #createSeatButton(String)}.
     */
    private void buildFixedSeatGrid() {
        seatGridPanel.removeAll();
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(3, 3, 3, 3);

        for (int row = 0; row < ROW_LABELS.length; row++) {
            int gridCol = 0;

            gbc.gridx = gridCol++;
            gbc.gridy = row;
            JLabel rowLabel = new JLabel(ROW_LABELS[row], SwingConstants.CENTER);
            rowLabel.setFont(new Font("Arial", Font.BOLD, 12));
            rowLabel.setPreferredSize(new Dimension(20, 36));
            seatGridPanel.add(rowLabel, gbc);

            for (int col = 1; col <= SEATS_PER_ROW; col++) {
                if (col == AISLE_AFTER + 1) {
                    gbc.gridx = gridCol++;
                    gbc.gridy = row;
                    seatGridPanel.add(Box.createHorizontalStrut(18), gbc);
                }

                String seatId = ROW_LABELS[row] + col;
                JButton seatButton = createSeatButton(seatId);
                seatGrid[row][col - 1] = seatButton;

                gbc.gridx = gridCol++;
                gbc.gridy = row;
                seatGridPanel.add(seatButton, gbc);
            }
        }

        seatGridPanel.revalidate();
        seatGridPanel.repaint();
    }

    /**
     * Creates a single seat button for the given seat ID, styled in the
     * available (green) state, and registers an action listener that
     * delegates click events to {@link #onSeatClicked(JButton, String)}.
     *
     * @param seatId the seat identifier to display on the button and use
     *               as the key for selection tracking (e.g. {@code "A1"})
     * @return a configured {@link JButton} representing the specified seat
     */
    private JButton createSeatButton(String seatId) {
        JButton button = new JButton(seatId);
        button.setPreferredSize(new Dimension(46, 34));
        button.setFont(new Font("Arial", Font.PLAIN, 10));
        button.setFocusPainted(false);
        button.setBackground(new Color(144, 238, 144));
        button.addActionListener(e -> onSeatClicked(button, seatId));
        return button;
    }

    /**
     * Marks all seat IDs in the given set as booked by disabling their
     * corresponding buttons and applying a red background. Called once on
     * dialog initialization to reflect seats already reserved by other users.
     *
     * @param bookedSeatIds the set of seat IDs that are already occupied,
     *                      sourced from {@link Movie#getOccupiedSeat()}
     */
    private void markBookedSeats(Set<String> bookedSeatIds) {
        for (int row = 0; row < ROW_LABELS.length; row++) {
            for (int col = 0; col < SEATS_PER_ROW; col++) {
                String seatId = ROW_LABELS[row] + (col + 1);
                if (bookedSeatIds.contains(seatId)) {
                    JButton btn = seatGrid[row][col];
                    btn.setBackground(new Color(220, 80, 80));
                    btn.setForeground(Color.WHITE);
                    btn.setEnabled(false);
                }
            }
        }
    }

    /**
     * Handles a click on a seat button by toggling its selection state.
     * If the seat is currently selected, it is deselected and returned to
     * the available (green) color. If it is not selected, it is added to
     * {@link #selectedSeatIds} and highlighted in blue. The summary section
     * is updated after every toggle via {@link #updateSummary()}.
     *
     * @param seatButton the {@link JButton} that was clicked
     * @param seatId     the seat identifier associated with the clicked button
     *                   (e.g. {@code "B5"})
     */
    private void onSeatClicked(JButton seatButton, String seatId) {
        if (selectedSeatIds.contains(seatId)) {
            selectedSeatIds.remove(seatId);
            seatButton.setBackground(new Color(144, 238, 144));
            seatButton.setForeground(Color.BLACK);
        } else {
            selectedSeatIds.add(seatId);
            seatButton.setBackground(new Color(100, 149, 237));
            seatButton.setForeground(Color.WHITE);
        }
        updateSummary();
    }

    /**
     * Refreshes the selected seats label and total price label in the summary
     * section to reflect the current contents of {@link #selectedSeatIds}.
     * Displays {@code "None"} and {@code "$0"} when no seats are selected,
     * or the comma-separated seat list and computed total otherwise.
     */
    private void updateSummary() {
        if (selectedSeatIds.isEmpty()) {
            selectedSeatsLabel.setText("Selected Seats: None");
            totalPriceLabel.setText("Total Price: $0");
        } else {
            selectedSeatsLabel.setText("Selected Seats: " + String.join(",", selectedSeatIds));
            int ticketPrice = MovieRepository.getMovies().get(currentMovieId).getSeatPrice();
            int total = BookingRepository.calculateTotalPrice(selectedSeatIds, ticketPrice);
            totalPriceLabel.setText("Total Price: $" + total);
        }
    }
}