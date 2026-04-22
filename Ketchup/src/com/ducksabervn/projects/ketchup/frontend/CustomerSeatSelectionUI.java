package com.ducksabervn.projects.ketchup.frontend;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
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
    private List<String> selectedSeatIds;

    //the main frame
    private JFrame mainFrame;
    private JPanel mainPanel;

    //top bar: movie title + back button
    private JPanel topPanel;
    private JLabel titleLabel;
    private JButton backButton;

    //showtime selection section
    private JPanel showtimePanel;
    private JLabel showtimeLabel;
    private JComboBox<String> showtimeComboBox;
    private JButton loadSeatsButton;

    //seat layout section
    private JPanel seatSectionPanel;

    //screen indicator above seat grid
    private JLabel screenLabel;

    //fixed seat grid: rows x cols of JButtons, aisle gaps are null
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
    private JButton proceedButton;

    private CustomerSeatSelectionUI() {
        this.selectedSeatIds = new ArrayList<>();
        this.seatGrid = new JButton[ROW_LABELS.length][SEATS_PER_ROW];
        this.mainFrame = new JFrame("Movie Booking System - Select Seat");
        this.mainPanel = new JPanel();
        this.topPanel = new JPanel(new BorderLayout());
        this.titleLabel = new JLabel("", SwingConstants.LEFT);
        this.backButton = new JButton("Back");
        this.showtimePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        this.showtimeLabel = new JLabel("Select Showtime:");
        this.showtimeComboBox = new JComboBox<>();
        this.loadSeatsButton = new JButton("Load Seats");
        this.seatSectionPanel = new JPanel(new BorderLayout(0, 5));
        this.screenLabel = new JLabel("[ SCREEN ]", SwingConstants.CENTER);

        //seat grid panel: cols = row label + 6 seats + aisle gap + 6 seats
        this.seatGridPanel = new JPanel(new GridBagLayout());

        this.legendPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 5));
        this.legendAvailable = new JLabel("  Available  ");
        this.legendBooked = new JLabel("  Booked  ");
        this.legendSelected = new JLabel("  Selected  ");
        this.bottomPanel = new JPanel(new BorderLayout(10, 5));
        this.selectedSeatsLabel = new JLabel("Selected Seats: None");
        this.totalPriceLabel = new JLabel("Total Price: $0");
        this.proceedButton = new JButton("Proceed to Confirm");
    }

    /**
     * Since we are using singleton design pattern, this UI will only be initialized once
     * movieId is the ID of the movie the customer wants to book
     */
    public static void initialize(String movieId) {
        if (CustomerSeatSelectionUI.customerSeatSelectionUI == null) {
            CustomerSeatSelectionUI.customerSeatSelectionUI = new CustomerSeatSelectionUI();
        }
        CustomerSeatSelectionUI.customerSeatSelectionUI.currentMovieId = movieId;
        CustomerSeatSelectionUI.customerSeatSelectionUI.initializeAllElements();
        CustomerSeatSelectionUI.customerSeatSelectionUI.mainFrame.add(CustomerSeatSelectionUI.customerSeatSelectionUI.mainPanel);
        CustomerSeatSelectionUI.customerSeatSelectionUI.mainFrame.setVisible(true);
    }

    private void initializeAllElements() {
        //initialize the main frame
        mainFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        mainFrame.setSize(720, 640);
        mainFrame.setLocationRelativeTo(null);
        mainFrame.setResizable(false);

        //initialize the main content panel
        mainPanel.setLayout(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        //initialize the top bar (movie title + back button)
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        // TODO: titleLabel.setText(MovieService.getMovieById(currentMovieId).getTitle())
        backButton.setPreferredSize(new Dimension(80, 30));
        topPanel.add(titleLabel, BorderLayout.WEST);
        topPanel.add(backButton, BorderLayout.EAST);
        topPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 5, 0));
        mainPanel.add(topPanel, BorderLayout.NORTH);

        //initialize the showtime selection row
        showtimeComboBox.setPreferredSize(new Dimension(250, 28));
        showtimePanel.setBorder(BorderFactory.createTitledBorder("Showtime"));
        showtimePanel.add(showtimeLabel);
        showtimePanel.add(showtimeComboBox);
        showtimePanel.add(loadSeatsButton);
        // TODO: Call ShowtimeService.getShowtimesByMovieId(currentMovieId) and populate showtimeComboBox

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
        legendBooked.setBackground(new Color(220, 80, 80));
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

        //group showtime row + seat section into CENTER
        JPanel centerPanel = new JPanel(new BorderLayout(0, 10));
        centerPanel.add(showtimePanel, BorderLayout.NORTH);
        centerPanel.add(seatSectionPanel, BorderLayout.CENTER);
        mainPanel.add(centerPanel, BorderLayout.CENTER);

        //initialize the bottom summary section
        selectedSeatsLabel.setFont(new Font("Arial", Font.PLAIN, 13));
        totalPriceLabel.setFont(new Font("Arial", Font.BOLD, 13));
        proceedButton.setPreferredSize(new Dimension(170, 30));
        JPanel summaryLeft = new JPanel(new GridLayout(2, 1, 0, 4));
        summaryLeft.add(selectedSeatsLabel);
        summaryLeft.add(totalPriceLabel);
        bottomPanel.setBorder(BorderFactory.createTitledBorder("Summary"));
        bottomPanel.add(summaryLeft, BorderLayout.CENTER);
        bottomPanel.add(proceedButton, BorderLayout.EAST);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        ////SUBSECTION - ADDING LISTENER TO THE LOAD SEATS BUTTON
        this.loadSeatsButton.addActionListener(e -> {
            String selectedShowtime = (String) showtimeComboBox.getSelectedItem();
            if (selectedShowtime == null) {
                JOptionPane.showMessageDialog(mainFrame, "No showtime available.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            selectedSeatIds.clear();
            updateSummary();
            resetAllSeatsToAvailable();
            // TODO: Get showtime ID from selected item in showtimeComboBox
            // TODO: Call SeatService.getBookedSeatIdsByShowtimeId(showtimeId) to get booked seat ID list
            // TODO: Pass the booked seat list into markBookedSeats(bookedSeatIds)
        });

        ////SUBSECTION - ADDING LISTENER TO THE PROCEED BUTTON
        this.proceedButton.addActionListener(e -> {
            if (selectedSeatIds.isEmpty()) {
                JOptionPane.showMessageDialog(mainFrame, "Please select at least one seat.", "No Seat Selected", JOptionPane.WARNING_MESSAGE);
                return;
            }
            // TODO: Get showtime ID from selected item in showtimeComboBox
            // TODO: Open CustomerBookingConfirmUI with currentMovieId, showtimeId, selectedSeatIds
        });

        ////SUBSECTION - ADDING LISTENER TO THE BACK BUTTON
        this.backButton.addActionListener(e -> {
            mainFrame.dispose();
            customerSeatSelectionUI = null;
            // TODO: Open CustomerMovieDetailUI.initialize(currentMovieId)
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

    //reset all seats back to available state (called before loading new showtime)
    private void resetAllSeatsToAvailable() {
        for (int row = 0; row < ROW_LABELS.length; row++) {
            for (int col = 0; col < SEATS_PER_ROW; col++) {
                JButton btn = seatGrid[row][col];
                btn.setBackground(new Color(144, 238, 144));
                btn.setForeground(Color.BLACK);
                btn.setEnabled(true);
            }
        }
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
            selectedSeatsLabel.setText("Selected Seats: " + String.join(", ", selectedSeatIds));
            // TODO: Get ticket price from ShowtimeService.getShowtimeById(showtimeId).getTicketPrice()
            // TODO: int total = selectedSeatIds.size() * ticketPrice
            // TODO: totalPriceLabel.setText("Total Price: $" + total)
        }
    }
}