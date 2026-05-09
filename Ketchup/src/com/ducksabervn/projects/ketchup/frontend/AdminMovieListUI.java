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
 * File Name:       AdminMovieListUI.java
 * Developer:       Tran Phan Anh*, Nguyen The Khoi Nguyen*, Nguyen Dinh Quy*,
 *                  Mai Dinh Vinh* (* equal contributions)
 * Description:     Main administration screen displaying all movie screening
 *                  records in a sortable, searchable table, with controls for
 *                  adding, editing, and deleting movies, and saving all changes
 *                  to disk on logout or window close.
 ******************************************************************************/

package com.ducksabervn.projects.ketchup.frontend;

import com.ducksabervn.projects.ketchup.backend.io.CredentialCsvIO;
import com.ducksabervn.projects.ketchup.backend.io.MovieCsvIO;
import com.ducksabervn.projects.ketchup.backend.model.Movie;
import com.ducksabervn.projects.ketchup.backend.repositories.MovieRepository;
import com.ducksabervn.projects.ketchup.frontend.util.DisplayMessage;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.stream.Collectors;

/**
 * The main administration screen of the Ketchup application. Displays all
 * movie screening records loaded from {@link MovieRepository} in a tabular
 * format, and provides controls for searching, sorting, adding, editing,
 * and deleting movies. Changes made during the session are persisted to
 * {@code MOVIES.csv} and {@code USER_CREDENTIALS.csv} on logout or when
 * the window is closed.
 */
public class AdminMovieListUI {

    /**
     * The sole instance of {@code AdminMovieListUI}, replaced on each
     * call to {@link #initialize(String)}.
     */
    private static AdminMovieListUI adminMovieListUI;

    /**
     * The list of {@link Movie} objects currently displayed in the table.
     * Updated whenever the table is refreshed, searched, or sorted.
     */
    private ArrayList<Movie> currentData;

    /** The main application window for the admin screen. */
    private JFrame mainFrame;

    /** The root content panel using {@link BorderLayout}. */
    private JPanel mainPanel;

    /** Panel containing the screen title, username label, and logout button. */
    private JPanel topPanel;

    /** Label displaying the "Movie Management" screen heading. */
    private JLabel titleLabel;

    /** Label displaying a welcome message with the logged-in admin's username. */
    private JLabel usernameLabel;

    /** Button that triggers the logout flow, saving data before returning to login. */
    private JButton logoutButton;

    /** Panel containing the search bar and sort bar controls. */
    private JPanel searchPanel;

    /** Label identifying the search input field. */
    private JLabel searchLabel;

    /** Text field for entering a search keyword. */
    private JTextField searchField;

    /** Button that triggers a search against {@link MovieRepository}. */
    private JButton searchButton;

    /**
     * Hint label reminding the admin to use {@code yyyy-MM-dd HH:mm}
     * format when searching by showtime.
     */
    private JLabel searchNoteLabel;

    /** Hint label informing the admin that searching by occupied seats is unsupported. */
    private JLabel searchNoteLabel2;

    /** Label identifying the sort criterion combo box. */
    private JLabel sortLabel;

    /**
     * Combo box for selecting the column by which the movie table is sorted.
     * Options: ID, Title, Genre, Duration, Rating, Showtime,
     * Number of selected seats, Price/Seat.
     */
    private JComboBox<String> sortComboBox;

    /** Button that applies the selected sort criterion to the current table data. */
    private JButton sortButton;

    /** Column header names for the movie table. */
    private String[] tableColumns;

    /**
     * The table model backing {@link #movieTable}. Cells are non-editable
     * to prevent direct in-table editing.
     */
    private DefaultTableModel tableModel;

    /** The table component displaying all movie records. */
    private JTable movieTable;

    /** Scroll pane wrapping {@link #movieTable} to support vertical scrolling. */
    private JScrollPane tableScrollPane;

    /** Panel containing the Add, Edit, Delete, and Refresh action buttons. */
    private JPanel buttonPanel;

    /** Button that opens {@link AdminMovieFormUI} in {@code "ADD"} mode. */
    private JButton addMovieButton;

    /** Button that opens {@link AdminMovieFormUI} in {@code "EDIT"} mode for the selected row. */
    private JButton editMovieButton;

