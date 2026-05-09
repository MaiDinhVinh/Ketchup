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
 * File Name:       CustomerHomeUI.java
 * Developer:       Tran Phan Anh*, Nguyen The Khoi Nguyen*, Nguyen Dinh Quy*,
 *                  Mai Dinh Vinh* (* equal contributions)
 * Description:     Main screen for logged-in customers, presenting a tabbed
 *                  interface with a searchable and sortable movie listing tab
 *                  and a personal booking history tab, with all session data
 *                  persisted to disk on logout or window close.
 ******************************************************************************/

package com.ducksabervn.projects.ketchup.frontend;

import com.ducksabervn.projects.ketchup.backend.io.BookingCsvIO;
import com.ducksabervn.projects.ketchup.backend.io.CredentialCsvIO;
import com.ducksabervn.projects.ketchup.backend.io.MovieCsvIO;
import com.ducksabervn.projects.ketchup.backend.model.Movie;
import com.ducksabervn.projects.ketchup.backend.repositories.MovieRepository;
import com.ducksabervn.projects.ketchup.backend.model.Booking;
import com.ducksabervn.projects.ketchup.backend.repositories.BookingRepository;
import com.ducksabervn.projects.ketchup.frontend.util.DisplayMessage;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.stream.Collectors;

/**
 * The main screen presented to a customer after a successful login.
 * Organizes content into two tabs via a {@link JTabbedPane}:
 *   Movies — displays all available (unbooked) screenings in a
 *       searchable, sortable table with "View Details" and "Book Now" actions.
 *   My Bookings — displays the current user's booking history in a
 *       searchable, sortable table.
 * On logout or window close, all session data is persisted to the respective
 * CSV files via {@link MovieCsvIO}, {@link CredentialCsvIO}, and
 * {@link BookingCsvIO} before returning to {@link LoginUI}.
 */
public class CustomerHomeUI {

    /**
     * The sole instance of {@code CustomerHomeUI}, replaced on each
     * call to {@link #initialize(String, String)}.
     */
    private static CustomerHomeUI customerHomeUI;

    /**
     * The list of {@link Movie} objects currently displayed in the movie table.
     * Contains only movies that the current user has not yet booked.
     */
    private ArrayList<Movie> currentMovieData;

    /**
     * The list of {@link Booking} objects currently displayed in the
     * booking history table.
     */
    private ArrayList<Booking> currentBookingData;

    /**
     * The email address of the currently logged-in customer, used when
     * creating new bookings via {@link BookingRepository#addBooking}.
     */
    private String currentEmail;

    /** The main application window for the customer home screen. */
    private JFrame mainFrame;

    /** The root content panel using {@link BorderLayout}. */
    private JPanel mainPanel;

    /** Panel containing the welcome label and logout button at the top. */
    private JPanel topPanel;

    /** Label displaying a personalized welcome message with the customer's username. */
    private JLabel welcomeLabel;

    /** Button that triggers the logout flow, saving all session data before returning to login. */
    private JButton logoutButton;

    /**
     * The tabbed pane that hosts the Movies tab and the My Bookings tab
     * as the main content area of the screen.
     */
    private JTabbedPane tabbedPane;

    /** Root panel for the Movies tab, using {@link BorderLayout}. */
    private JPanel movieListTab;

    /** Panel containing the movie search bar and sort bar controls. */
    private JPanel searchPanel;

    /** Label identifying the movie search input field. */
    private JLabel searchLabel;

    /** Text field for entering a movie search keyword. */
    private JTextField searchField;

    /** Button that triggers a movie search against {@link MovieRepository}. */
    private JButton searchButton;

    /**
     * Hint label reminding the customer to use {@code yyyy-MM-dd HH:mm}
     * format when searching movies by showtime.
     */
    private JLabel searchNoteLabel;

    /** Hint label informing the customer that searching by occupied seats is unsupported. */
    private JLabel searchNoteLabel2;

    /** Label identifying the movie sort criterion combo box. */
    private JLabel sortLabel;

    /**
     * Combo box for selecting the column by which the movie table is sorted.
     * Options: Title, Genre, Duration (min), Showtime.
     */
    private JComboBox<String> sortComboBox;

