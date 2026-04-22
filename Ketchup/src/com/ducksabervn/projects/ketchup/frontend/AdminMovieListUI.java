package com.ducksabervn.projects.ketchup.frontend;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class AdminMovieListUI {

    //SINGLETON DESIGN PATTERN => 1 UI INSTANCE AT A TIME
    private static AdminMovieListUI adminMovieListUI;

    //the admin main frame
    private JFrame mainFrame;
    private JPanel mainPanel;

    //top bar: title + logout button
    private JPanel topPanel;
    private JLabel titleLabel;
    private JButton logoutButton;

    //search bar
    private JPanel searchPanel;
    private JLabel searchLabel;
    private JTextField searchField;
    private JButton searchButton;

    //sort bar
    private JLabel sortLabel;
    private JComboBox<String> sortComboBox;
    private JButton sortButton;

    //movie table
    private String[] tableColumns;
    private DefaultTableModel tableModel;
    private JTable movieTable;
    private JScrollPane tableScrollPane;

    //action buttons at the bottom
    private JPanel buttonPanel;
    private JButton addMovieButton;
    private JButton editMovieButton;
    private JButton deleteMovieButton;
    private JButton viewShowtimesButton;

    private AdminMovieListUI() {
        this.mainFrame = new JFrame("Ketchup - Admin");
        this.mainPanel = new JPanel();
        this.topPanel = new JPanel(new BorderLayout());
        this.titleLabel = new JLabel("Movie Management", SwingConstants.LEFT);
        this.logoutButton = new JButton("Logout");
        this.searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        this.searchLabel = new JLabel("Search:");
        this.searchField = new JTextField(20);
        this.searchButton = new JButton("Search");
        this.sortLabel = new JLabel("Sort by:");
        this.sortComboBox = new JComboBox<>(new String[]{"Title", "Genre", "Duration", "Rating"});
        this.sortButton = new JButton("Sort");
        this.tableColumns = new String[]{"ID", "Title", "Genre", "Duration (min)", "Rating"};
        this.tableModel = new DefaultTableModel(tableColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        this.movieTable = new JTable(tableModel);
        this.tableScrollPane = new JScrollPane(movieTable);
        this.buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        this.addMovieButton = new JButton("Add Movie");
        this.editMovieButton = new JButton("Edit Movie");
        this.deleteMovieButton = new JButton("Delete Movie");
        this.viewShowtimesButton = new JButton("View Showtimes");
    }

    /**
     * Since we are using singleton design pattern, this UI will only be initialized once
     */
    public static void initialize() {
        if (AdminMovieListUI.adminMovieListUI == null) {
            AdminMovieListUI.adminMovieListUI = new AdminMovieListUI();
        }
        AdminMovieListUI.adminMovieListUI.initializeAllElements();
        AdminMovieListUI.adminMovieListUI.mainFrame.add(AdminMovieListUI.adminMovieListUI.mainPanel);
        AdminMovieListUI.adminMovieListUI.mainFrame.setVisible(true);
    }

    private void initializeAllElements() {
        //initialize the main frame
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setSize(800, 550);
        mainFrame.setLocationRelativeTo(null);
        mainFrame.setResizable(true);

        //initialize the main content panel
        mainPanel.setLayout(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        //initialize the top bar (title + logout)
        titleLabel.setFont(new Font("Arial", Font.BOLD, 22));
        logoutButton.setPreferredSize(new Dimension(90, 30));
        topPanel.add(titleLabel, BorderLayout.WEST);
        topPanel.add(logoutButton, BorderLayout.EAST);
        topPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 5, 0));

        //initialize the search bar and sort bar
        searchPanel.add(searchLabel);
        searchPanel.add(searchField);
        searchPanel.add(searchButton);
        searchPanel.add(Box.createHorizontalStrut(20));
        searchPanel.add(sortLabel);
        searchPanel.add(sortComboBox);
        searchPanel.add(sortButton);

        //group top bar + search into the NORTH section
        JPanel northPanel = new JPanel(new BorderLayout());
        northPanel.add(topPanel, BorderLayout.NORTH);
        northPanel.add(searchPanel, BorderLayout.SOUTH);
        mainPanel.add(northPanel, BorderLayout.NORTH);

        //initialize the movie table
        movieTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        movieTable.setRowHeight(28);
        movieTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 13));
        movieTable.setFont(new Font("Arial", Font.PLAIN, 13));
        mainPanel.add(tableScrollPane, BorderLayout.CENTER);

        //initialize the action buttons
        addMovieButton.setPreferredSize(new Dimension(120, 30));
        editMovieButton.setPreferredSize(new Dimension(120, 30));
        deleteMovieButton.setPreferredSize(new Dimension(120, 30));
        viewShowtimesButton.setPreferredSize(new Dimension(140, 30));
        buttonPanel.add(addMovieButton);
        buttonPanel.add(editMovieButton);
        buttonPanel.add(deleteMovieButton);
        buttonPanel.add(viewShowtimesButton);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        //load movie list into table on startup
        loadMovies();

        ////SUBSECTION - ADDING LISTENER TO THE SEARCH BUTTON
        this.searchButton.addActionListener(e -> {
            String keyword = searchField.getText().trim();
            // TODO: Call MovieService.searchMovies(keyword) and pass results to loadMovies(results)
        });

        ////SUBSECTION - ADDING LISTENER TO THE SORT BUTTON
        this.sortButton.addActionListener(e -> {
            String selectedColumn = (String) sortComboBox.getSelectedItem();
            // TODO: Call MovieService.getSortedMovies(selectedColumn) and pass results to loadMovies(results)
        });

        ////SUBSECTION - ADDING LISTENER TO THE ADD MOVIE BUTTON
        this.addMovieButton.addActionListener(e -> {
            AdminMovieFormUI.initialize("ADD", null);
        });

        ////SUBSECTION - ADDING LISTENER TO THE EDIT MOVIE BUTTON
        this.editMovieButton.addActionListener(e -> {
            int selectedRow = movieTable.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(mainFrame, "Please select a movie to edit.", "No Selection", JOptionPane.WARNING_MESSAGE);
                return;
            }
            // TODO: Get movie ID from tableModel.getValueAt(selectedRow, 0)
            AdminMovieFormUI.initialize("EDIT", (String)
                    this.tableModel.getValueAt(this.movieTable.getSelectedRow(), 0));
        });

        ////SUBSECTION - ADDING LISTENER TO THE DELETE MOVIE BUTTON
        this.deleteMovieButton.addActionListener(e -> {
            int selectedRow = movieTable.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(mainFrame, "Please select a movie to delete.", "No Selection", JOptionPane.WARNING_MESSAGE);
                return;
            }
            int confirm = JOptionPane.showConfirmDialog(mainFrame, "Are you sure you want to delete this movie?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                // TODO: Get movie ID from tableModel.getValueAt(selectedRow, 0)
                // TODO: Call MovieService.deleteMovie(movieId)
                // TODO: If success -> tableModel.removeRow(selectedRow)
            }
        });

        ////SUBSECTION - ADDING LISTENER TO THE VIEW SHOWTIMES BUTTON
        this.viewShowtimesButton.addActionListener(e -> {
            int selectedRow = movieTable.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(mainFrame, "Please select a movie to view showtimes.", "No Selection", JOptionPane.WARNING_MESSAGE);
                return;
            }
            // TODO: Get movie ID from tableModel.getValueAt(selectedRow, 0)
            // TODO: Open AdminShowtimeListUI with the selected movie ID
        });

        ////SUBSECTION - ADDING LISTENER TO THE LOGOUT BUTTON
        this.logoutButton.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(mainFrame, "Are you sure you want to logout?", "Confirm Logout", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                mainFrame.dispose();
                adminMovieListUI = null;
                LoginUI.initalize();
            }
        });
    }

    //clear table and reload all movies from service
    private void loadMovies() {
        tableModel.setRowCount(0);
        // TODO: Replace sample rows below with real data from MovieService.getAllMovies()
        // TODO: Loop through the movie list and call addMovieRow() for each movie
        addMovieRow("M001", "Inception", "Sci-Fi", 148, "PG-13");
        addMovieRow("M002", "The Dark Knight", "Action", 152, "PG-13");
        addMovieRow("M003", "Interstellar", "Sci-Fi", 169, "PG");
    }

    //add a single movie row into the table
    private void addMovieRow(String id, String title, String genre, int duration, String rating) {
        tableModel.addRow(new Object[]{id, title, genre, duration, rating});
    }
}