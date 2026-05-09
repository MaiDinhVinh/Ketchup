package com.ducksabervn.projects.ketchup.frontend;

import com.ducksabervn.projects.ketchup.backend.movie.Movie;
import com.ducksabervn.projects.ketchup.backend.movie.MovieRepository;
import com.ducksabervn.projects.ketchup.backend.ui.DisplayMessage;

import javax.swing.*;
import java.awt.*;
import java.time.DateTimeException;
import java.time.LocalDateTime;

public class AdminMovieFormUI {

    private static AdminMovieFormUI adminMovieFormUI;

    //form mode: "ADD" or "EDIT"
    private String mode;

    //the form main frame
    private JDialog mainFrame;
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
    private JLabel showtimeHintLabel;

    //seat price row
    private JPanel seatPriceRow;
    private JLabel seatPriceLabel;
    private JTextField seatPriceField;

    //action buttons
    private JPanel buttonPanel;
    private JButton saveButton;
    private JButton cancelButton;

    private AdminMovieFormUI() {
        this.mainFrame = new JDialog();
        this.mainPanel = new JPanel();
        this.titleLabel = new JLabel("", SwingConstants.CENTER);
        this.formPanel = new JPanel();
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
        this.showtimeHintLabel = new JLabel("*Please use the format: yyyy-MM-dd HH:mm");
        this.seatPriceRow = new JPanel(new BorderLayout(8, 0));
        this.seatPriceLabel = new JLabel("Seat Price ($):");
        this.seatPriceField = new JTextField();
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
        AdminMovieFormUI.adminMovieFormUI = new AdminMovieFormUI();
        AdminMovieFormUI.adminMovieFormUI.mode = mode;
        AdminMovieFormUI.adminMovieFormUI.currentMovieId = movieId;
        AdminMovieFormUI.adminMovieFormUI.initializeAllElements();
        AdminMovieFormUI.adminMovieFormUI.mainFrame.add(AdminMovieFormUI.adminMovieFormUI.mainPanel);
        AdminMovieFormUI.adminMovieFormUI.mainFrame.setModalityType(Dialog.ModalityType.APPLICATION_MODAL);
        AdminMovieFormUI.adminMovieFormUI.mainFrame.setVisible(true);
    }

    private void initializeAllElements() {
        //initialize the main frame
        mainFrame.setTitle("Ketchup");
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

        showtimeHintLabel.setForeground(Color.RED);
        showtimeHintLabel.setFont(new Font("Arial", Font.ITALIC, 13));

        JPanel showtimeWrapper = new JPanel(new BorderLayout(0, 2));
        showtimeWrapper.add(showtimeRow, BorderLayout.NORTH);
        showtimeWrapper.add(showtimeHintLabel, BorderLayout.SOUTH);

        seatPriceLabel.setPreferredSize(new Dimension(110, 25));
        seatPriceRow.add(seatPriceLabel, BorderLayout.WEST);
        seatPriceRow.add(seatPriceField, BorderLayout.CENTER);

        Dimension rowSize = new Dimension(Integer.MAX_VALUE, 30);
        Dimension showtimeRowSize = new Dimension(Integer.MAX_VALUE, 55);

        movieTitleRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        genreRow.setMaximumSize(rowSize);
        durationRow.setMaximumSize(rowSize);
        ratingRow.setMaximumSize(rowSize);
        showtimeWrapper.setMaximumSize(showtimeRowSize);
        seatPriceRow.setMaximumSize(rowSize);

        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));

        formPanel.add(movieTitleRow);
        formPanel.add(Box.createVerticalStrut(8));
        formPanel.add(genreRow);
        formPanel.add(Box.createVerticalStrut(8));
        formPanel.add(durationRow);
        formPanel.add(Box.createVerticalStrut(8));
        formPanel.add(ratingRow);
        formPanel.add(Box.createVerticalStrut(8));
        formPanel.add(showtimeWrapper);
        formPanel.add(Box.createVerticalStrut(8));
        formPanel.add(seatPriceRow);
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
            this.showtimeField.setText(m.getShowTime().format(Movie.getDatetimeFormat()));
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
            if(title.isEmpty() || genre.isEmpty() || durationStr.isEmpty() || rating.isEmpty() ||
            showtime.isEmpty() || seatPriceStr.isEmpty()){
                DisplayMessage.displayError(this.mainFrame, "Required field must not be empty");
            }else{
                boolean failed = false;
                try{
                    Integer.parseInt(seatPriceStr);
                    Integer.parseInt(durationStr);
                    LocalDateTime.parse(showtime, Movie.getDatetimeFormat());
                }catch(NumberFormatException | DateTimeException ex){
                    if(ex instanceof NumberFormatException){
                        DisplayMessage.displayError(this.mainFrame, "Invalid seat price/duration value");
                    }else{
                        DisplayMessage.displayError(this.mainFrame, "Invalid showtime value");
                    }
                    failed = true;
                }
                if(!failed){
                    if (mode.equals("ADD")) {
                        Movie m = MovieRepository.addMovie(title, genre, Integer.parseInt(durationStr), rating, showtime, "",
                                Integer.parseInt(seatPriceStr));
                        AdminMovieListUI.addMovieRow(m);
                    } else {
                        Movie edited = new Movie(this.currentMovieId, title, genre, Integer.parseInt(durationStr), rating, showtime, "",
                                Integer.parseInt(seatPriceStr));
                        MovieRepository.editMovie(this.currentMovieId, edited);
                    }
                    mainFrame.dispose();
                }
            }
        });

        ////SUBSECTION - ADDING LISTENER TO THE CANCEL BUTTON
        this.cancelButton.addActionListener(e -> {
            mainFrame.dispose();
        });
    }
}