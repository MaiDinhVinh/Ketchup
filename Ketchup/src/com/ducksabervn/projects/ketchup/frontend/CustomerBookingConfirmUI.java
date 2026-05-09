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
 * File Name:       CustomerBookingConfirmUI.java
 * Developer:       Tran Phan Anh*, Nguyen The Khoi Nguyen*, Nguyen Dinh Quy*,
 *                  Mai Dinh Vinh* (* equal contributions)
 * Description:     Modal dialog presenting a full booking summary to the
 *                  customer before final confirmation, allowing them to either
 *                  commit the booking to the repository or return to seat
 *                  selection to revise their choices.
 ******************************************************************************/

package com.ducksabervn.projects.ketchup.frontend;

import com.ducksabervn.projects.ketchup.backend.model.Movie;
import com.ducksabervn.projects.ketchup.backend.repositories.MovieRepository;
import com.ducksabervn.projects.ketchup.backend.model.Booking;
import com.ducksabervn.projects.ketchup.backend.repositories.BookingRepository;

import javax.swing.*;
import java.awt.*;
import java.util.HashSet;

/**
 * Modal confirmation dialog shown to the customer as the final step of the
 * booking flow. Displays a full summary of the pending booking — including
 * movie title, showtime, selected seats, seat count, per-seat price, and
 * total price — before the customer commits or cancels. On confirmation,
 * the booking is added to {@link BookingRepository}, the movie is removed
 * from the available listings in {@link CustomerHomeUI}, and the new booking
 * appears in the customer's booking history.
 */
public class CustomerBookingConfirmUI {

    /**
     * The sole instance of {@code CustomerBookingConfirmUI}, replaced on
     * each call to {@link #initialize(String, HashSet, String)}.
     */
    private static CustomerBookingConfirmUI customerBookingConfirmUI;

    /**
     * The ID of the movie being booked, used to retrieve movie details
     * from {@link MovieRepository}.
     */
    private String currentMovieId;

    /**
     * The showtime ID associated with the booking context.
     * Retained for reference within the confirmation flow.
     */
    private String currentShowtimeId;

    /**
     * The email address of the currently logged-in customer, used to
     * associate the confirmed booking with the correct account.
     */
    private String currentEmail;

    /**
     * The set of seat IDs selected by the customer in
     * {@link CustomerSeatSelectionUI}, passed in at initialization.
     */
    private HashSet<String> currentSelectedSeatIds;

    /** The main modal dialog window hosting all confirmation UI components. */
    private JDialog mainFrame;

    /** The root content panel using {@link BorderLayout}. */
    private JPanel mainPanel;

    /** Panel containing the confirmation screen heading. */
    private JPanel topPanel;

    /** Label displaying the "Booking Confirmation" screen heading. */
    private JLabel titleLabel;

    /**
     * Panel containing all booking summary rows, laid out in a
     * {@link GridLayout} to align labels and values consistently.
     */
    private JPanel summaryPanel;

    /** Row panel grouping the movie title label and value. */
    private JPanel movieTitleRow;

    /** Bold label identifying the movie title field. */
    private JLabel movieTitleLabel;

    /** Label displaying the title of the movie being booked. */
    private JLabel movieTitleValue;

    /** Row panel grouping the showtime label and value. */
    private JPanel showtimeRow;

    /** Bold label identifying the showtime field. */
    private JLabel showtimeLabel;

    /** Label displaying the formatted showtime of the selected screening. */
    private JLabel showtimeValue;

    /** Row panel grouping the selected seats label and value. */
    private JPanel seatsRow;

    /** Bold label identifying the selected seats field. */
    private JLabel seatsLabel;

    /** Label displaying the comma-separated list of selected seat IDs. */
    private JLabel seatsValue;

    /** Row panel grouping the seat count label and value. */
    private JPanel seatCountRow;

    /** Bold label identifying the number of seats field. */
    private JLabel seatCountLabel;

    /** Label displaying the total number of seats selected. */
    private JLabel seatCountValue;

    /** Row panel grouping the per-seat price label and value. */
    private JPanel pricePerSeatRow;

    /** Bold label identifying the per-seat price field. */
    private JLabel pricePerSeatLabel;

    /** Label displaying the price charged per individual seat. */
    private JLabel pricePerSeatValue;

    /**
     * Visual separator drawn between the per-seat price row and the
     * total price row to distinguish the subtotals from the final amount.
     */
    private JSeparator separator;

    /** Row panel grouping the total price label and value. */
    private JPanel totalPriceRow;

    /** Bold label identifying the total price field. */
    private JLabel totalPriceLabel;

    /**
     * Label displaying the final total price in green bold text,
     * calculated as seat count × per-seat price.
     */
    private JLabel totalPriceValue;

    /**
     * Label reserved for displaying success or error messages
     * within the summary panel.
     */
    private JLabel messageLabel;

    /**
     * Panel containing the Confirm Booking and Cancel buttons,
     * centered at the bottom of the dialog.
     */
    private JPanel buttonPanel;

    /**
     * Button that finalizes the booking by adding it to
     * {@link BookingRepository} and updating {@link CustomerHomeUI}.
     */
    private JButton confirmButton;

