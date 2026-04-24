package com.ducksabervn.projects.ketchup.frontend;

import com.ducksabervn.projects.ketchup.backend.admin.Movie;
import com.ducksabervn.projects.ketchup.backend.admin.MovieRepository;

import javax.swing.*;
import java.awt.*;

public class AdminMovieFormUI {

    //SINGLETON DESIGN PATTERN => 1 UI INSTANCE AT A TIME
    private static AdminMovieFormUI adminMovieFormUI;

    //form mode: "ADD" or "EDIT"
    private String mode;

    //the form main frame
    private JFrame mainFrame;
    private JPanel mainPanel;

    //form window title label
    private JLabel titleLabel;

    //the movie input form panel
    private JPanel formPanel;

    //movie id row (hidden input, used only in EDIT mode)
    private String currentMovieId;

    //movie title row
    private JPanel movieTitleRow;
    private JLabel movieTitleLabel;
    private JTextField movieTitleField;

    //genre row
    private JPanel genreRow;
    private JLabel genreLabel;
    private JTextField genreField;

    //duration row
    private JPanel durationRow;
    private JLabel durationLabel;
    private JTextField durationField;

    //rating row
    private JPanel ratingRow;
    private JLabel ratingLabel;
    private JComboBox<String> ratingComboBox;

    //showtime row (format: yyyy-MM-dd HH:mm)
    private JPanel showtimeRow;
    private JLabel showtimeLabel;
    private JTextField showtimeField;

    //seat price row
    private JPanel seatPriceRow;
    private JLabel seatPriceLabel;
    private JTextField seatPriceField;

    //error/success message label
    private JLabel messageLabel;

    //action buttons
    private JPanel buttonPanel;
    private JButton saveButton;
    private JButton cancelButton;

    private AdminMovieFormUI() {
        this.mainFrame = new JFrame();
        this.mainPanel = new JPanel();
        this.titleLabel = new JLabel("", SwingConstants.CENTER);
        this.formPanel = new JPanel(new GridLayout(8, 1, 8, 8));
        this.currentMovieId = null;
        this.movieTitleRow = new JPanel(new BorderLayout(8, 0));
        this.movieTitleLabel = new JLabel("Title:");
        this.movieTitleField = new JTextField();
        this.genreRow = new JPanel(new BorderLayout(8, 0));
        this.genreLabel = new JLabel("Genre:");
        this.genreField = new JTextField();
        this.durationRow = new JPanel(new BorderLayout(8, 0));
        this.durationLabel = new JLabel("Duration (min):");
        this.durationField = new JTextField();
        this.ratingRow = new JPanel(new BorderLayout(8, 0));
        this.ratingLabel = new JLabel("Rating:");
        this.ratingComboBox = new JComboBox<>(new String[]{"G", "PG", "PG-13", "R", "NC-17"});
        this.showtimeRow = new JPanel(new BorderLayout(8, 0));
        this.showtimeLabel = new JLabel("Showtime:");
        this.showtimeField = new JTextField();
        this.seatPriceRow = new JPanel(new BorderLayout(8, 0));
        this.seatPriceLabel = new JLabel("Seat Price ($):");
        this.seatPriceField = new JTextField();
        this.messageLabel = new JLabel("", SwingConstants.CENTER);
        this.buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        this.saveButton = new JButton("Save");
        this.cancelButton = new JButton("Cancel");
    }

    /**
     * Since we are using singleton design pattern, this UI will only be initialized once
     * mode should be either "ADD" or "EDIT"
     * movieId is only required in EDIT mode, pass null for ADD mode
     */
    public static void initialize(String mode, String movieId) {
        if (AdminMovieFormUI.adminMovieFormUI == null) {
            AdminMovieFormUI.adminMovieFormUI = new AdminMovieFormUI();
        }
        AdminMovieFormUI.adminMovieFormUI.mode = mode;
        AdminMovieFormUI.adminMovieFormUI.currentMovieId = movieId;
        AdminMovieFormUI.adminMovieFormUI.initializeAllElements();
        AdminMovieFormUI.adminMovieFormUI.mainFrame.add(AdminMovieFormUI.adminMovieFormUI.mainPanel);
        AdminMovieFormUI.adminMovieFormUI.mainFrame.setVisible(true);
    }

