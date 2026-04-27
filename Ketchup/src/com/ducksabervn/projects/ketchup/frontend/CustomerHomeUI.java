package com.ducksabervn.projects.ketchup.frontend;

import com.ducksabervn.projects.ketchup.backend.admin.Movie;
import com.ducksabervn.projects.ketchup.backend.admin.MovieRepository;
import com.ducksabervn.projects.ketchup.backend.helper.DisplayMessage;
import com.ducksabervn.projects.ketchup.backend.helper.ReadCSVFile;
import com.ducksabervn.projects.ketchup.backend.user.Booking;
import com.ducksabervn.projects.ketchup.backend.user.BookingRepository;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.stream.Collectors;

public class CustomerHomeUI {

    //SINGLETON DESIGN PATTERN => 1 UI INSTANCE AT A TIME
    private static CustomerHomeUI customerHomeUI;

    //Data loaded for UI only
    private ArrayList<Movie> currentMovieData;
    private ArrayList<Booking> currentBookingData;
    private String currentEmail;

    //the main frame
    private JFrame mainFrame;
    private JPanel mainPanel;

    //top bar: welcome label + logout button
    private JPanel topPanel;
    private JLabel welcomeLabel;
    private JButton logoutButton;

    //main tab container
    private JTabbedPane tabbedPane;

    ////TAB 1 - MOVIE LIST
    private JPanel movieListTab;

    //search bar
    private JPanel searchPanel;
    private JLabel searchLabel;
    private JTextField searchField;
    private JButton searchButton;
    private JLabel searchNoteLabel;
    private JLabel searchNoteLabel2;

    //sort bar
    private JLabel sortLabel;
    private JComboBox<String> sortComboBox;
    private JButton sortButton;

    //movie table
    private String[] movieTableColumns;
    private DefaultTableModel movieTableModel;
    private JTable movieTable;
    private JScrollPane movieTableScrollPane;

    //movie action buttons
    private JPanel movieButtonPanel;
    private JButton viewDetailsButton;
    private JButton bookNowButton;
    private JButton movieRefreshButton;

    ////TAB 2 - BOOKING HISTORY
    private JPanel bookingHistoryTab;

    //booking search bar
    private JPanel bookingSearchPanel;
    private JLabel bookingSearchLabel;
    private JTextField bookingSearchField;
    private JButton bookingSearchButton;
    private JLabel bookingSearchNoteLabel;
    private JLabel bookingSearchNoteLabel2;

    //booking sort bar
    private JLabel bookingSortLabel;
    private JComboBox<String> bookingSortComboBox;
    private JButton bookingSortButton;

    //booking history table
    private String[] bookingTableColumns;
    private DefaultTableModel bookingTableModel;
    private JTable bookingTable;
    private JScrollPane bookingTableScrollPane;

    //booking action buttons
    private JPanel bookingButtonPanel;
    private JButton bookingRefreshButton;

    //SORTING CRITERIONS
    private static final Comparator<Movie> SORT_BY_TITLE = Comparator.comparing(Movie::getTitle);
    private static final Comparator<Movie> SORT_BY_GENRE = Comparator.comparing(Movie::getGenre);
    private static final Comparator<Movie> SORT_BY_DURATION = Comparator.comparing(Movie::getDuration);
    private static final Comparator<Movie> SORT_BY_SHOWTIME = Comparator.comparing(Movie::getShowTime);