    /** Button that deletes the currently selected movie after a confirmation dialog. */
    private JButton deleteMovieButton;

    /** Button that reloads all movie data from {@link MovieRepository} into the table. */
    private JButton refreshButton;

    /** Comparator that sorts movies alphabetically by their UUID movie ID. */
    private static final Comparator<Movie> SORT_BY_ID =
            Comparator.comparing(Movie::getMovieId);

    /** Comparator that sorts movies alphabetically by title. */
    private static final Comparator<Movie> SORT_BY_TITLE =
            Comparator.comparing(Movie::getTitle);

    /** Comparator that sorts movies alphabetically by genre. */
    private static final Comparator<Movie> SORT_BY_GENRE =
            Comparator.comparing(Movie::getGenre);

    /** Comparator that sorts movies by runtime in ascending order. */
    private static final Comparator<Movie> SORT_BY_DURATION =
            Comparator.comparing(Movie::getDuration);

    /** Comparator that sorts movies alphabetically by age rating. */
    private static final Comparator<Movie> SORT_BY_RATING =
            Comparator.comparing(Movie::getRating);

    /** Comparator that sorts movies by showtime in chronological order. */
    private static final Comparator<Movie> SORT_BY_SHOWTIME =
            Comparator.comparing(Movie::getShowTime);

    /**
     * Comparator that sorts movies by the number of occupied seats
     * in ascending order.
     */
    private static final Comparator<Movie> SORT_BY_NUMBER_OF_OCCUPIED_SEATS =
            Comparator.comparingInt((Movie m) -> m.getOccupiedSeat().size());

    /** Comparator that sorts movies by seat price in ascending order. */
    private static final Comparator<Movie> SORT_BY_SEAT_PRICE =
            Comparator.comparing(Movie::getSeatPrice);