    private void initializeAllElements() {
        //initialize the main frame
        mainFrame.setTitle("Movie Booking System - " + (mode.equals("ADD") ? "Add Movie" : "Edit Movie"));
        mainFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        mainFrame.setSize(450, 480);
        mainFrame.setLocationRelativeTo(null);
        mainFrame.setResizable(false);

        //initialize the main content panel
        mainPanel.setLayout(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        //initialize the title label
        titleLabel.setText(mode.equals("ADD") ? "Add New Movie" : "Edit Movie");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        mainPanel.add(titleLabel, BorderLayout.NORTH);

        //initialize the movie input form
        movieTitleLabel.setPreferredSize(new Dimension(110, 25));
        movieTitleRow.add(movieTitleLabel, BorderLayout.WEST);
        movieTitleRow.add(movieTitleField, BorderLayout.CENTER);

        genreLabel.setPreferredSize(new Dimension(110, 25));
        genreRow.add(genreLabel, BorderLayout.WEST);
        genreRow.add(genreField, BorderLayout.CENTER);

        durationLabel.setPreferredSize(new Dimension(110, 25));
        durationRow.add(durationLabel, BorderLayout.WEST);
        durationRow.add(durationField, BorderLayout.CENTER);

        ratingLabel.setPreferredSize(new Dimension(110, 25));
        ratingRow.add(ratingLabel, BorderLayout.WEST);
        ratingRow.add(ratingComboBox, BorderLayout.CENTER);

        showtimeLabel.setPreferredSize(new Dimension(110, 25));
        showtimeRow.add(showtimeLabel, BorderLayout.WEST);
        showtimeRow.add(showtimeField, BorderLayout.CENTER);

        seatPriceLabel.setPreferredSize(new Dimension(110, 25));
        seatPriceRow.add(seatPriceLabel, BorderLayout.WEST);
        seatPriceRow.add(seatPriceField, BorderLayout.CENTER);

        messageLabel.setFont(new Font("Arial", Font.PLAIN, 12));

        formPanel.add(movieTitleRow);
        formPanel.add(genreRow);
        formPanel.add(durationRow);
        formPanel.add(ratingRow);
        formPanel.add(showtimeRow);
        formPanel.add(seatPriceRow);
        formPanel.add(messageLabel);
        mainPanel.add(formPanel, BorderLayout.CENTER);

        //initialize the action buttons
        saveButton.setPreferredSize(new Dimension(100, 30));
        cancelButton.setPreferredSize(new Dimension(100, 30));
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        //if EDIT mode, pre-fill the form with existing movie data
        if (mode.equals("EDIT")) {
            Movie m = MovieRepository.getMovies().get(this.currentMovieId);
            this.movieTitleField.setText(m.getTitle());
            this.genreField.setText(m.getGenre());
            this.durationField.setText(Integer.toString(m.getDuration()));
            this.ratingComboBox.setSelectedItem(m.getRating());
            this.showtimeField.setText(m.getShowTime());
            this.seatPriceField.setText(Integer.toString(m.getSeatPrice()));
        }

        ////SUBSECTION - ADDING LISTENER TO THE SAVE BUTTON
        this.saveButton.addActionListener(e -> {
            String title        = movieTitleField.getText().trim();
            String genre        = genreField.getText().trim();
            String durationStr  = durationField.getText().trim();
            String rating       = (String) ratingComboBox.getSelectedItem();
            String showtime     = showtimeField.getText().trim();
            String seatPriceStr = seatPriceField.getText().trim();

            if (mode.equals("ADD")) {
                Movie m = MovieRepository.addMovie(title, genre, Integer.parseInt(durationStr), rating, showtime, "",
                        Integer.parseInt(seatPriceStr));
                AdminMovieListUI.updateTable(m);
            } else {
                Movie edited = MovieRepository.addMovie(title, genre, Integer.parseInt(durationStr), rating, showtime, "",
                        Integer.parseInt(seatPriceStr));
                MovieRepository.editMovie(this.currentMovieId, edited);
            }
            mainFrame.dispose();
        });

        ////SUBSECTION - ADDING LISTENER TO THE CANCEL BUTTON
        this.cancelButton.addActionListener(e -> {
            mainFrame.dispose();
        });
    }
}