    /** Button that applies the selected sort criterion to the current movie table data. */
    private JButton sortButton;

    /** Column header names for the movie table. */
    private String[] movieTableColumns;

    /**
     * The table model backing {@link #movieTable}. Cells are non-editable
     * to prevent direct in-table editing.
     */
    private DefaultTableModel movieTableModel;

    /** The table component displaying all available movie screenings. */
    private JTable movieTable;

    /** Scroll pane wrapping {@link #movieTable} to support vertical scrolling. */
    private JScrollPane movieTableScrollPane;

    /** Panel containing the View Details, Book Now, and Refresh action buttons. */
    private JPanel movieButtonPanel;

    /** Button that opens {@link CustomerMovieDetailUI} for the selected movie. */
    private JButton viewDetailsButton;

    /** Button that opens {@link CustomerSeatSelectionUI} for the selected movie. */
    private JButton bookNowButton;

    /** Button that reloads the unbooked movie list from {@link MovieRepository}. */
    private JButton movieRefreshButton;

    /** Root panel for the My Bookings tab, using {@link BorderLayout}. */
    private JPanel bookingHistoryTab;

    /** Panel containing the booking search bar and sort bar controls. */
    private JPanel bookingSearchPanel;

    /** Label identifying the booking search input field. */
    private JLabel bookingSearchLabel;

    /** Text field for entering a booking search keyword. */
    private JTextField bookingSearchField;

    /** Button that triggers a booking search against {@link BookingRepository}. */
    private JButton bookingSearchButton;

    /**
     * Hint label reminding the customer to use {@code yyyy-MM-dd HH:mm}
     * format when searching bookings by showtime.
     */
    private JLabel bookingSearchNoteLabel;

    /** Hint label informing the customer that searching by movie details is unsupported here. */
    private JLabel bookingSearchNoteLabel2;

    /** Label identifying the booking sort criterion combo box. */
    private JLabel bookingSortLabel;

    /**
     * Combo box for selecting the column by which the booking table is sorted.
     * Options: Booking ID, Movie, Showtime, Total Price.
     */
    private JComboBox<String> bookingSortComboBox;

    /** Button that applies the selected sort criterion to the current booking table data. */
    private JButton bookingSortButton;

    /** Column header names for the booking history table. */
    private String[] bookingTableColumns;

    /**
     * The table model backing {@link #bookingTable}. Cells are non-editable
     * to prevent direct in-table editing.
     */
    private DefaultTableModel bookingTableModel;

    /** The table component displaying the current user's booking history. */
    private JTable bookingTable;

    /** Scroll pane wrapping {@link #bookingTable} to support vertical scrolling. */
    private JScrollPane bookingTableScrollPane;

    /** Panel containing the booking Refresh button. */
    private JPanel bookingButtonPanel;

    /** Button that reloads the booking history from {@link BookingRepository}. */
    private JButton bookingRefreshButton;

    /** Comparator that sorts movies alphabetically by title. */
    private static final Comparator<Movie> SORT_BY_TITLE =
            Comparator.comparing(Movie::getTitle);

    /** Comparator that sorts movies alphabetically by genre. */
    private static final Comparator<Movie> SORT_BY_GENRE =
            Comparator.comparing(Movie::getGenre);

    /** Comparator that sorts movies by runtime in ascending order. */
    private static final Comparator<Movie> SORT_BY_DURATION =
            Comparator.comparing(Movie::getDuration);

    /** Comparator that sorts movies by showtime in chronological order. */
    private static final Comparator<Movie> SORT_BY_SHOWTIME =
            Comparator.comparing(Movie::getShowTime);

    /** Comparator that sorts bookings alphabetically by booking ID. */
    private static final Comparator<Booking> SORT_BY_ID =
            Comparator.comparing(Booking::getBookingId);

    /**
     * Comparator that sorts bookings alphabetically by the title of the
     * associated movie, resolved from {@link MovieRepository}.
     */
    private static final Comparator<Booking> SORT_BY_MOVIE =
            Comparator.comparing(
                    (Booking b) -> MovieRepository.getMovies().get(b.getMovieId()).getTitle());

