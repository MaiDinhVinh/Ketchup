package com.ducksabervn.projects.ketchup.frontend;

import com.ducksabervn.projects.ketchup.backend.admin.Movie;
import com.ducksabervn.projects.ketchup.backend.admin.MovieRepository;
import com.ducksabervn.projects.ketchup.backend.helper.DisplayMessage;
import com.ducksabervn.projects.ketchup.backend.helper.ReadCSVFile;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.stream.Collectors;

public class AdminMovieListUI {

    //SINGLETON DESIGN PATTERN => 1 UI INSTANCE AT A TIME
    private static AdminMovieListUI adminMovieListUI;

    //Data loaded for UI only
    private ArrayList<Movie> currentData;

    //the admin main frame
    private JFrame mainFrame;
    private JPanel mainPanel;

    //top bar: title + username label + logout button
    private JPanel topPanel;
    private JLabel titleLabel;
    private JLabel usernameLabel;
    private JButton logoutButton;

    //search bar
    private JPanel searchPanel;
    private JLabel searchLabel;
    private JTextField searchField;
    private JButton searchButton;
    private JLabel searchNoteLabel;

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
    private JButton refreshButton;

    //SORTING CRITERIONS
    private static final Comparator<Movie> SORT_BY_ID = Comparator.comparing(Movie::getMovieId);
    private static final Comparator<Movie> SORT_BY_TITLE = Comparator.comparing(Movie::getTitle);
    private static final Comparator<Movie> SORT_BY_GENRE = Comparator.comparing(Movie::getGenre);
    private static final Comparator<Movie> SORT_BY_DURATION = Comparator.comparing(Movie::getDuration);
    private static final Comparator<Movie> SORT_BY_RATING = Comparator.comparing(Movie::getRating);
    private static final Comparator<Movie> SORT_BY_SHOWTIME = Comparator.comparing(Movie::getShowTime);
    private static final Comparator<Movie> SORT_BY_NUMBER_OF_OCCUPIED_SEATS =
            Comparator.comparingInt((Movie m) -> m.getOccupiedSeat().size());
    private static final Comparator<Movie> SORT_BY_SEAT_PRICE =
            Comparator.comparing(Movie::getSeatPrice);

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
        this.searchNoteLabel = new JLabel("* Only support searching for ID, TITLE, GENRE, DURATION, RATING");
        this.sortLabel = new JLabel("Sort by:");
        this.sortComboBox = new JComboBox<>(
                new String[]{"ID",
                        "Title",
                        "Genre",
                        "Duration (min)",
                        "Rating",
                        "Showtime",
                        "Number of selected seats",
                        "Price/Seat"}
        );
        this.sortButton = new JButton("Sort");
        this.tableColumns = new String[]{"ID",
                "Title",
                "Genre",
                "Duration (min)",
                "Rating",
                "Showtime",
                "Selected seats",
                "Price/Seat"};
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
     * Since we are using singleton design pattern, this UI will only be initialized once
     * username is the name of the logged-in admin to display in the top bar
     */
    public static void initialize(String username) {
        if (AdminMovieListUI.adminMovieListUI == null) {
            AdminMovieListUI.adminMovieListUI = new AdminMovieListUI();
            AdminMovieListUI.adminMovieListUI.initializeAllElements(username);
            AdminMovieListUI.adminMovieListUI.mainFrame.add(AdminMovieListUI.adminMovieListUI.mainPanel);
        }
        AdminMovieListUI.adminMovieListUI.mainFrame.setVisible(true);
    }

