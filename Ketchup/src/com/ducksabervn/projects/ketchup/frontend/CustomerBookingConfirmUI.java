package com.ducksabervn.projects.ketchup.frontend;

import com.ducksabervn.projects.ketchup.backend.admin.Movie;
import com.ducksabervn.projects.ketchup.backend.admin.MovieRepository;
import com.ducksabervn.projects.ketchup.backend.credientials.Credential;
import com.ducksabervn.projects.ketchup.backend.credientials.CredentialRepository;
import com.ducksabervn.projects.ketchup.backend.helper.ReadCSVFile;
import com.ducksabervn.projects.ketchup.backend.user.Booking;
import com.ducksabervn.projects.ketchup.backend.user.BookingRepository;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;

public class CustomerBookingConfirmUI {

    //SINGLETON DESIGN PATTERN => 1 UI INSTANCE AT A TIME
    private static CustomerBookingConfirmUI customerBookingConfirmUI;

    //booking context passed in from CustomerSeatSelectionUI
    private String currentMovieId;
    private String currentShowtimeId;
    private String currentEmail;
    private List<String> currentSelectedSeatIds;

    //the main frame
    private JDialog mainFrame;
    private JPanel mainPanel;

    //top bar: title + back button
    private JPanel topPanel;
    private JLabel titleLabel;

    //booking summary section
    private JPanel summaryPanel;

    //movie info rows
    private JPanel movieTitleRow;
    private JLabel movieTitleLabel;
    private JLabel movieTitleValue;

    private JPanel showtimeRow;
    private JLabel showtimeLabel;
    private JLabel showtimeValue;


    private JPanel seatsRow;
    private JLabel seatsLabel;
    private JLabel seatsValue;

    private JPanel seatCountRow;
    private JLabel seatCountLabel;
    private JLabel seatCountValue;

    private JPanel pricePerSeatRow;
    private JLabel pricePerSeatLabel;
    private JLabel pricePerSeatValue;

    private JPanel totalPriceRow;
    private JLabel totalPriceLabel;
    private JLabel totalPriceValue;

    //separator between summary and total
    private JSeparator separator;

    //error/success message label
    private JLabel messageLabel;

    //action buttons
    private JPanel buttonPanel;
    private JButton confirmButton;
    private JButton cancelButton;

    private CustomerBookingConfirmUI() {
        this.mainFrame = new JDialog();
        this.mainPanel = new JPanel();
        this.topPanel = new JPanel(new BorderLayout());
        this.titleLabel = new JLabel("Booking Confirmation", SwingConstants.LEFT);
        this.summaryPanel = new JPanel(new GridLayout(9, 1, 6, 6));
        this.movieTitleRow = new JPanel(new BorderLayout());
        this.movieTitleLabel = new JLabel("Movie:");
        this.movieTitleValue = new JLabel();
        this.showtimeRow = new JPanel(new BorderLayout());
        this.showtimeLabel = new JLabel("Showtime:");
        this.showtimeValue = new JLabel();
        this.seatsRow = new JPanel(new BorderLayout());
        this.seatsLabel = new JLabel("Selected Seats:");
        this.seatsValue = new JLabel();
        this.seatCountRow = new JPanel(new BorderLayout());
        this.seatCountLabel = new JLabel("Number of Seats:");
        this.seatCountValue = new JLabel();
        this.pricePerSeatRow = new JPanel(new BorderLayout());
        this.pricePerSeatLabel = new JLabel("Price per Seat:");
        this.pricePerSeatValue = new JLabel();
        this.separator = new JSeparator();
        this.totalPriceRow = new JPanel(new BorderLayout());
        this.totalPriceLabel = new JLabel("Total Price:");
        this.totalPriceValue = new JLabel();
        this.messageLabel = new JLabel("", SwingConstants.CENTER);
        this.buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        this.confirmButton = new JButton("Confirm Booking");
        this.cancelButton = new JButton("Cancel");
    }

    /**
     * Since we are using singleton design pattern, this UI will only be initialized once
     * movieId, showtimeId, selectedSeatIds are passed in from CustomerSeatSelectionUI
     */
    public static void initialize(String movieId, List<String> selectedSeatIds, String email) {
        CustomerBookingConfirmUI.customerBookingConfirmUI = new CustomerBookingConfirmUI();
        CustomerBookingConfirmUI.customerBookingConfirmUI.currentMovieId = movieId;
        CustomerBookingConfirmUI.customerBookingConfirmUI.currentEmail = email;
        CustomerBookingConfirmUI.customerBookingConfirmUI.currentSelectedSeatIds = selectedSeatIds;
        CustomerBookingConfirmUI.customerBookingConfirmUI.initializeAllElements();
        CustomerBookingConfirmUI.customerBookingConfirmUI.mainFrame.add(CustomerBookingConfirmUI.customerBookingConfirmUI.mainPanel);
        CustomerBookingConfirmUI.customerBookingConfirmUI.mainFrame.setModalityType(Dialog.ModalityType.APPLICATION_MODAL);
        CustomerBookingConfirmUI.customerBookingConfirmUI.mainFrame.setVisible(true);
    }

