package com.ducksabervn.projects.ketchup.frontend;

import com.ducksabervn.projects.ketchup.backend.admin.Movie;
import com.ducksabervn.projects.ketchup.backend.admin.MovieRepository;

import javax.swing.*;
import java.awt.*;

public class CustomerMovieDetailUI {

    //SINGLETON DESIGN PATTERN => 1 UI INSTANCE AT A TIME
    private static CustomerMovieDetailUI customerMovieDetailUI;

    //the movie ID being viewed
    private String currentMovieId;

    //the main frame
    private JFrame mainFrame;
    private JPanel mainPanel;

    //top bar: title label + back button
    private JPanel topPanel;
    private JLabel titleLabel;
    private JButton backButton;

    //movie detail section
    private JPanel detailPanel;

    //movie info rows (label + value side by side)
    private JLabel movieTitleLabel;
    private JLabel movieTitleValue;
    private JLabel genreLabel;
    private JLabel genreValue;
    private JLabel durationLabel;
    private JLabel durationValue;
    private JLabel ratingLabel;
    private JLabel ratingValue;
    private JLabel showtimeLabel;
    private JLabel showtimeValue;
    private JLabel seatPriceLabel;
    private JLabel seatPriceValue;

    //action button at the bottom
    private JPanel buttonPanel;

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
     * Since we are using singleton design pattern, this UI will only be initialized once
     * movieId is the ID of the movie to display
     */
    public static void initialize(String movieId) {
        CustomerMovieDetailUI.customerMovieDetailUI = new CustomerMovieDetailUI();
        CustomerMovieDetailUI.customerMovieDetailUI.currentMovieId = movieId;
        CustomerMovieDetailUI.customerMovieDetailUI.initializeAllElements();
        CustomerMovieDetailUI.customerMovieDetailUI.mainFrame.add(CustomerMovieDetailUI.customerMovieDetailUI.mainPanel);
        CustomerMovieDetailUI.customerMovieDetailUI.mainFrame.setVisible(true);
    }

    private void initializeAllElements() {
        //initialize the main frame
        mainFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        mainFrame.setSize(480, 380);
        mainFrame.setLocationRelativeTo(null);
        mainFrame.setResizable(false);

        //initialize the main content panel
        mainPanel.setLayout(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        //initialize the top bar (title)
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        backButton.setPreferredSize(new Dimension(80, 30));
        topPanel.add(titleLabel, BorderLayout.WEST);
        topPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 5, 0));
        mainPanel.add(topPanel, BorderLayout.NORTH);

        //initialize the detail panel using BoxLayout so all rows are visible
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

        //initialize the back button
        buttonPanel.add(backButton);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        //load movie data into the UI fields
        loadMovieDetail();

        ////SUBSECTION - ADDING LISTENER TO THE BACK BUTTON
        this.backButton.addActionListener(e -> {
            mainFrame.dispose();
        });
    }

    //build a single info row with a bold label on the left and a value on the right
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

    //fetch movie data from service and populate all UI fields
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