    /**
     * Button that cancels the confirmation and reopens
     * {@link CustomerSeatSelectionUI} so the customer can revise their seat choices.
     */
    private JButton cancelButton;

    /**
     * Private constructor that initializes all Swing components with default
     * values. Customization and layout are handled by
     * {@link #initializeAllElements()}.
     */
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
     * Creates a new {@code CustomerBookingConfirmUI} instance, populates it
     * with the provided booking context, and displays it as an
     * application-modal dialog.
     *
     * @param movieId         the ID of the movie being booked
     * @param selectedSeatIds the set of seat IDs chosen by the customer
     *                        in {@link CustomerSeatSelectionUI}
     * @param email           the email address of the currently logged-in customer
     */
    public static void initialize(String movieId, HashSet<String> selectedSeatIds, String email) {
        CustomerBookingConfirmUI.customerBookingConfirmUI = new CustomerBookingConfirmUI();
        CustomerBookingConfirmUI.customerBookingConfirmUI.currentMovieId = movieId;
        CustomerBookingConfirmUI.customerBookingConfirmUI.currentEmail = email;
        CustomerBookingConfirmUI.customerBookingConfirmUI.currentSelectedSeatIds = selectedSeatIds;
        CustomerBookingConfirmUI.customerBookingConfirmUI.initializeAllElements();
        CustomerBookingConfirmUI.customerBookingConfirmUI.mainFrame.add(
                CustomerBookingConfirmUI.customerBookingConfirmUI.mainPanel);
        CustomerBookingConfirmUI.customerBookingConfirmUI.mainFrame.setModalityType(
                Dialog.ModalityType.APPLICATION_MODAL);
        CustomerBookingConfirmUI.customerBookingConfirmUI.mainFrame.setVisible(true);
    }

    /**
     * Configures and lays out all UI components within the dialog, loads
     * the booking summary data into the display fields, and attaches action
     * listeners to the Confirm and Cancel buttons.
     * <p>
     * On confirm: creates a new {@link Booking} via
     * {@link BookingRepository#addBooking}, removes the booked movie from the
     * customer's available movie list via {@link CustomerHomeUI#removeMovie},
     * appends the new booking to the history table via
     * {@link CustomerHomeUI#addBookingRow}, then closes the dialog.
     * <p>
     * On cancel: prompts the customer for confirmation, and if confirmed,
     * closes this dialog and reopens {@link CustomerSeatSelectionUI} to
     * allow seat revision.
     */
    private void initializeAllElements() {
        mainFrame.setTitle("Ketchup - Confirm Booking");
        mainFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        mainFrame.setSize(480, 460);
        mainFrame.setLocationRelativeTo(null);
        mainFrame.setResizable(false);

        mainPanel.setLayout(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        topPanel.add(titleLabel, BorderLayout.WEST);
        topPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 5, 0));
        mainPanel.add(topPanel, BorderLayout.NORTH);

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

        confirmButton.setPreferredSize(new Dimension(150, 32));
        cancelButton.setPreferredSize(new Dimension(100, 32));
        buttonPanel.add(confirmButton);
        buttonPanel.add(cancelButton);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        loadBookingSummary();

        ////SUBSECTION - ADDING LISTENER TO THE CONFIRM BUTTON
        this.confirmButton.addActionListener(e -> {
            Movie m = MovieRepository.getMovies().get(this.currentMovieId);
            int ticketPrice = m.getSeatPrice();
            int total = BookingRepository.calculateTotalPrice(currentSelectedSeatIds, ticketPrice);
            Booking b = BookingRepository.addBooking(this.currentEmail, this.currentMovieId,
                    m.getShowTime().format(Movie.getDatetimeFormat()),
                    this.currentSelectedSeatIds, total);
            CustomerHomeUI.removeMovie(this.currentMovieId);
            CustomerHomeUI.addBookingRow(b);
            mainFrame.dispose();
        });

        ////SUBSECTION - ADDING LISTENER TO THE CANCEL BUTTON
        this.cancelButton.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(mainFrame,
                    "Are you sure you want to cancel this booking?",
                    "Confirm Cancel", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                mainFrame.dispose();
                CustomerSeatSelectionUI.initialize(this.currentMovieId, this.currentEmail);
            }
        });
    }

    /**
     * Retrieves the movie and seat data for the current booking context and
     * populates all summary display labels in the dialog. Computes the total
     * price using {@link BookingRepository#calculateTotalPrice}.
     */
    private void loadBookingSummary() {
        Movie m = MovieRepository.getMovies().get(this.currentMovieId);
        this.movieTitleValue.setText(m.getTitle());
        this.showtimeValue.setText(m.getShowTime().format(Movie.getDatetimeFormat()));
        int ticketPrice = m.getSeatPrice();
        int total = BookingRepository.calculateTotalPrice(currentSelectedSeatIds, ticketPrice);
        this.pricePerSeatValue.setText(Integer.toString(ticketPrice));
        this.totalPriceValue.setText(Integer.toString(total));
        seatsValue.setText(String.join(", ", currentSelectedSeatIds));
        seatCountValue.setText(String.valueOf(currentSelectedSeatIds.size()));
    }
}