    private CustomerHomeUI() {
        this.mainFrame = new JFrame("Ketchup");
        this.mainPanel = new JPanel();
        this.topPanel = new JPanel(new BorderLayout());
        this.welcomeLabel = new JLabel("", SwingConstants.LEFT);
        this.logoutButton = new JButton("Logout");
        this.tabbedPane = new JTabbedPane();

        //tab 1 - movie list
        this.movieListTab = new JPanel(new BorderLayout(10, 10));
        this.searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        this.searchLabel = new JLabel("Search:");
        this.searchField = new JTextField(20);
        this.searchButton = new JButton("Search");
        this.searchNoteLabel = new JLabel("* If searching for showtime, please use the format \"yyyy-MM-dd HH:mm\"");
        this.searchNoteLabel2 = new JLabel("* Searching for occupied seats is not supported");
        this.sortLabel = new JLabel("Sort by:");
        this.sortComboBox = new JComboBox<>(new String[]{"Title", "Genre", "Duration (min)", "Show time"});
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

        //tab 2 - booking history
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
     * Since we are using singleton design pattern, this UI will only be initialized once
     * username is displayed in the welcome label at the top
     */
    public static void initialize(String username, String email) {
        CustomerHomeUI.customerHomeUI = new CustomerHomeUI();
        CustomerHomeUI.customerHomeUI.initializeAllElements(username);
        CustomerHomeUI.customerHomeUI.currentEmail = email;
        CustomerHomeUI.customerHomeUI.mainFrame.add(CustomerHomeUI.customerHomeUI.mainPanel);
        CustomerHomeUI.customerHomeUI.mainFrame.setVisible(true);
    }

    private void initializeAllElements(String username) {
        //initialize the main frame
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setSize(850, 580);
        mainFrame.setLocationRelativeTo(null);
        mainFrame.setResizable(true);

        //initialize the main content panel
        mainPanel.setLayout(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 10, 15));

        //initialize the top bar (welcome label + logout)
        welcomeLabel.setText("Welcome, " + username + "!");
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 18));
        logoutButton.setPreferredSize(new Dimension(90, 30));
        topPanel.add(welcomeLabel, BorderLayout.WEST);
        topPanel.add(logoutButton, BorderLayout.EAST);
        topPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 5, 0));
        mainPanel.add(topPanel, BorderLayout.NORTH);

        //initialize tab 1 and tab 2 content
        initializeMovieListTab();
        initializeBookingHistoryTab();

        //add both tabs into the tabbed pane
        tabbedPane.addTab("Movies", movieListTab);
        tabbedPane.addTab("My Bookings", bookingHistoryTab);
        mainPanel.add(tabbedPane, BorderLayout.CENTER);

        ////SUBSECTION - ADDING LISTENER TO THE LOGOUT BUTTON
        this.logoutButton.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(mainFrame, "Are you sure you want to logout?", "Confirm Logout", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                mainFrame.dispose();
                new SwingWorker<Void, Void>() {
                    @Override
                    protected Void doInBackground() {
                        ReadCSVFile.updateDataBackground();
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
        this.mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.mainFrame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                new SwingWorker<Void, Void>() {
                    @Override
                    protected Void doInBackground() {
                        ReadCSVFile.updateDataBackground();
                        return null;
                    }
                }.execute();
            }
        });

        //load data for both tabs on startup
        loadMovies();
        loadBookingHistory();
    }

    //initialize all components inside the Movie List tab
    private void initializeMovieListTab() {
        movieListTab.setBorder(BorderFactory.createEmptyBorder(10, 5, 5, 5));

        //search bar + sort bar
        searchPanel.add(searchLabel);
        searchPanel.add(searchField);
        searchPanel.add(searchButton);
        searchPanel.add(Box.createHorizontalStrut(20));
        searchPanel.add(sortLabel);
        searchPanel.add(sortComboBox);
        searchPanel.add(sortButton);

        //initialize the search note labels
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

        //movie table
        movieTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        movieTable.setRowHeight(28);
        movieTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 13));
        movieTable.setFont(new Font("Arial", Font.PLAIN, 13));
        movieListTab.add(movieTableScrollPane, BorderLayout.CENTER);

        //movie action buttons (left: View Details + Book Now, right: Refresh)
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
                    return MovieRepository.searchMovie(keyword);
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
                case "Title":
                    CustomerHomeUI.customerHomeUI.sortMovieByCriterion(0);
                    break;
                case "Genre":
                    CustomerHomeUI.customerHomeUI.sortMovieByCriterion(1);
                    break;
                case "Duration (min)":
                    CustomerHomeUI.customerHomeUI.sortMovieByCriterion(2);
                    break;
                case "Showtime":
                    CustomerHomeUI.customerHomeUI.sortMovieByCriterion(3);
                    break;
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
                JOptionPane.showMessageDialog(mainFrame, "Please select a movie to book.", "No Selection", JOptionPane.WARNING_MESSAGE);
                return;
            }
            String currentMovieId = this.currentMovieData.get(this.movieTable.getSelectedRow()).getMovieId();
            CustomerSeatSelectionUI.initialize(currentMovieId, this.currentEmail);
        });
    }

    //initialize all components inside the Booking History tab
    private void initializeBookingHistoryTab() {
        bookingHistoryTab.setBorder(BorderFactory.createEmptyBorder(10, 5, 5, 5));

        //search bar + sort bar
        bookingSearchPanel.add(bookingSearchLabel);
        bookingSearchPanel.add(bookingSearchField);
        bookingSearchPanel.add(bookingSearchButton);
        bookingSearchPanel.add(Box.createHorizontalStrut(20));
        bookingSearchPanel.add(bookingSortLabel);
        bookingSearchPanel.add(bookingSortComboBox);
        bookingSearchPanel.add(bookingSortButton);

        //initialize the search note label
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

        //booking history table
        bookingTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        bookingTable.setRowHeight(28);
        bookingTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 13));
        bookingTable.setFont(new Font("Arial", Font.PLAIN, 13));
        bookingHistoryTab.add(bookingTableScrollPane, BorderLayout.CENTER);

        //booking action buttons (right: Refresh)
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
            // TODO: implement booking search logic
        });

        ////SUBSECTION - ADDING LISTENER TO THE SORT BUTTON
        this.bookingSortButton.addActionListener(e -> {
            // TODO: implement booking sort logic
        });
    }

    //clear movie table and reload all movies from service
    private void loadMovies() {
        new SwingWorker<ArrayList<Movie>, Void>() {
            @Override
            protected ArrayList<Movie> doInBackground() {
                ArrayList<Movie> nonBookedMovies = new ArrayList<>(MovieRepository.getMovies().values());
                ArrayList<Booking> bookings = new ArrayList<>(BookingRepository.getBookings().values());
                for (Booking b : bookings) {
                    nonBookedMovies.removeIf((Movie m) -> m.getMovieId().equals(b.getMovieId()));
                }
                return nonBookedMovies;
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

    //clear booking table and reload all bookings for the current user from service
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

    //add a single movie row into the table
    private void addMovie(Movie m) {
        CustomerHomeUI.customerHomeUI.movieTableModel.addRow(new Object[]{
                m.getTitle(),
                m.getGenre(),
                m.getDuration(),
                m.getShowTime().format(Movie.getDatetimeFormat())
        });
    }

    //add a single booking row into the table
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

    private void updateMovieRows(ArrayList<Movie> arr) {
        movieTableModel.setRowCount(0);
        for (Movie m : arr) {
            addMovie(m);
        }
    }

    private void sortMovieByCriterion(int criterion) {
        ArrayList<Movie> arr = CustomerHomeUI.customerHomeUI.currentMovieData;
        switch (criterion) {
            case 0:
                arr.sort(SORT_BY_TITLE);
                break;
            case 1:
                arr.sort(SORT_BY_GENRE);
                break;
            case 2:
                arr.sort(SORT_BY_DURATION);
                break;
            case 3:
                arr.sort(SORT_BY_SHOWTIME);
                break;
        }
        this.updateMovieRows(arr);
    }

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

    public static void addBookingRow(Booking b) {
        CustomerHomeUI.customerHomeUI.currentBookingData.add(b);
        CustomerHomeUI.customerHomeUI.addBooking(b);
    }
}