    /** Comparator that sorts bookings by showtime in chronological order. */
    private static final Comparator<Booking> SORT_BY_BOOKING_SHOWTIME =
            Comparator.comparing(Booking::getShowtime);

    /** Comparator that sorts bookings by total price in ascending order. */
    private static final Comparator<Booking> SORT_BY_TOTAL_PRICE =
            Comparator.comparing(Booking::getTotalPrice);

    /**
     * Private constructor that initializes all Swing components with default
     * values. Customization and layout are handled by
     * {@link #initializeAllElements(String)}.
     */
    private CustomerHomeUI() {
        this.mainFrame = new JFrame("Ketchup");
        this.mainPanel = new JPanel();
        this.topPanel = new JPanel(new BorderLayout());
        this.welcomeLabel = new JLabel("", SwingConstants.LEFT);
        this.logoutButton = new JButton("Logout");
        this.tabbedPane = new JTabbedPane();

        this.movieListTab = new JPanel(new BorderLayout(10, 10));
        this.searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        this.searchLabel = new JLabel("Search:");
        this.searchField = new JTextField(20);
        this.searchButton = new JButton("Search");
        this.searchNoteLabel = new JLabel("* If searching for showtime, please use the format \"yyyy-MM-dd HH:mm\"");
        this.searchNoteLabel2 = new JLabel("* Searching for occupied seats is not supported");
        this.sortLabel = new JLabel("Sort by:");
        this.sortComboBox = new JComboBox<>(new String[]{"Title", "Genre", "Duration (min)", "Showtime"});
        this.sortButton = new JButton("Sort");
        this.movieTableColumns = new String[]{"Title", "Genre", "Duration (min)", "Show time"};
        this.movieTableModel = new DefaultTableModel(movieTableColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        this.movieTable = new JTable(movieTableModel);
        this.movieTableScrollPane = new JScrollPane(movieTable);
        this.movieButtonPanel = new JPanel(new BorderLayout());
        this.viewDetailsButton = new JButton("View Details");
        this.bookNowButton = new JButton("Book Now");
        this.movieRefreshButton = new JButton("⟳");

        this.bookingHistoryTab = new JPanel(new BorderLayout(10, 10));
        this.bookingSearchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        this.bookingSearchLabel = new JLabel("Search:");
        this.bookingSearchField = new JTextField(20);
        this.bookingSearchButton = new JButton("Search");
        this.bookingSearchNoteLabel = new JLabel("* If searching for showtime, please use the format \"yyyy-MM-dd HH:mm\"");
        this.bookingSearchNoteLabel2 = new JLabel("* Searching for occupied seats or other Movie information is not supported here");
        this.bookingSortLabel = new JLabel("Sort by:");
        this.bookingSortComboBox = new JComboBox<>(new String[]{"Booking ID", "Movie", "Showtime", "Total Price"});
        this.bookingSortButton = new JButton("Sort");
        this.bookingTableColumns = new String[]{"Booking ID", "Movie", "Showtime", "Seats", "Total Price"};
        this.bookingTableModel = new DefaultTableModel(bookingTableColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        this.bookingTable = new JTable(bookingTableModel);
        this.bookingTableScrollPane = new JScrollPane(bookingTable);
        this.bookingButtonPanel = new JPanel(new BorderLayout());
        this.bookingRefreshButton = new JButton("⟳");
    }

    /**
     * Creates a new {@code CustomerHomeUI} instance, configures it with the
     * given username and email, and makes the main window visible. Both the
     * movie list and booking history are loaded immediately on startup.
     *
     * @param username the display name of the logged-in customer, shown in
     *                 the welcome label
     * @param email    the email address of the logged-in customer, used for
     *                 booking association
     */
    public static void initialize(String username, String email) {
        CustomerHomeUI.customerHomeUI = new CustomerHomeUI();
        CustomerHomeUI.customerHomeUI.initializeAllElements(username);
        CustomerHomeUI.customerHomeUI.currentEmail = email;
        CustomerHomeUI.customerHomeUI.mainFrame.add(CustomerHomeUI.customerHomeUI.mainPanel);
        CustomerHomeUI.customerHomeUI.mainFrame.setVisible(true);
    }

    /**
     * Configures and lays out all UI components within the main frame,
     * sets up both tabs, attaches all action listeners, and triggers
     * the initial data load for both the movie list and booking history.
     *
     * @param username the display name of the logged-in customer
     */
    private void initializeAllElements(String username) {
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setSize(850, 580);
        mainFrame.setLocationRelativeTo(null);
        mainFrame.setResizable(true);

        mainPanel.setLayout(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 10, 15));

        welcomeLabel.setText("Welcome, " + username + "!");
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 18));
        logoutButton.setPreferredSize(new Dimension(90, 30));
        topPanel.add(welcomeLabel, BorderLayout.WEST);
        topPanel.add(logoutButton, BorderLayout.EAST);
        topPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 5, 0));
        mainPanel.add(topPanel, BorderLayout.NORTH);

        initializeMovieListTab();
        initializeBookingHistoryTab();

        tabbedPane.addTab("Movies", movieListTab);
        tabbedPane.addTab("My Bookings", bookingHistoryTab);
        mainPanel.add(tabbedPane, BorderLayout.CENTER);

        ////SUBSECTION - ADDING LISTENER TO THE LOGOUT BUTTON
        this.logoutButton.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(mainFrame,
                    "Are you sure you want to logout?",
                    "Confirm Logout", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                mainFrame.dispose();
                new SwingWorker<Void, Void>() {
                    @Override
                    protected Void doInBackground() {
                        try {
                            MovieCsvIO.getIO().updateLatestData();
                            CredentialCsvIO.getIO().updateLatestData();
                            BookingCsvIO.getIO().updateLatestData();
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
                            BookingCsvIO.getIO().updateLatestData();
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

        loadMovies();
        loadBookingHistory();
    }

    /**
     * Configures and lays out all components inside the Movies tab, including
     * the search bar, sort bar, movie table, and action buttons. Attaches
     * action listeners for search, sort, refresh, view details, and book now.
     * <p>
     * The movie table displays only screenings that the current user has not
     * already booked, determined by filtering against {@link BookingRepository}.
     */
    private void initializeMovieListTab() {
        movieListTab.setBorder(BorderFactory.createEmptyBorder(10, 5, 5, 5));

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

        JPanel northWrapper = new JPanel(new BorderLayout());
        northWrapper.add(searchPanel, BorderLayout.CENTER);
        northWrapper.add(notePanel, BorderLayout.SOUTH);
        movieListTab.add(northWrapper, BorderLayout.NORTH);

        movieTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        movieTable.setRowHeight(28);
        movieTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 13));
        movieTable.setFont(new Font("Arial", Font.PLAIN, 13));
        movieListTab.add(movieTableScrollPane, BorderLayout.CENTER);

        JPanel movieLeftButtons = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        viewDetailsButton.setPreferredSize(new Dimension(120, 30));
        bookNowButton.setPreferredSize(new Dimension(120, 30));
        movieLeftButtons.add(viewDetailsButton);
        movieLeftButtons.add(bookNowButton);

        JPanel movieRightButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        movieRefreshButton.setPreferredSize(new Dimension(50, 30));
        movieRightButtons.add(movieRefreshButton);

        movieButtonPanel.add(movieLeftButtons, BorderLayout.WEST);
        movieButtonPanel.add(movieRightButtons, BorderLayout.EAST);
        movieListTab.add(movieButtonPanel, BorderLayout.SOUTH);

        ////SUBSECTION - ADDING LISTENER TO THE REFRESH BUTTON
        this.movieRefreshButton.addActionListener(e -> {
            this.loadMovies();
        });

        ////SUBSECTION - ADDING LISTENER TO THE SEARCH BUTTON
        this.searchButton.addActionListener(e -> {
            String keyword = searchField.getText().trim();
            new SwingWorker<ArrayList<Movie>, Void>() {
                @Override
                protected ArrayList<Movie> doInBackground() {
                    HashSet<String> bookedMovieIds = BookingRepository.getBookings().values()
                            .stream()
                            .map(Booking::getMovieId)
                            .collect(Collectors.toCollection(HashSet::new));
                    return MovieRepository.searchMovie(keyword)
                            .stream()
                            .filter(m -> !bookedMovieIds.contains(m.getMovieId()))
                            .collect(Collectors.toCollection(ArrayList::new));
                }

                @Override
                protected void done() {
                    try {
                        movieTableModel.setRowCount(0);
                        currentMovieData = get();
                        updateMovieRows(currentMovieData);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }.execute();
        });

        ////SUBSECTION - ADDING LISTENER TO THE SORT BUTTON
        this.sortButton.addActionListener(e -> {
            String sortingCriteria = ((String) this.sortComboBox.getSelectedItem());
            switch (sortingCriteria) {
                case "Title":         CustomerHomeUI.customerHomeUI.sortMovieByCriterion(0); break;
                case "Genre":         CustomerHomeUI.customerHomeUI.sortMovieByCriterion(1); break;
                case "Duration (min)":CustomerHomeUI.customerHomeUI.sortMovieByCriterion(2); break;
                case "Showtime":      CustomerHomeUI.customerHomeUI.sortMovieByCriterion(3); break;
            }
        });

        ////SUBSECTION - ADDING LISTENER TO THE VIEW DETAILS BUTTON
        this.viewDetailsButton.addActionListener(e -> {
            int selectedRow = movieTable.getSelectedRow();
            if (selectedRow == -1) {
                DisplayMessage.displayWarning(this.mainFrame, "Please select a movie to view details.");
                return;
            }
            String currentMovieId = this.currentMovieData.get(this.movieTable.getSelectedRow()).getMovieId();
            CustomerMovieDetailUI.initialize(currentMovieId);
        });

        ////SUBSECTION - ADDING LISTENER TO THE BOOK NOW BUTTON
        this.bookNowButton.addActionListener(e -> {
            int selectedRow = movieTable.getSelectedRow();
            if (selectedRow == -1) {
                DisplayMessage.displayWarning(mainFrame, "Please select a movie to book.");
                return;
            }
            String currentMovieId = this.currentMovieData.get(this.movieTable.getSelectedRow()).getMovieId();
            CustomerSeatSelectionUI.initialize(currentMovieId, this.currentEmail);
        });
    }

    /**
     * Configures and lays out all components inside the My Bookings tab,
     * including the search bar, sort bar, booking table, and refresh button.
     * Attaches action listeners for search, sort, and refresh.
     */
    private void initializeBookingHistoryTab() {
        bookingHistoryTab.setBorder(BorderFactory.createEmptyBorder(10, 5, 5, 5));

        bookingSearchPanel.add(bookingSearchLabel);
        bookingSearchPanel.add(bookingSearchField);
        bookingSearchPanel.add(bookingSearchButton);
        bookingSearchPanel.add(Box.createHorizontalStrut(20));
        bookingSearchPanel.add(bookingSortLabel);
        bookingSearchPanel.add(bookingSortComboBox);
        bookingSearchPanel.add(bookingSortButton);

        bookingSearchNoteLabel.setForeground(Color.RED);
        bookingSearchNoteLabel.setFont(new Font("Arial", Font.ITALIC, 13));
        bookingSearchNoteLabel2.setForeground(Color.RED);
        bookingSearchNoteLabel2.setFont(new Font("Arial", Font.ITALIC, 13));

        JPanel bookingNotePanel = new JPanel(new GridLayout(2, 1));
        bookingNotePanel.add(bookingSearchNoteLabel);
        bookingNotePanel.add(bookingSearchNoteLabel2);

        JPanel bookingNorthWrapper = new JPanel(new BorderLayout());
        bookingNorthWrapper.add(bookingSearchPanel, BorderLayout.CENTER);
        bookingNorthWrapper.add(bookingNotePanel, BorderLayout.SOUTH);
        bookingHistoryTab.add(bookingNorthWrapper, BorderLayout.NORTH);

        bookingTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        bookingTable.setRowHeight(28);
        bookingTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 13));
        bookingTable.setFont(new Font("Arial", Font.PLAIN, 13));
        bookingHistoryTab.add(bookingTableScrollPane, BorderLayout.CENTER);

        JPanel bookingRightButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        bookingRefreshButton.setPreferredSize(new Dimension(50, 30));
        bookingRightButtons.add(bookingRefreshButton);

        bookingButtonPanel.add(bookingRightButtons, BorderLayout.EAST);
        bookingHistoryTab.add(bookingButtonPanel, BorderLayout.SOUTH);

        ////SUBSECTION - ADDING LISTENER TO THE REFRESH BUTTON
        this.bookingRefreshButton.addActionListener(e -> {
            this.loadBookingHistory();
        });

        ////SUBSECTION - ADDING LISTENER TO THE SEARCH BUTTON
        this.bookingSearchButton.addActionListener(e -> {
            String keyword = bookingSearchField.getText().trim();
            new SwingWorker<ArrayList<Booking>, Void>() {
                @Override
                protected ArrayList<Booking> doInBackground() {
                    return BookingRepository.searchBookings(keyword);
                }

                @Override
                protected void done() {
                    try {
                        movieTableModel.setRowCount(0);
                        currentBookingData = get();
                        updateBookingRows(currentBookingData);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }.execute();
        });

        ////SUBSECTION - ADDING LISTENER TO THE SORT BUTTON
        this.bookingSortButton.addActionListener(e -> {
            String sortingCriteria = ((String) this.bookingSortComboBox.getSelectedItem());
            switch (sortingCriteria) {
                case "Booking ID":  CustomerHomeUI.customerHomeUI.sortBookingByCriterion(0); break;
                case "Movie":       CustomerHomeUI.customerHomeUI.sortBookingByCriterion(1); break;
                case "Showtime":    CustomerHomeUI.customerHomeUI.sortBookingByCriterion(2); break;
                case "Total Price": CustomerHomeUI.customerHomeUI.sortBookingByCriterion(3); break;
            }
        });
    }

    /**
     * Asynchronously loads all unbooked movies from {@link MovieRepository}
     * into {@link #currentMovieData} and repopulates the movie table. Movies
     * already booked by the current user are excluded by cross-referencing
     * against {@link BookingRepository}. Runs on a background thread via
     * {@link SwingWorker} to keep the UI responsive.
     */
    private void loadMovies() {
        new SwingWorker<ArrayList<Movie>, Void>() {
            @Override
            protected ArrayList<Movie> doInBackground() {
                HashSet<String> bookedMovieIds = BookingRepository.getBookings().values()
                        .stream()
                        .map(Booking::getMovieId)
                        .collect(Collectors.toCollection(HashSet::new));
                return MovieRepository.getMovies().values()
                        .stream()
                        .filter(m -> !bookedMovieIds.contains(m.getMovieId()))
                        .collect(Collectors.toCollection(ArrayList::new));
            }

            @Override
            protected void done() {
                try {
                    movieTableModel.setRowCount(0);
                    currentMovieData = get();
                    for (Movie m : currentMovieData) {
                        addMovie(m);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.execute();
    }

    /**
     * Asynchronously loads all bookings for the current user from
     * {@link BookingRepository} into {@link #currentBookingData} and
     * repopulates the booking history table. Runs on a background thread
     * via {@link SwingWorker} to keep the UI responsive.
     */
    private void loadBookingHistory() {
        new SwingWorker<ArrayList<Booking>, Void>() {
            @Override
            protected ArrayList<Booking> doInBackground() {
                return new ArrayList<>(BookingRepository.getBookings().values());
            }

            @Override
            protected void done() {
                try {
                    bookingTableModel.setRowCount(0);
                    currentBookingData = get();
                    for (Booking b : currentBookingData) {
                        addBooking(b);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.execute();
    }

    /**
     * Appends a single {@link Movie} as a new row at the bottom of the movie table.
     *
     * @param m the {@link Movie} whose data is to be added as a table row
     */
    private void addMovie(Movie m) {
        CustomerHomeUI.customerHomeUI.movieTableModel.addRow(new Object[]{
                m.getTitle(),
                m.getGenre(),
                m.getDuration(),
                m.getShowTime().format(Movie.getDatetimeFormat())
        });
    }

    /**
     * Appends a single {@link Booking} as a new row at the bottom of the
     * booking history table. The movie title is resolved from
     * {@link MovieRepository} using the booking's movie ID.
     *
     * @param b the {@link Booking} whose data is to be added as a table row
     */
    private void addBooking(Booking b) {
        String chosenSeats = b.getChosenSeats().stream().collect(Collectors.joining(","));
        CustomerHomeUI.customerHomeUI.bookingTableModel.addRow(new Object[]{
                b.getBookingId(),
                MovieRepository.getMovies().get(b.getMovieId()).getTitle(),
                b.getShowtime().format(Movie.getDatetimeFormat()),
                chosenSeats,
                b.getTotalPrice()
        });
    }

    /**
     * Clears the movie table and repopulates it with the rows from the given list.
     *
     * @param arr the list of {@link Movie} objects to display in the table
     */
    private void updateMovieRows(ArrayList<Movie> arr) {
        movieTableModel.setRowCount(0);
        for (Movie m : arr) {
            addMovie(m);
        }
    }

    /**
     * Clears the booking table and repopulates it with the rows from the given list.
     *
     * @param arr the list of {@link Booking} objects to display in the table
     */
    private void updateBookingRows(ArrayList<Booking> arr) {
        bookingTableModel.setRowCount(0);
        for (Booking b : arr) {
            addBooking(b);
        }
    }

    /**
     * Sorts {@link #currentMovieData} by the given criterion index and
     * refreshes the movie table to reflect the new order.
     *
     * @param criterion the zero-based index of the sort criterion, corresponding
     *                  to: 0 = Title, 1 = Genre, 2 = Duration, 3 = Showtime
     */
    private void sortMovieByCriterion(int criterion) {
        ArrayList<Movie> arr = CustomerHomeUI.customerHomeUI.currentMovieData;
        switch (criterion) {
            case 0: arr.sort(SORT_BY_TITLE);    break;
            case 1: arr.sort(SORT_BY_GENRE);    break;
            case 2: arr.sort(SORT_BY_DURATION); break;
            case 3: arr.sort(SORT_BY_SHOWTIME); break;
        }
        this.updateMovieRows(arr);
    }

    /**
     * Sorts {@link #currentBookingData} by the given criterion index and
     * refreshes the booking table to reflect the new order.
     *
     * @param criterion the zero-based index of the sort criterion, corresponding
     *                  to: 0 = Booking ID, 1 = Movie title, 2 = Showtime,
     *                  3 = Total price
     */
    private void sortBookingByCriterion(int criterion) {
        ArrayList<Booking> arr = CustomerHomeUI.customerHomeUI.currentBookingData;
        switch (criterion) {
            case 0: arr.sort(SORT_BY_ID);              break;
            case 1: arr.sort(SORT_BY_MOVIE);           break;
            case 2: arr.sort(SORT_BY_BOOKING_SHOWTIME);break;
            case 3: arr.sort(SORT_BY_TOTAL_PRICE);     break;
        }
        this.updateBookingRows(arr);
    }

    /**
     * Removes the movie with the given ID from both {@link #currentMovieData}
     * and the movie table. Called by {@link CustomerBookingConfirmUI} after a
     * booking is confirmed, so the movie no longer appears as available.
     *
     * @param movieId the ID of the movie to remove from the table
     */
    public static void removeMovie(String movieId) {
        int found = -1;
        for (int i = 0; i < CustomerHomeUI.customerHomeUI.currentMovieData.size(); i++) {
            if (CustomerHomeUI.customerHomeUI.currentMovieData.get(i).getMovieId().equals(movieId)) {
                found = i;
                break;
            }
        }
        CustomerHomeUI.customerHomeUI.movieTableModel.removeRow(found);
        CustomerHomeUI.customerHomeUI.currentMovieData.remove(found);
    }

    /**
     * Adds the given {@link Booking} to {@link #currentBookingData} and
     * appends a corresponding row to the booking history table. Called by
     * {@link CustomerBookingConfirmUI} immediately after a booking is confirmed.
     *
     * @param b the newly confirmed {@link Booking} to add to the history table
     */
    public static void addBookingRow(Booking b) {
        CustomerHomeUI.customerHomeUI.currentBookingData.add(b);
        CustomerHomeUI.customerHomeUI.addBooking(b);
    }
}