    private void initializeAllElements() {
        //initialize the main frame
        mainFrame.setTitle("Ketchup - Confirm Booking");
        mainFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        mainFrame.setSize(480, 460);
        mainFrame.setLocationRelativeTo(null);
        mainFrame.setResizable(false);

        //initialize the main content panel
        mainPanel.setLayout(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        //initialize the top bar (title + back button)
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        topPanel.add(titleLabel, BorderLayout.WEST);
        topPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 5, 0));
        mainPanel.add(topPanel, BorderLayout.NORTH);

        //initialize the booking summary rows
        Dimension labelWidth = new Dimension(140, 25);

        movieTitleLabel.setFont(new Font("Arial", Font.BOLD, 13));
        movieTitleLabel.setPreferredSize(labelWidth);
        movieTitleValue.setFont(new Font("Arial", Font.PLAIN, 13));
        movieTitleRow.add(movieTitleLabel, BorderLayout.WEST);
        movieTitleRow.add(movieTitleValue, BorderLayout.CENTER);

        showtimeLabel.setFont(new Font("Arial", Font.BOLD, 13));
        showtimeLabel.setPreferredSize(labelWidth);
        showtimeValue.setFont(new Font("Arial", Font.PLAIN, 13));
        showtimeRow.add(showtimeLabel, BorderLayout.WEST);
        showtimeRow.add(showtimeValue, BorderLayout.CENTER);

        seatsLabel.setFont(new Font("Arial", Font.BOLD, 13));
        seatsLabel.setPreferredSize(labelWidth);
        seatsValue.setFont(new Font("Arial", Font.PLAIN, 13));
        seatsRow.add(seatsLabel, BorderLayout.WEST);
        seatsRow.add(seatsValue, BorderLayout.CENTER);

        seatCountLabel.setFont(new Font("Arial", Font.BOLD, 13));
        seatCountLabel.setPreferredSize(labelWidth);
        seatCountValue.setFont(new Font("Arial", Font.PLAIN, 13));
        seatCountRow.add(seatCountLabel, BorderLayout.WEST);
        seatCountRow.add(seatCountValue, BorderLayout.CENTER);

        pricePerSeatLabel.setFont(new Font("Arial", Font.BOLD, 13));
        pricePerSeatLabel.setPreferredSize(labelWidth);
        pricePerSeatValue.setFont(new Font("Arial", Font.PLAIN, 13));
        pricePerSeatRow.add(pricePerSeatLabel, BorderLayout.WEST);
        pricePerSeatRow.add(pricePerSeatValue, BorderLayout.CENTER);

        totalPriceLabel.setFont(new Font("Arial", Font.BOLD, 15));
        totalPriceLabel.setPreferredSize(labelWidth);
        totalPriceValue.setFont(new Font("Arial", Font.BOLD, 15));
        totalPriceValue.setForeground(new Color(0, 128, 0));
        totalPriceRow.add(totalPriceLabel, BorderLayout.WEST);
        totalPriceRow.add(totalPriceValue, BorderLayout.CENTER);

        summaryPanel.setBorder(BorderFactory.createTitledBorder("Booking Summary"));
        summaryPanel.add(movieTitleRow);
        summaryPanel.add(showtimeRow);
        summaryPanel.add(seatsRow);
        summaryPanel.add(seatCountRow);
        summaryPanel.add(pricePerSeatRow);
        summaryPanel.add(separator);
        summaryPanel.add(totalPriceRow);
        summaryPanel.add(messageLabel);
        mainPanel.add(summaryPanel, BorderLayout.CENTER);

        //initialize the action buttons
        confirmButton.setPreferredSize(new Dimension(150, 32));
        cancelButton.setPreferredSize(new Dimension(100, 32));
        buttonPanel.add(confirmButton);
        buttonPanel.add(cancelButton);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        //load booking summary data into the UI fields
        loadBookingSummary();

        ////SUBSECTION - ADDING LISTENER TO THE CONFIRM BUTTON
        this.confirmButton.addActionListener(e -> {
            Movie m = MovieRepository.getMovies().get(this.currentMovieId);
            int ticketPrice = m.getSeatPrice();
            int total = currentSelectedSeatIds.size() * ticketPrice;
            Booking b = BookingRepository.addBooking(this.currentEmail, this.currentMovieId,
                    m.getShowTime().format(Movie.getDatetimeFormat()),
                    this.currentSelectedSeatIds, total);
            CustomerHomeUI.removeMovie(this.currentMovieId);
            CustomerHomeUI.addBookingRow(b);
            mainFrame.dispose();
        });

        ////SUBSECTION - ADDING LISTENER TO THE CANCEL BUTTON
        this.cancelButton.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(mainFrame, "Are you sure you want to cancel this booking?", "Confirm Cancel", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                mainFrame.dispose();
                CustomerSeatSelectionUI.initialize(this.currentMovieId, this.currentEmail);
            }
        });
    }

    //fetch all booking data from backend and populate summary fields
    private void loadBookingSummary() {
        Movie m = MovieRepository.getMovies().get(this.currentMovieId);
        this.movieTitleLabel.setText(m.getTitle());
        this.showtimeLabel.setText(m.getShowTime().format(Movie.getDatetimeFormat()));
        int ticketPrice = m.getSeatPrice();
        int total = currentSelectedSeatIds.size() * ticketPrice;
        this.pricePerSeatValue.setText(Integer.toString(ticketPrice));
        this.totalPriceValue.setText(Integer.toString(total));
        seatsValue.setText(String.join(", ", currentSelectedSeatIds));
        seatCountValue.setText(String.valueOf(currentSelectedSeatIds.size()));
    }
}