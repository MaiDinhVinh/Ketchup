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
 * File Name:       CustomerMovieDetailUI.java
 * Developer:       Tran Phan Anh*, Nguyen The Khoi Nguyen*, Nguyen Dinh Quy*,
 *                  Mai Dinh Vinh* (* equal contributions)
 * Description:     Read-only detail screen presenting full information about
 *                  a selected movie screening to the customer, including title,
 *                  genre, duration, rating, showtime, and seat price.
 ******************************************************************************/

package com.ducksabervn.projects.ketchup.frontend;

import com.ducksabervn.projects.ketchup.backend.model.Movie;
import com.ducksabervn.projects.ketchup.backend.repositories.MovieRepository;

import javax.swing.*;
import java.awt.*;

/**
 * A read-only detail window that displays the full information of a selected
 * movie screening. Opened from {@link CustomerHomeUI} when the customer clicks
 * "View Details" on a movie row. All fields are populated from
 * {@link MovieRepository} using the provided movie ID and are presented in a
 * labeled two-column layout. The window is non-resizable and disposed when
 * the customer clicks Back.
 */
public class CustomerMovieDetailUI {

    /**
     * The sole instance of {@code CustomerMovieDetailUI}, replaced on each
     * call to {@link #initialize(String)}.
     */
    private static CustomerMovieDetailUI customerMovieDetailUI;

    /**
     * The ID of the movie whose details are being displayed, used to
     * retrieve the corresponding {@link Movie} from {@link MovieRepository}.
     */
    private String currentMovieId;

    /** The main application window for the movie detail screen. */
    private JFrame mainFrame;

    /** The root content panel using {@link BorderLayout}. */
    private JPanel mainPanel;

    /** Panel containing the screen heading and back button. */
    private JPanel topPanel;

    /** Label displaying the "Movie Detail" screen heading. */
    private JLabel titleLabel;

    /** Button that closes the detail window and returns to {@link CustomerHomeUI}. */
    private JButton backButton;

    /**
     * Panel containing all labeled movie information rows, laid out using
     * {@link BoxLayout} along the Y axis.
     */
    private JPanel detailPanel;

    /** Bold label identifying the movie title field. */
    private JLabel movieTitleLabel;

    /** Label displaying the title of the selected movie. */
    private JLabel movieTitleValue;

    /** Bold label identifying the genre field. */
    private JLabel genreLabel;

    /** Label displaying the genre of the selected movie. */
    private JLabel genreValue;

    /** Bold label identifying the duration field. */
    private JLabel durationLabel;

    /** Label displaying the runtime of the selected movie in minutes. */
    private JLabel durationValue;

    /** Bold label identifying the rating field. */
    private JLabel ratingLabel;

    /** Label displaying the age rating of the selected movie. */
    private JLabel ratingValue;

    /** Bold label identifying the showtime field. */
    private JLabel showtimeLabel;

    /** Label displaying the formatted showtime of the selected screening. */
    private JLabel showtimeValue;

    /** Bold label identifying the seat price field. */
    private JLabel seatPriceLabel;

    /**
     * Label displaying the seat price of the selected screening, rendered
     * in green bold text to highlight it as a key purchasing detail.
     */
    private JLabel seatPriceValue;

    /**
     * Panel containing the Back button, aligned to the right at the
     * bottom of the window.
     */
    private JPanel buttonPanel;

    /**
     * Private constructor that initializes all Swing components with default
     * values. Customization and layout are handled by
     * {@link #initializeAllElements()}.
     */
    private CustomerMovieDetailUI() {
        this.mainFrame = new JFrame("Ketchup");
        this.mainPanel = new JPanel();
        this.topPanel = new JPanel(new BorderLayout());
        this.titleLabel = new JLabel("Movie Detail", SwingConstants.LEFT);
        this.backButton = new JButton("Back");
        this.detailPanel = new JPanel();
        this.movieTitleLabel = new JLabel("Title:");
        this.movieTitleValue = new JLabel("-");
        this.genreLabel = new JLabel("Genre:");
        this.genreValue = new JLabel("-");
        this.durationLabel = new JLabel("Duration:");
        this.durationValue = new JLabel("-");
        this.ratingLabel = new JLabel("Rating:");
        this.ratingValue = new JLabel("-");
        this.showtimeLabel = new JLabel("Showtime:");
        this.showtimeValue = new JLabel("-");
        this.seatPriceLabel = new JLabel("Seat Price:");
        this.seatPriceValue = new JLabel("-");
        this.buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    }