    private void initializeAllElements(String username) {
        //initialize the main frame
        mainFrame.setSize(800, 550);
        mainFrame.setLocationRelativeTo(null);
        mainFrame.setResizable(true);

        //initialize the main content panel
        mainPanel.setLayout(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        //initialize the top bar (title + username + logout)
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

        //initialize the search bar and sort bar
        searchPanel.add(searchLabel);
        searchPanel.add(searchField);
        searchPanel.add(searchButton);
        searchPanel.add(Box.createHorizontalStrut(20));
        searchPanel.add(sortLabel);
        searchPanel.add(sortComboBox);
        searchPanel.add(sortButton);

        //initialize the search note label
        searchNoteLabel.setForeground(Color.RED);
        searchNoteLabel.setFont(new Font("Arial", Font.ITALIC, 11));

        //group top bar + search into the NORTH section
        JPanel northPanel = new JPanel(new BorderLayout());
        northPanel.add(topPanel, BorderLayout.NORTH);
        northPanel.add(searchPanel, BorderLayout.CENTER);
        northPanel.add(searchNoteLabel, BorderLayout.SOUTH);
        mainPanel.add(northPanel, BorderLayout.NORTH);

        //initialize the movie table
        movieTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        movieTable.setRowHeight(28);
        movieTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 13));
        movieTable.setFont(new Font("Arial", Font.PLAIN, 13));
        mainPanel.add(tableScrollPane, BorderLayout.CENTER);

        //initialize the action buttons
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

        //load movie list into table on startup
        initalizeData();

        ////SUBSECTION - ADDING LISTENER TO THE REFRESH BUTTON
        this.refreshButton.addActionListener(e -> {
//            this.initalizeData();

            //threading problem => I have to use this shit
            new SwingWorker<Void, Void>(){
                @Override
                protected Void doInBackground() throws Exception {
                    AdminMovieListUI.adminMovieListUI.initalizeData();
                    return null;
                }
            }.execute();
        });

        ////SUBSECTION - ADDING LISTENER TO THE SEARCH BUTTON
        this.searchButton.addActionListener(e -> {
            String keyword = searchField.getText().trim();
            tableModel.setRowCount(0);

            //threading problem => I have to use this shit
            new SwingWorker<Void, Void>(){
                @Override
                protected Void doInBackground() throws Exception {
                    AdminMovieListUI.adminMovieListUI.currentData = MovieRepository.searchMovie(keyword);
                    AdminMovieListUI.adminMovieListUI.updateRows(AdminMovieListUI.adminMovieListUI.currentData);
                    return null;
                }
            }.execute();
        });

        ////SUBSECTION - ADDING LISTENER TO THE SORT BUTTON
        this.sortButton.addActionListener(e -> {
            String selectedColumn = (String) sortComboBox.getSelectedItem();

            //threading problem => I have to use this shit
            new SwingWorker<Void, Void>(){
                @Override
                protected Void doInBackground() throws Exception {
                    switch (selectedColumn) {
                        case "ID":
                            AdminMovieListUI.adminMovieListUI.sortMovieByCriterion(0);
                            break;
                        case "Title":
                            AdminMovieListUI.adminMovieListUI.sortMovieByCriterion(1);
                            break;
                        case "Genre":
                            AdminMovieListUI.adminMovieListUI.sortMovieByCriterion(2);
                            break;
                        case "Duration (min)":
                            AdminMovieListUI.adminMovieListUI.sortMovieByCriterion(3);
                            break;
                        case "Rating":
                            AdminMovieListUI.adminMovieListUI.sortMovieByCriterion(4);
                            break;
                        case "Showtime":
                            AdminMovieListUI.adminMovieListUI.sortMovieByCriterion(5);
                            break;
                        case "Number of selected seats":
                            AdminMovieListUI.adminMovieListUI.sortMovieByCriterion(6);
                            break;
                        case "Price/Seat":
                            AdminMovieListUI.adminMovieListUI.sortMovieByCriterion(7);
                            break;
                    }
                    return null;
                }
            }.execute();
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
            String selectedMovieId = (String)this.tableModel.getValueAt(this.movieTable.getSelectedRow(), 0);
            AdminMovieFormUI.initialize("EDIT", selectedMovieId);

            //after editing the movie data, update the row's data
            Movie m = MovieRepository.getMovies().get(selectedMovieId);
            this.tableModel.setValueAt(m.getTitle(), this.movieTable.getSelectedRow(), 1);
            this.tableModel.setValueAt(m.getGenre(), this.movieTable.getSelectedRow(), 2);
            this.tableModel.setValueAt(m.getDuration(), this.movieTable.getSelectedRow(), 3);
            this.tableModel.setValueAt(m.getRating(), this.movieTable.getSelectedRow(), 4);
            this.tableModel.setValueAt(m.getShowTime(), this.movieTable.getSelectedRow(), 5);
            String occupiedSeats = m.getOccupiedSeat().stream().collect(Collectors.joining(","));
            this.tableModel.setValueAt(occupiedSeats, this.movieTable.getSelectedRow(), 6);
            this.tableModel.setValueAt(m.getSeatPrice(), this.movieTable.getSelectedRow(), 6);
        });

