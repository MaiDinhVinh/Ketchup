package com.ducksabervn.projects.ketchup.frontend;

import com.ducksabervn.projects.ketchup.backend.admin.Movie;
import com.ducksabervn.projects.ketchup.backend.admin.MovieRepository;
import com.ducksabervn.projects.ketchup.backend.credientials.Credential;
import com.ducksabervn.projects.ketchup.backend.credientials.CredentialRepository;
import com.ducksabervn.projects.ketchup.backend.helper.DisplayMessage;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class CustomerSeatSelectionUI {

    //SINGLETON DESIGN PATTERN => 1 UI INSTANCE AT A TIME
    private static CustomerSeatSelectionUI customerSeatSelectionUI;

    //fixed cinema seat formation: 8 rows x 12 seats, with a center aisle between col 6 and 7
    private static final String[] ROW_LABELS    = {"A", "B", "C", "D", "E", "F", "G", "H"};
    private static final int      SEATS_PER_ROW = 12;
    private static final int      AISLE_AFTER   = 6;

    //the movie ID being booked
    private String currentMovieId;

    //list of seat IDs the customer has currently selected

    //change log: Changed from List<String> to HashSet<String>, for constant time checking
    private HashSet<String> selectedSeatIds;

    //current login email
    private String currentEmail;

    //the main frame
    private JDialog mainFrame;
    private JPanel mainPanel;

    //top bar: movie title + back button
    private JPanel topPanel;
    private JLabel titleLabel;

    //seat layout section
    private JPanel seatSectionPanel;

    //screen indicator above seat grid
    private JLabel screenLabel;

    //fixed seat grid: rows x cols of JButtons
    private JButton[][] seatGrid;
    private JPanel seatGridPanel;

    //seat legend: shows color meaning
    private JPanel legendPanel;
    private JLabel legendAvailable;
    private JLabel legendBooked;
    private JLabel legendSelected;

    //bottom summary + confirm section
    private JPanel bottomPanel;
    private JLabel selectedSeatsLabel;
    private JLabel totalPriceLabel;
    private JPanel actionButtonPanel;
    private JButton cancelButton;
    private JButton proceedButton;

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
     * Since we are using singleton design pattern, this UI will only be initialized once
     * movieId is the ID of the movie the customer wants to book
     */
    public static void initialize(String movieId, String currentEmail) {
        CustomerSeatSelectionUI.customerSeatSelectionUI = new CustomerSeatSelectionUI();
        CustomerSeatSelectionUI.customerSeatSelectionUI.currentMovieId = movieId;
        CustomerSeatSelectionUI.customerSeatSelectionUI.currentEmail = currentEmail;
        CustomerSeatSelectionUI.customerSeatSelectionUI.initializeAllElements();
        CustomerSeatSelectionUI.customerSeatSelectionUI.mainFrame.add(CustomerSeatSelectionUI.customerSeatSelectionUI.mainPanel);
        CustomerSeatSelectionUI.customerSeatSelectionUI.mainFrame.setModalityType(Dialog.ModalityType.APPLICATION_MODAL);
        CustomerSeatSelectionUI.customerSeatSelectionUI.mainFrame.setVisible(true);
    }

    private void initializeAllElements() {
        //initialize the main frame
        mainFrame.setTitle("Ketchup - Select Seat");
        mainFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        mainFrame.setSize(720, 600);
        mainFrame.setLocationRelativeTo(null);
        mainFrame.setResizable(false);

        //initialize the main content panel
        mainPanel.setLayout(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        //initialize the top bar (movie title + back button)
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        topPanel.add(titleLabel, BorderLayout.WEST);
        topPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 5, 0));
        mainPanel.add(topPanel, BorderLayout.NORTH);

        //initialize the screen indicator
        screenLabel.setFont(new Font("Arial", Font.BOLD, 13));
        screenLabel.setOpaque(true);
        screenLabel.setBackground(Color.DARK_GRAY);
        screenLabel.setForeground(Color.WHITE);
        screenLabel.setBorder(BorderFactory.createEmptyBorder(4, 0, 4, 0));

        //build the fixed seat grid
        buildFixedSeatGrid();

        //initialize the seat color legend
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

        //group screen + seat grid + legend into seat section
        JPanel seatGridWrapper = new JPanel(new BorderLayout(0, 8));
        seatGridWrapper.add(screenLabel, BorderLayout.NORTH);
        seatGridWrapper.add(seatGridPanel, BorderLayout.CENTER);
        seatGridWrapper.add(legendPanel, BorderLayout.SOUTH);
        seatSectionPanel.setBorder(BorderFactory.createTitledBorder("Seat Layout"));
        seatSectionPanel.add(seatGridWrapper, BorderLayout.CENTER);
        mainPanel.add(seatSectionPanel, BorderLayout.CENTER);

        //initialize the bottom summary section
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

        //load booked seats immediately on startup
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
            CustomerBookingConfirmUI.initialize(this.currentMovieId, new ArrayList<>(this.selectedSeatIds), this.currentEmail);
        });

        ////SUBSECTION - ADDING LISTENER TO THE CANCEL BUTTON
        this.cancelButton.addActionListener(e -> {
            mainFrame.dispose();
        });
    }

    //build the fixed seat grid layout with row labels and center aisle
    private void buildFixedSeatGrid() {
        seatGridPanel.removeAll();
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(3, 3, 3, 3);

        for (int row = 0; row < ROW_LABELS.length; row++) {
            int gridCol = 0;

            //row label on the left (e.g. "A", "B", ...)
            gbc.gridx = gridCol++;
            gbc.gridy = row;
            JLabel rowLabel = new JLabel(ROW_LABELS[row], SwingConstants.CENTER);
            rowLabel.setFont(new Font("Arial", Font.BOLD, 12));
            rowLabel.setPreferredSize(new Dimension(20, 36));
            seatGridPanel.add(rowLabel, gbc);

            for (int col = 1; col <= SEATS_PER_ROW; col++) {
                //add an empty aisle gap after AISLE_AFTER seats
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

    //create a single available seat button with click listener
    private JButton createSeatButton(String seatId) {
        JButton button = new JButton(seatId);
        button.setPreferredSize(new Dimension(46, 34));
        button.setFont(new Font("Arial", Font.PLAIN, 10));
        button.setFocusPainted(false);
        button.setBackground(new Color(144, 238, 144));
        button.addActionListener(e -> onSeatClicked(button, seatId));
        return button;
    }

    //mark a list of seat IDs as booked (red + disabled)
    private void markBookedSeats(List<String> bookedSeatIds) {
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

    //handle seat button click: toggle selection state
    private void onSeatClicked(JButton seatButton, String seatId) {
        if (selectedSeatIds.contains(seatId)) {
            //deselect: turn back to available green
            selectedSeatIds.remove(seatId);
            seatButton.setBackground(new Color(144, 238, 144));
            seatButton.setForeground(Color.BLACK);
        } else {
            //select: turn blue
            selectedSeatIds.add(seatId);
            seatButton.setBackground(new Color(100, 149, 237));
            seatButton.setForeground(Color.WHITE);
        }
        updateSummary();
    }

    //update selected seats label and total price in the summary section
    private void updateSummary() {
        if (selectedSeatIds.isEmpty()) {
            selectedSeatsLabel.setText("Selected Seats: None");
            totalPriceLabel.setText("Total Price: $0");
        } else {
            selectedSeatsLabel.setText("Selected Seats: " + String.join(",", selectedSeatIds));
            int ticketPrice = MovieRepository.getMovies().get(currentMovieId).getSeatPrice();
            int total = selectedSeatIds.size() * ticketPrice;
            totalPriceLabel.setText("Total Price: $" + total);
        }
    }
}