    /**
     * Creates a new {@code CustomerMovieDetailUI} instance, loads the details
     * of the specified movie, and makes the window visible.
     *
     * @param movieId the ID of the movie to display, corresponding to a key
     *                in {@link MovieRepository}
     */
    public static void initialize(String movieId) {
        CustomerMovieDetailUI.customerMovieDetailUI = new CustomerMovieDetailUI();
        CustomerMovieDetailUI.customerMovieDetailUI.currentMovieId = movieId;
        CustomerMovieDetailUI.customerMovieDetailUI.initializeAllElements();
        CustomerMovieDetailUI.customerMovieDetailUI.mainFrame.add(
                CustomerMovieDetailUI.customerMovieDetailUI.mainPanel);
        CustomerMovieDetailUI.customerMovieDetailUI.mainFrame.setVisible(true);
    }

    /**
     * Configures and lays out all UI components within the main frame,
     * populates all movie detail fields from {@link MovieRepository}, and
     * attaches the Back button action listener.
     */
    private void initializeAllElements() {
        mainFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        mainFrame.setSize(480, 380);
        mainFrame.setLocationRelativeTo(null);
        mainFrame.setResizable(false);

        mainPanel.setLayout(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        backButton.setPreferredSize(new Dimension(80, 30));
        topPanel.add(titleLabel, BorderLayout.WEST);
        topPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 5, 0));
        mainPanel.add(topPanel, BorderLayout.NORTH);

        detailPanel.setLayout(new BoxLayout(detailPanel, BoxLayout.Y_AXIS));
        detailPanel.setBorder(BorderFactory.createTitledBorder("Movie Information"));

        Dimension labelSize = new Dimension(100, 28);
        detailPanel.add(buildInfoRow(movieTitleLabel, movieTitleValue, labelSize, false));
        detailPanel.add(buildInfoRow(genreLabel, genreValue, labelSize, false));
        detailPanel.add(buildInfoRow(durationLabel, durationValue, labelSize, false));
        detailPanel.add(buildInfoRow(ratingLabel, ratingValue, labelSize, false));
        detailPanel.add(buildInfoRow(showtimeLabel, showtimeValue, labelSize, false));
        detailPanel.add(buildInfoRow(seatPriceLabel, seatPriceValue, labelSize, true));

        mainPanel.add(detailPanel, BorderLayout.CENTER);

        buttonPanel.add(backButton);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        loadMovieDetail();

        ////SUBSECTION - ADDING LISTENER TO THE BACK BUTTON
        this.backButton.addActionListener(e -> {
            mainFrame.dispose();
        });
    }

    /**
     * Constructs a single labeled information row consisting of a bold label
     * on the left and a value label on the right, laid out using
     * {@link BorderLayout}. Optionally renders the value in green bold text
     * to highlight it as a key field (used for seat price).
     *
     * @param label          the bold descriptor label displayed on the left
     * @param value          the value label displayed on the right
     * @param labelSize      the preferred fixed size applied to the descriptor label
     *                       to ensure consistent column alignment across all rows
     * @param highlightValue {@code true} to render the value in green bold text,
     *                       {@code false} for plain text
     * @return a {@link JPanel} containing the assembled label-value row
     */
    private JPanel buildInfoRow(JLabel label, JLabel value, Dimension labelSize, boolean highlightValue) {
        JPanel row = new JPanel(new BorderLayout(8, 0));
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 32));
        row.setBorder(BorderFactory.createEmptyBorder(2, 6, 2, 6));
        label.setFont(new Font("Arial", Font.BOLD, 13));
        label.setPreferredSize(labelSize);
        value.setFont(new Font("Arial", highlightValue ? Font.BOLD : Font.PLAIN, 13));
        if (highlightValue) value.setForeground(new Color(0, 128, 0));
        row.add(label, BorderLayout.WEST);
        row.add(value, BorderLayout.CENTER);
        return row;
    }

    /**
     * Retrieves the {@link Movie} identified by {@link #currentMovieId} from
     * {@link MovieRepository} and populates all value labels in the detail
     * panel with the corresponding movie data.
     */
    private void loadMovieDetail() {
        Movie m = MovieRepository.getMovies().get(this.currentMovieId);
        movieTitleValue.setText(m.getTitle());
        genreValue.setText(m.getGenre());
        durationValue.setText(m.getDuration() + " min");
        ratingValue.setText(m.getRating());
        showtimeValue.setText(m.getShowTime().format(Movie.getDatetimeFormat()));
        seatPriceValue.setText("$" + m.getSeatPrice());
    }
}