        ////SUBSECTION - ADDING LISTENER TO THE DELETE MOVIE BUTTON
        this.deleteMovieButton.addActionListener(e -> {
            int selectedRow = movieTable.getSelectedRow();
            if (selectedRow == -1) {
                DisplayMessage.displayWarning(mainFrame, "Please select a movie to delete.");
                return;
            }
            if (DisplayMessage.displayConfirmationDialog(mainFrame,
                    "Are you sure you want to delete this movie?",
                    "Ketchup")) {
                String selectedMovieId = (String)this.tableModel.getValueAt(this.movieTable.getSelectedRow(), 0);

                //threading problem => I have to use this shit
                new SwingWorker<Void, Void>(){
                    @Override
                    protected Void doInBackground() throws Exception {
                        MovieRepository.deleteMovie(selectedMovieId);
                        return null;
                    }
                }.execute();

                this.tableModel.removeRow(this.movieTable.getSelectedRow());
            }
        });

        ////SUBSECTION - ADDING LISTENER TO THE LOGOUT BUTTON
        this.logoutButton.addActionListener(e -> {
            if (DisplayMessage.displayConfirmationDialog(this.mainFrame,
                    "Are you sure you want to logout?",
                    "Ketchup")) {
                mainFrame.dispose();
                LoginUI.initialize();
            }
            ReadCSVFile.updateDataBackground();
        });

        ////SUBSECTION - ADDING LISTENER TO THE "X" - EXIT BUTTON
        this.mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.mainFrame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e){
                ReadCSVFile.updateDataBackground();
                mainFrame.dispose();
            }
        });
    }

    //clear table and reload all movies from service
    private void initalizeData() {
        tableModel.setRowCount(0);
        AdminMovieListUI.adminMovieListUI.currentData = new ArrayList<>(MovieRepository.getMovies().values());
        for(Movie m: AdminMovieListUI.adminMovieListUI.currentData){
            this.addMovie(m);
        }
    }

    //add a single movie row into the table
    private void addMovie(Movie m) {
        String occupiedSeats = m.getOccupiedSeat().stream().
                collect(Collectors.joining(","));
        AdminMovieListUI.adminMovieListUI.tableModel.addRow(new Object[]{
                m.getMovieId(),
                m.getTitle(),
                m.getGenre(),
                m.getDuration(),
                m.getRating(),
                m.getShowTime(),
                occupiedSeats,
                m.getSeatPrice()});
    }

    private void sortMovieByCriterion(int criterion){
        ArrayList<Movie> arr = AdminMovieListUI.adminMovieListUI.currentData;
        switch (criterion) {
            case 0:
                arr.sort(SORT_BY_ID);
                break;
            case 1:
                arr.sort(SORT_BY_TITLE);
                break;
            case 2:
                arr.sort(SORT_BY_GENRE);
                break;
            case 3:
                arr.sort(SORT_BY_DURATION);
                break;
            case 4:
                arr.sort(SORT_BY_RATING);
                break;
            case 5:
                arr.sort(SORT_BY_SHOWTIME);
                break;
            case 6:
                arr.sort(SORT_BY_NUMBER_OF_OCCUPIED_SEATS);
                break;
            case 7:
                arr.sort(SORT_BY_SEAT_PRICE);
                break;
        }
        this.updateRows(arr);
    }

    private void updateRows(ArrayList<Movie> arr){
        tableModel.setRowCount(0);
        for(Movie m: arr){
            addMovie(m);
        }
    }

    public static void addMovieRow(Movie m){
        AdminMovieListUI.adminMovieListUI.addMovie(m);
    }

    public static AdminMovieListUI getAdminMovieListUI(){
        return AdminMovieListUI.adminMovieListUI;
    }

    public JFrame getMainFrame(){
        return this.mainFrame;
    }
}