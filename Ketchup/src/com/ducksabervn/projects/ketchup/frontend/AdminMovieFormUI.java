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
 * File Name:       AdminMovieFormUI.java
 * Developer:       Tran Phan Anh*, Nguyen The Khoi Nguyen*, Nguyen Dinh Quy*,
 *                  Mai Dinh Vinh* (* equal contributions)
 * Description:     Modal dialog form used by administrators to add a new movie
 *                  screening or edit an existing one, with input validation
 *                  before committing changes to the repository.
 ******************************************************************************/

package com.ducksabervn.projects.ketchup.frontend;

import com.ducksabervn.projects.ketchup.backend.model.Movie;
import com.ducksabervn.projects.ketchup.backend.repositories.MovieRepository;
import com.ducksabervn.projects.ketchup.frontend.util.DisplayMessage;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.time.DateTimeException;
import java.time.LocalDateTime;
import java.util.stream.Collectors;

/**
 * Modal dialog that provides a form for administrators to either add a new
 * movie screening or edit an existing one. Operates in two modes controlled
 * by the {@code mode} field: {@code "ADD"} initializes an empty form, while
 * {@code "EDIT"} pre-fills the form with the selected movie's current data.
 * Input validation is performed before any changes are committed to
 * {@link MovieRepository}.
 */
public class AdminMovieFormUI {

    /**
     * The sole instance of {@code AdminMovieFormUI}, replaced on each
     * call to {@link #initialize(String, String)}.
     */
    private static AdminMovieFormUI adminMovieFormUI;

    /**
     * The operational mode of this form. Must be either {@code "ADD"}
     * for creating a new movie, or {@code "EDIT"} for modifying an
     * existing one.
     */
    private String mode;

    /**
     * The main modal dialog window that hosts all form components.
     */
    private JDialog mainFrame;

    /**
     * The root content panel using {@link BorderLayout} to arrange
     * the title, form, and button sections.
     */
    private JPanel mainPanel;

    /**
     * The heading label displaying either {@code "Add New Movie"} or
     * {@code "Edit Movie"} depending on the current mode.
     */
    private JLabel titleLabel;

    /**
     * The panel containing all labeled input rows, laid out using
     * {@link BoxLayout} along the Y axis.
     */
    private JPanel formPanel;

    /**
     * The movie ID of the record being edited. {@code null} in {@code "ADD"} mode.
     */
    private String currentMovieId;

    /** Row panel grouping the movie title label and text field. */
    private JPanel movieTitleRow;

    /** Label identifying the movie title input field. */
    private JLabel movieTitleLabel;

    /** Text field for entering or editing the movie title. */
    private JTextField movieTitleField;

    /** Row panel grouping the genre label and text field. */
    private JPanel genreRow;

    /** Label identifying the genre input field. */
    private JLabel genreLabel;

    /** Text field for entering or editing the movie genre. */
    private JTextField genreField;

    /** Row panel grouping the duration label and text field. */
    private JPanel durationRow;

    /** Label identifying the duration input field. */
    private JLabel durationLabel;

    /** Text field for entering or editing the movie duration in minutes. */
    private JTextField durationField;

    /** Row panel grouping the rating label and combo box. */
    private JPanel ratingRow;

    /** Label identifying the rating selection combo box. */
    private JLabel ratingLabel;

    /**
     * Combo box allowing the admin to select the movie's age rating
     * from the standard set: G, PG, PG-13, R, NC-17.
     */
    private JComboBox<String> ratingComboBox;

    /** Row panel grouping the showtime label and text field. */
    private JPanel showtimeRow;

    /** Label identifying the showtime input field. */
    private JLabel showtimeLabel;

    /**
     * Text field for entering or editing the movie showtime.
     * Must follow the {@code yyyy-MM-dd HH:mm} format.
     */
    private JTextField showtimeField;

    /**
     * Hint label displayed below the showtime field reminding the admin
     * of the required date-time format.
     */
    private JLabel showtimeHintLabel;

    /** Row panel grouping the seat price label and text field. */
    private JPanel seatPriceRow;

    /** Label identifying the seat price input field. */
    private JLabel seatPriceLabel;