    /**
     * Private constructor that initializes all Swing components with
     * default values. Customization and layout are handled by
     * {@link #initializeAllElements(String)}.
     */
    private AdminMovieListUI() {
        this.mainFrame = new JFrame("Ketchup - Admin");
        this.mainPanel = new JPanel();
        this.topPanel = new JPanel(new BorderLayout());
        this.titleLabel = new JLabel("Movie Management", SwingConstants.LEFT);
        this.usernameLabel = new JLabel("", SwingConstants.RIGHT);
        this.logoutButton = new JButton("Logout");
        this.searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        this.searchLabel = new JLabel("Search:");
        this.searchField = new JTextField(20);
        this.searchButton = new JButton("Search");
        this.searchNoteLabel = new JLabel("* If searching for showtime, please use the format \"yyyy-MM-dd HH:mm\"");
        this.searchNoteLabel2 = new JLabel("* Searching for occupied seats is not supported");
        this.sortLabel = new JLabel("Sort by:");
        this.sortComboBox = new JComboBox<>(
                new String[]{"ID", "Title", "Genre", "Duration (min)", "Rating",
                        "Showtime", "Number of selected seats", "Price/Seat"});
        this.sortButton = new JButton("Sort");
        this.tableColumns = new String[]{"ID", "Title", "Genre", "Duration (min)",
                "Rating", "Showtime", "Selected seats", "Price/Seat"};
        this.tableModel = new DefaultTableModel(tableColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        this.movieTable = new JTable(tableModel);
        this.tableScrollPane = new JScrollPane(movieTable);
        this.buttonPanel = new JPanel(new BorderLayout());
        this.addMovieButton = new JButton("Add Movie");
        this.editMovieButton = new JButton("Edit Movie");
        this.deleteMovieButton = new JButton("Delete Movie");
        this.refreshButton = new JButton("⟳");
    }

    /**
     * Creates a new {@code AdminMovieListUI} instance, configures it with
     * the given admin username, and makes the main window visible.
     *
     * @param username the display name of the logged-in administrator,
     *                 shown in the top-right welcome label
     */
    public static void initialize(String username) {
        AdminMovieListUI.adminMovieListUI = new AdminMovieListUI();
        AdminMovieListUI.adminMovieListUI.initializeAllElements(username);
        AdminMovieListUI.adminMovieListUI.mainFrame.add(AdminMovieListUI.adminMovieListUI.mainPanel);
        AdminMovieListUI.adminMovieListUI.mainFrame.setVisible(true);
    }

    /**
     * Configures and lays out all UI components within the main frame.
     * Populates the movie table on startup and attaches action listeners
     * to all interactive controls including search, sort, add, edit,
     * delete, refresh, logout, and window-close.
     *
     * @param username the display name of the logged-in administrator
     */
    private void initializeAllElements(String username) {
        mainFrame.setSize(800, 550);
        mainFrame.setLocationRelativeTo(null);
        mainFrame.setResizable(true);

        mainPanel.setLayout(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        titleLabel.setFont(new Font("Arial", Font.BOLD, 22));
        usernameLabel.setText("Welcome! " + username);
        usernameLabel.setFont(new Font("Arial", Font.ITALIC, 12));
        logoutButton.setPreferredSize(new Dimension(90, 30));
        JPanel topRightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        topRightPanel.add(usernameLabel);
        topRightPanel.add(logoutButton);
        topPanel.add(titleLabel, BorderLayout.WEST);
        topPanel.add(topRightPanel, BorderLayout.EAST);
        topPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 5, 0));

        searchPanel.add(searchLabel);
        searchPanel.add(searchField);
        searchPanel.add(searchButton);
        searchPanel.add(Box.createHorizontalStrut(20));
        searchPanel.add(sortLabel);
        searchPanel.add(sortComboBox);
        searchPanel.add(sortButton);

        searchNoteLabel.setForeground(Color.RED);
        searchNoteLabel.setFont(new Font("Arial", Font.ITALIC, 13));
        searchNoteLabel2.setForeground(Color.RED);
        searchNoteLabel2.setFont(new Font("Arial", Font.ITALIC, 13));

        JPanel notePanel = new JPanel(new GridLayout(2, 1));
        notePanel.add(searchNoteLabel);
        notePanel.add(searchNoteLabel2);

        JPanel northPanel = new JPanel(new BorderLayout());
        northPanel.add(topPanel, BorderLayout.NORTH);
        northPanel.add(searchPanel, BorderLayout.CENTER);
        northPanel.add(notePanel, BorderLayout.SOUTH);
        mainPanel.add(northPanel, BorderLayout.NORTH);

        movieTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        movieTable.setRowHeight(28);
        movieTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 13));
        movieTable.setFont(new Font("Arial", Font.PLAIN, 13));
        mainPanel.add(tableScrollPane, BorderLayout.CENTER);

        JPanel leftButtons = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        addMovieButton.setPreferredSize(new Dimension(120, 30));
        editMovieButton.setPreferredSize(new Dimension(120, 30));
        deleteMovieButton.setPreferredSize(new Dimension(120, 30));
        leftButtons.add(addMovieButton);
        leftButtons.add(editMovieButton);
        leftButtons.add(deleteMovieButton);

        JPanel rightButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        refreshButton.setPreferredSize(new Dimension(50, 30));
        rightButtons.add(refreshButton);

        buttonPanel.add(leftButtons, BorderLayout.WEST);
        buttonPanel.add(rightButtons, BorderLayout.EAST);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        initalizeData();

        ////SUBSECTION - ADDING LISTENER TO THE REFRESH BUTTON
        this.refreshButton.addActionListener(e -> {
            this.initalizeData();
        });

        ////SUBSECTION - ADDING LISTENER TO THE SEARCH BUTTON
        this.searchButton.addActionListener(e -> {
            String keyword = searchField.getText().trim();
            new SwingWorker<ArrayList<Movie>, Void>() {
                @Override
                protected ArrayList<Movie> doInBackground() {
                    return MovieRepository.searchMovie(keyword);
                }

                @Override
                protected void done() {
                    try {
                        tableModel.setRowCount(0);
                        currentData = get();
                        updateRows(currentData);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }.execute();
        });

        ////SUBSECTION - ADDING LISTENER TO THE SORT BUTTON
        this.sortButton.addActionListener(e -> {
            String selectedColumn = (String) sortComboBox.getSelectedItem();
            switch (selectedColumn) {
                case "ID":                        sortMovieByCriterion(0); break;
                case "Title":                     sortMovieByCriterion(1); break;
                case "Genre":                     sortMovieByCriterion(2); break;
                case "Duration (min)":            sortMovieByCriterion(3); break;
                case "Rating":                    sortMovieByCriterion(4); break;
                case "Showtime":                  sortMovieByCriterion(5); break;
                case "Number of selected seats":  sortMovieByCriterion(6); break;
                case "Price/Seat":                sortMovieByCriterion(7); break;
            }
        });

        ////SUBSECTION - ADDING LISTENER TO THE ADD MOVIE BUTTON
        this.addMovieButton.addActionListener(e -> {
            AdminMovieFormUI.initialize("ADD", null);
        });

        ////SUBSECTION - ADDING LISTENER TO THE EDIT MOVIE BUTTON
        this.editMovieButton.addActionListener(e -> {
            int selectedRow = movieTable.getSelectedRow();
            if (selectedRow == -1) {
                DisplayMessage.displayWarning(mainFrame, "Please select a movie to edit.");
                return;
            }
            String selectedMovieId = (String) this.tableModel.getValueAt(this.movieTable.getSelectedRow(), 0);
            AdminMovieFormUI.initialize("EDIT", selectedMovieId);

            Movie m = MovieRepository.getMovies().get(selectedMovieId);
            this.tableModel.setValueAt(m.getTitle(), this.movieTable.getSelectedRow(), 1);
            this.tableModel.setValueAt(m.getGenre(), this.movieTable.getSelectedRow(), 2);
            this.tableModel.setValueAt(m.getDuration(), this.movieTable.getSelectedRow(), 3);
            this.tableModel.setValueAt(m.getRating(), this.movieTable.getSelectedRow(), 4);
            this.tableModel.setValueAt(m.getShowTime(), this.movieTable.getSelectedRow(), 5);
            String occupiedSeats = m.getOccupiedSeat().stream().collect(Collectors.joining(","));
            this.tableModel.setValueAt(occupiedSeats, this.movieTable.getSelectedRow(), 6);
            this.tableModel.setValueAt(m.getSeatPrice(), this.movieTable.getSelectedRow(), 7);
        });

        ////SUBSECTION - ADDING LISTENER TO THE DELETE MOVIE BUTTON
        this.deleteMovieButton.addActionListener(e -> {
            int selectedRow = movieTable.getSelectedRow();
            if (selectedRow == -1) {
                DisplayMessage.displayWarning(mainFrame, "Please select a movie to delete.");
                return;
            }
            if (DisplayMessage.displayConfirmationDialog(mainFrame,
                    "Are you sure you want to delete this movie?", "Ketchup")) {
                String selectedMovieId = (String) this.tableModel.getValueAt(this.movieTable.getSelectedRow(), 0);
                MovieRepository.deleteMovie(selectedMovieId);
                this.tableModel.removeRow(this.movieTable.getSelectedRow());
            }
        });

        ////SUBSECTION - ADDING LISTENER TO THE LOGOUT BUTTON
        this.logoutButton.addActionListener(e -> {
            if (DisplayMessage.displayConfirmationDialog(this.mainFrame,
                    "Are you sure you want to logout?", "Ketchup")) {
                mainFrame.dispose();
                new SwingWorker<Void, Void>() {
                    @Override
                    protected Void doInBackground() {
                        try {
                            MovieCsvIO.getIO().updateLatestData();
                            CredentialCsvIO.getIO().updateLatestData();
                        } catch (IOException e) {
                            DisplayMessage.displayError(
                                    AdminMovieListUI.getAdminMovieListUI().getMainFrame(), e.getMessage());
                        }
                        return null;
                    }

                    @Override
                    protected void done() {
                        LoginUI.initialize();
                    }
                }.execute();
            }
        });

        ////SUBSECTION - ADDING LISTENER TO THE "X" - EXIT BUTTON
        this.mainFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        this.mainFrame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                new SwingWorker<Void, Void>() {
                    @Override
                    protected Void doInBackground() {
                        try {
                            MovieCsvIO.getIO().updateLatestData();
                            CredentialCsvIO.getIO().updateLatestData();
                        } catch (IOException e) {
                            DisplayMessage.displayError(
                                    AdminMovieListUI.getAdminMovieListUI().getMainFrame(), e.getMessage());
                        }
                        return null;
                    }

                    @Override
                    protected void done() {
                        System.exit(0);
                    }
                }.execute();
            }
        });
    }

    /**
     * Asynchronously loads all movies from {@link MovieRepository} into
     * {@link #currentData} and repopulates the table. Runs the data fetch
     * on a background thread via {@link SwingWorker} to keep the UI responsive.
     */
    private void initalizeData() {
        new SwingWorker<ArrayList<Movie>, Void>() {
            @Override
            protected ArrayList<Movie> doInBackground() {
                return new ArrayList<>(MovieRepository.getMovies().values());
            }

            @Override
            protected void done() {
                try {
                    tableModel.setRowCount(0);
                    currentData = get();
                    for (Movie m : currentData) {
                        addMovie(m);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.execute();
    }

    /**
     * Appends a single {@link Movie} as a new row at the bottom of the table.
     *
     * @param m the {@link Movie} whose data is to be added as a table row
     */
    private void addMovie(Movie m) {
        String occupiedSeats = m.getOccupiedSeat().stream().collect(Collectors.joining(","));
        AdminMovieListUI.adminMovieListUI.tableModel.addRow(new Object[]{
                m.getMovieId(),
                m.getTitle(),
                m.getGenre(),
                m.getDuration(),
                m.getRating(),
                m.getShowTime().format(Movie.getDatetimeFormat()),
                occupiedSeats,
                m.getSeatPrice()});
    }

    /**
     * Sorts {@link #currentData} by the given criterion index and refreshes
     * the table to reflect the new order.
     *
     * @param criterion the zero-based index of the sort criterion, corresponding
     *                  to: 0 = ID, 1 = Title, 2 = Genre, 3 = Duration,
     *                  4 = Rating, 5 = Showtime, 6 = Occupied seats count,
     *                  7 = Seat price
     */
    private void sortMovieByCriterion(int criterion) {
        ArrayList<Movie> arr = AdminMovieListUI.adminMovieListUI.currentData;
        switch (criterion) {
            case 0: arr.sort(SORT_BY_ID);                        break;
            case 1: arr.sort(SORT_BY_TITLE);                     break;
            case 2: arr.sort(SORT_BY_GENRE);                     break;
            case 3: arr.sort(SORT_BY_DURATION);                  break;
            case 4: arr.sort(SORT_BY_RATING);                    break;
            case 5: arr.sort(SORT_BY_SHOWTIME);                  break;
            case 6: arr.sort(SORT_BY_NUMBER_OF_OCCUPIED_SEATS);  break;
            case 7: arr.sort(SORT_BY_SEAT_PRICE);                break;
        }
        this.updateRows(arr);
    }

    /**
     * Clears the table and repopulates it with the rows from the given list.
     *
     * @param arr the list of {@link Movie} objects to display in the table
     */
    private void updateRows(ArrayList<Movie> arr) {
        tableModel.setRowCount(0);
        for (Movie m : arr) {
            addMovie(m);
        }
    }

    /**
     * Adds the given {@link Movie} to {@link #currentData} and appends a
     * corresponding row to the table. Called by {@link AdminMovieFormUI}
     * after a new movie is successfully created in {@code "ADD"} mode.
     *
     * @param m the newly created {@link Movie} to add to the table
     */
    public static void addMovieRow(Movie m) {
        AdminMovieListUI.adminMovieListUI.currentData.add(m);
        AdminMovieListUI.adminMovieListUI.addMovie(m);
    }

    /**
     * Returns the current singleton instance of {@code AdminMovieListUI}.
     * Used by other classes to obtain a parent component reference for
     * dialog centering and error display.
     *
     * @return the active {@code AdminMovieListUI} instance
     */
    public static AdminMovieListUI getAdminMovieListUI() {
        return AdminMovieListUI.adminMovieListUI;
    }

    /**
     * Returns the main {@link JFrame} of the admin screen.
     * Used as a parent component for dialogs spawned from other UI classes.
     *
     * @return the main admin {@link JFrame}
     */
    public JFrame getMainFrame() {
        return this.mainFrame;
    }
}