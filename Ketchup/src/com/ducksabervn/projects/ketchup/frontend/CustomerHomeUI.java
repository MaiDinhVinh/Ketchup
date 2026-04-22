package com.ducksabervn.projects.ketchup.frontend;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class CustomerHomeUI {

    //SINGLETON DESIGN PATTERN => 1 UI INSTANCE AT A TIME
    private static CustomerHomeUI customerHomeUI;

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

    //movie table
    private String[] movieTableColumns;
    private DefaultTableModel movieTableModel;
    private JTable movieTable;
    private JScrollPane movieTableScrollPane;

    //movie action buttons
    private JPanel movieButtonPanel;
    private JButton viewDetailsButton;
    private JButton bookNowButton;

    ////TAB 2 - BOOKING HISTORY
    private JPanel bookingHistoryTab;

    //booking history table
    private String[] bookingTableColumns;
    private DefaultTableModel bookingTableModel;
    private JTable bookingTable;
    private JScrollPane bookingTableScrollPane;

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
        this.movieTableColumns = new String[]{"ID", "Title", "Genre", "Duration (min)", "Rating"};
        this.movieTableModel = new DefaultTableModel(movieTableColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        this.movieTable = new JTable(movieTableModel);
        this.movieTableScrollPane = new JScrollPane(movieTable);
        this.movieButtonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        this.viewDetailsButton = new JButton("View Details");
        this.bookNowButton = new JButton("Book Now");

        //tab 2 - booking history
        this.bookingHistoryTab = new JPanel(new BorderLayout(10, 10));
        this.bookingTableColumns = new String[]{"Booking ID", "Movie", "Showtime", "Seats", "Total Price", "Status"};
        this.bookingTableModel = new DefaultTableModel(bookingTableColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        this.bookingTable = new JTable(bookingTableModel);
        this.bookingTableScrollPane = new JScrollPane(bookingTable);
    }

    /**
     * Since we are using singleton design pattern, this UI will only be initialized once
     * username is displayed in the welcome label at the top
     */
    public static void initialize(String username) {
        if (CustomerHomeUI.customerHomeUI == null) {
            CustomerHomeUI.customerHomeUI = new CustomerHomeUI();
        }
        CustomerHomeUI.customerHomeUI.initializeAllElements(username);
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
                customerHomeUI = null;
                LoginUI.initalize();
            }
        });

        //load data for both tabs on startup
        loadMovies();
        loadBookingHistory();
    }

    //initialize all components inside the Movie List tab
    private void initializeMovieListTab() {
        movieListTab.setBorder(BorderFactory.createEmptyBorder(10, 5, 5, 5));

        //search bar
        searchPanel.add(searchLabel);
        searchPanel.add(searchField);
        searchPanel.add(searchButton);
        movieListTab.add(searchPanel, BorderLayout.NORTH);

        //movie table
        movieTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        movieTable.setRowHeight(28);
        movieTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 13));
        movieTable.setFont(new Font("Arial", Font.PLAIN, 13));
        movieListTab.add(movieTableScrollPane, BorderLayout.CENTER);

        //movie action buttons
        viewDetailsButton.setPreferredSize(new Dimension(120, 30));
        bookNowButton.setPreferredSize(new Dimension(120, 30));
        movieButtonPanel.add(viewDetailsButton);
        movieButtonPanel.add(bookNowButton);
        movieListTab.add(movieButtonPanel, BorderLayout.SOUTH);

        ////SUBSECTION - ADDING LISTENER TO THE SEARCH BUTTON
        this.searchButton.addActionListener(e -> {
            String keyword = searchField.getText().trim();
            // TODO: Call MovieService.searchMovies(keyword) and pass results to loadMovies(results)
        });

        ////SUBSECTION - ADDING LISTENER TO THE VIEW DETAILS BUTTON
        this.viewDetailsButton.addActionListener(e -> {
            int selectedRow = movieTable.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(mainFrame, "Please select a movie to view details.", "No Selection", JOptionPane.WARNING_MESSAGE);
                return;
            }
            // TODO: Get movie ID from movieTableModel.getValueAt(selectedRow, 0)
            // TODO: Open CustomerMovieDetailUI.initialize(movieId)
        });

        ////SUBSECTION - ADDING LISTENER TO THE BOOK NOW BUTTON
        this.bookNowButton.addActionListener(e -> {
            int selectedRow = movieTable.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(mainFrame, "Please select a movie to book.", "No Selection", JOptionPane.WARNING_MESSAGE);
                return;
            }
            // TODO: Get movie ID from movieTableModel.getValueAt(selectedRow, 0)
            // TODO: Open CustomerSeatSelectionUI.initialize(movieId)
        });
    }

    //initialize all components inside the Booking History tab
    private void initializeBookingHistoryTab() {
        bookingHistoryTab.setBorder(BorderFactory.createEmptyBorder(10, 5, 5, 5));

        //booking history table
        bookingTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        bookingTable.setRowHeight(28);
        bookingTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 13));
        bookingTable.setFont(new Font("Arial", Font.PLAIN, 13));
        bookingHistoryTab.add(bookingTableScrollPane, BorderLayout.CENTER);
    }

    //clear movie table and reload all movies from service
    private void loadMovies() {
        movieTableModel.setRowCount(0);
        // TODO: Call MovieService.getAllMovies() and loop through results
        // TODO: Call addMovieRow() for each movie in the list
    }

    //add a single movie row into the movie table
    private void addMovieRow(String id, String title, String genre, int duration, String rating) {
        movieTableModel.addRow(new Object[]{id, title, genre, duration, rating});
    }

    //clear booking table and reload all bookings for the current user from service
    private void loadBookingHistory() {
        bookingTableModel.setRowCount(0);
        // TODO: Call BookingService.getBookingsByCustomerId(currentCustomerId) and loop through results
        // TODO: Call addBookingRow() for each booking in the list
    }

    //add a single booking row into the booking history table
    private void addBookingRow(String bookingId, String movieTitle, String showtime, String seats, String totalPrice, String status) {
        bookingTableModel.addRow(new Object[]{bookingId, movieTitle, showtime, seats, totalPrice, status});
    }
}