    /** Text field for entering or editing the price per seat in dollars. */
    private JTextField seatPriceField;

    /**
     * Panel containing the Save and Cancel buttons, centered at the
     * bottom of the dialog.
     */
    private JPanel buttonPanel;

    /**
     * Button that validates all form inputs and, if valid, commits the
     * new or edited movie to {@link MovieRepository}.
     */
    private JButton saveButton;

    /**
     * Button that discards all changes and closes the dialog without
     * modifying any data.
     */
    private JButton cancelButton;

    /**
     * Private constructor that initializes all Swing components with
     * default values. Customization and layout are handled by
     * {@link #initializeAllElements()}.
     */
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
     * Creates a new {@code AdminMovieFormUI} instance, configures it for the
     * given mode and movie ID, and displays it as an application-modal dialog.
     * In {@code "EDIT"} mode, the form is pre-filled with the current data of
     * the movie identified by {@code movieId}.
     *
     * @param mode    the operational mode; must be {@code "ADD"} or {@code "EDIT"}
     * @param movieId the ID of the movie to edit in {@code "EDIT"} mode;
     *                pass {@code null} for {@code "ADD"} mode
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

    /**
     * Configures and lays out all UI components within the dialog. Sets up
     * the form rows, attaches the Save and Cancel action listeners, and
     * pre-fills the form fields when operating in {@code "EDIT"} mode.
     * <p>
     * The Save button performs the following validation before committing:
     *   All fields must be non-empty.
     *   Duration and seat price must be valid integers.
     *   Showtime must conform to the {@code yyyy-MM-dd HH:mm} format.
     * On success in {@code "ADD"} mode, the new movie is added to
     * {@link MovieRepository} and a row is appended to {@link AdminMovieListUI}.
     * In {@code "EDIT"} mode, the existing record in {@link MovieRepository}
     * is replaced with the updated {@link Movie} object.
     */
    private void initializeAllElements() {
        mainFrame.setTitle("Ketchup");
        mainFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        mainFrame.setSize(450, 480);
        mainFrame.setLocationRelativeTo(null);
        mainFrame.setResizable(false);

        mainPanel.setLayout(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        titleLabel.setText(mode.equals("ADD") ? "Add New Movie" : "Edit Movie");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        mainPanel.add(titleLabel, BorderLayout.NORTH);

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

        saveButton.setPreferredSize(new Dimension(100, 30));
        cancelButton.setPreferredSize(new Dimension(100, 30));
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

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
            if (title.isEmpty() || genre.isEmpty() || durationStr.isEmpty() || rating.isEmpty() ||
                    showtime.isEmpty() || seatPriceStr.isEmpty()) {
                DisplayMessage.displayError(this.mainFrame, "Required field must not be empty");
            } else {
                boolean failed = false;
                try {
                    Integer.parseInt(seatPriceStr);
                    Integer.parseInt(durationStr);
                    LocalDateTime.parse(showtime, Movie.getDatetimeFormat());
                } catch (NumberFormatException | DateTimeException ex) {
                    if (ex instanceof NumberFormatException) {
                        DisplayMessage.displayError(this.mainFrame, "Invalid seat price/duration value");
                    } else {
                        DisplayMessage.displayError(this.mainFrame, "Invalid showtime value");
                    }
                    failed = true;
                }
                if (!failed) {
                    if (mode.equals("ADD")) {
                        try {
                            Movie m = MovieRepository.addMovie(title, genre, Integer.parseInt(durationStr),
                                    rating, showtime, "", Integer.parseInt(seatPriceStr));
                            AdminMovieListUI.addMovieRow(m);
                        } catch (IOException ex) {
                            DisplayMessage.displayError(AdminMovieListUI.getAdminMovieListUI().getMainFrame(),
                                    ex.getMessage());
                        }
                    } else {
                        Movie old = MovieRepository.getMovies().get(this.currentMovieId);
                        String occupiedSeats = old.getOccupiedSeat()
                                .stream()
                                .collect(Collectors.joining(","));
                        Movie edited = new Movie(this.currentMovieId, title, genre,
                                Integer.parseInt(durationStr), rating, showtime, occupiedSeats,
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