package com.ducksabervn.projects.ketchup.frontend;

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

    //movie poster placeholder
    private JLabel posterLabel;

    //movie info panel (right side of poster)
    private JPanel infoPanel;
    private JLabel movieTitleLabel;
    private JLabel movieTitleValue;
    private JLabel genreLabel;
    private JLabel genreValue;
    private JLabel durationLabel;
    private JLabel durationValue;
    private JLabel ratingLabel;
    private JLabel ratingValue;

    //movie description section
    private JPanel descriptionPanel;
    private JLabel descriptionTitleLabel;
    private JTextArea descriptionArea;
    private JScrollPane descriptionScrollPane;

    //action button at the bottom
    private JPanel buttonPanel;
    private JButton bookNowButton;

    private CustomerMovieDetailUI() {
        this.mainFrame = new JFrame("Movie Booking System - Movie Detail");
        this.mainPanel = new JPanel();
        this.topPanel = new JPanel(new BorderLayout());
        this.titleLabel = new JLabel("Movie Detail", SwingConstants.LEFT);
        this.backButton = new JButton("Back");
        this.detailPanel = new JPanel(new BorderLayout(15, 0));
        this.posterLabel = new JLabel("No Image", SwingConstants.CENTER);
        this.infoPanel = new JPanel(new GridLayout(8, 1, 4, 4));
        this.movieTitleLabel = new JLabel("Title:");
        this.movieTitleValue = new JLabel("-");
        this.genreLabel = new JLabel("Genre:");
        this.genreValue = new JLabel("-");
        this.durationLabel = new JLabel("Duration:");
        this.durationValue = new JLabel("-");
        this.ratingLabel = new JLabel("Rating:");
        this.ratingValue = new JLabel("-");
        this.descriptionPanel = new JPanel(new BorderLayout(0, 5));
        this.descriptionTitleLabel = new JLabel("Description:");
        this.descriptionArea = new JTextArea(5, 20);
        this.descriptionScrollPane = new JScrollPane(descriptionArea);
        this.buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        this.bookNowButton = new JButton("Book Now");
    }

    /**
     * Since we are using singleton design pattern, this UI will only be initialized once
     * movieId is the ID of the movie to display
     */
    public static void initialize(String movieId) {
        if (CustomerMovieDetailUI.customerMovieDetailUI == null) {
            CustomerMovieDetailUI.customerMovieDetailUI = new CustomerMovieDetailUI();
        }
        CustomerMovieDetailUI.customerMovieDetailUI.currentMovieId = movieId;
        CustomerMovieDetailUI.customerMovieDetailUI.initializeAllElements();
        CustomerMovieDetailUI.customerMovieDetailUI.mainFrame.add(CustomerMovieDetailUI.customerMovieDetailUI.mainPanel);
        CustomerMovieDetailUI.customerMovieDetailUI.mainFrame.setVisible(true);
    }

    private void initializeAllElements() {
        //initialize the main frame
        mainFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        mainFrame.setSize(600, 480);
        mainFrame.setLocationRelativeTo(null);
        mainFrame.setResizable(false);

        //initialize the main content panel
        mainPanel.setLayout(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        //initialize the top bar (title + back button)
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        backButton.setPreferredSize(new Dimension(80, 30));
        topPanel.add(titleLabel, BorderLayout.WEST);
        topPanel.add(backButton, BorderLayout.EAST);
        topPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 5, 0));
        mainPanel.add(topPanel, BorderLayout.NORTH);

        //initialize the poster placeholder
        posterLabel.setPreferredSize(new Dimension(160, 220));
        posterLabel.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        posterLabel.setOpaque(true);
        posterLabel.setBackground(Color.LIGHT_GRAY);
        // TODO: Load actual poster image from MovieService and set via new ImageIcon(...)

        //initialize the movie info panel (right side)
        movieTitleLabel.setFont(new Font("Arial", Font.BOLD, 13));
        movieTitleValue.setFont(new Font("Arial", Font.PLAIN, 13));
        genreLabel.setFont(new Font("Arial", Font.BOLD, 13));
        genreValue.setFont(new Font("Arial", Font.PLAIN, 13));
        durationLabel.setFont(new Font("Arial", Font.BOLD, 13));
        durationValue.setFont(new Font("Arial", Font.PLAIN, 13));
        ratingLabel.setFont(new Font("Arial", Font.BOLD, 13));
        ratingValue.setFont(new Font("Arial", Font.PLAIN, 13));
        infoPanel.add(movieTitleLabel);
        infoPanel.add(movieTitleValue);
        infoPanel.add(genreLabel);
        infoPanel.add(genreValue);
        infoPanel.add(durationLabel);
        infoPanel.add(durationValue);
        infoPanel.add(ratingLabel);
        infoPanel.add(ratingValue);

        //group poster + info into the detail panel
        detailPanel.add(posterLabel, BorderLayout.WEST);
        detailPanel.add(infoPanel, BorderLayout.CENTER);

        //initialize the description section
        descriptionTitleLabel.setFont(new Font("Arial", Font.BOLD, 13));
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        descriptionArea.setEditable(false);
        descriptionArea.setBackground(mainPanel.getBackground());
        descriptionPanel.add(descriptionTitleLabel, BorderLayout.NORTH);
        descriptionPanel.add(descriptionScrollPane, BorderLayout.CENTER);

        //group detail panel + description into the CENTER section
        JPanel centerPanel = new JPanel(new BorderLayout(0, 10));
        centerPanel.add(detailPanel, BorderLayout.NORTH);
        centerPanel.add(descriptionPanel, BorderLayout.CENTER);
        mainPanel.add(centerPanel, BorderLayout.CENTER);

        //initialize the book now button
        bookNowButton.setPreferredSize(new Dimension(120, 30));
        buttonPanel.add(bookNowButton);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        //load movie data into the UI fields
        loadMovieDetail();

        ////SUBSECTION - ADDING LISTENER TO THE BOOK NOW BUTTON
        this.bookNowButton.addActionListener(e -> {
            // TODO: Open CustomerShowtimeListUI with currentMovieId
        });

        ////SUBSECTION - ADDING LISTENER TO THE BACK BUTTON
        this.backButton.addActionListener(e -> {
            mainFrame.dispose();
            // TODO: Open CustomerMovieListUI.initialize(username) if not already open
        });
    }

    //fetch movie data from service and populate all UI fields
    private void loadMovieDetail() {
        // TODO: Call MovieService.getMovieById(currentMovieId) to fetch movie data
        // TODO: movieTitleValue.setText(movie.getTitle())
        // TODO: genreValue.setText(movie.getGenre())
        // TODO: durationValue.setText(movie.getDuration() + " min")
        // TODO: ratingValue.setText(movie.getRating())
        // TODO: descriptionArea.setText(movie.getDescription())

        //sample data for UI testing, remove when backend is connected
        movieTitleValue.setText("Inception");
        genreValue.setText("Sci-Fi");
        durationValue.setText("148 min");
        ratingValue.setText("PG-13");
        descriptionArea.setText("A thief who steals corporate secrets through the use of dream-sharing technology is given the inverse task of planting an idea into the mind of a C.E.O.");
    }
}