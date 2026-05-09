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
 * File Name:       LoginUI.java
 * Developer:       Tran Phan Anh*, Nguyen The Khoi Nguyen*, Nguyen Dinh Quy*,
 *                  Mai Dinh Vinh* (* equal contributions)
 * Description:     The application entry point screen where users authenticate
 *                  by entering their email, password, and role, with navigation
 *                  to the appropriate home screen on success or to the
 *                  registration screen for new users.
 ******************************************************************************/

package com.ducksabervn.projects.ketchup.frontend;

import com.ducksabervn.projects.ketchup.backend.repositories.CredentialRepository;
import com.ducksabervn.projects.ketchup.backend.io.BookingCsvIO;
import com.ducksabervn.projects.ketchup.backend.model.Credential;
import com.ducksabervn.projects.ketchup.backend.repositories.BookingRepository;
import com.ducksabervn.projects.ketchup.frontend.util.DisplayMessage;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;

/**
 * The login screen of the Ketchup application, presented at startup and
 * after every logout. Accepts an email address, password, and role selection
 * ({@code "User"} or {@code "Admin"}), then authenticates against
 * {@link CredentialRepository}. On success, navigates to either
 * {@link AdminMovieListUI} or {@link CustomerHomeUI} depending on the
 * selected role. Users without an account are directed to {@link RegisterUI}
 * via the Register button.
 */
public class LoginUI {

    /**
     * The sole instance of {@code LoginUI}, replaced on each call to
     * {@link #initialize()}.
     */
    private static LoginUI loginUI;

    /** The main application window for the login screen. */
    private JFrame mainFrame;

    /** The root content panel using {@link BorderLayout}. */
    private JPanel mainPanel;

    /**
     * Label displaying the {@code "Ketchup Login"} heading, centered
     * at the top of the screen.
     */
    private JLabel titleLabel;

    /**
     * Panel containing all credential input rows and the error label,
     * arranged in a 4-row {@link GridLayout}.
     */
    private JPanel formPanel;

    /** Row panel grouping the email label and text field. */
    private JPanel emailRow;

    /** Label identifying the email input field. */
    private JLabel emailLabel;

    /** Text field for entering the user's email address. */
    private JTextField emailField;

    /** Row panel grouping the password label and password field. */
    private JPanel passwordRow;

    /** Label identifying the password input field. */
    private JLabel passwordLabel;

    /**
     * Password field for entering the user's password. Input is masked
     * by default for security.
     */
    private JPasswordField passwordField;

    /** Row panel grouping the role label and role combo box. */
    private JPanel roleRow;

    /** Label identifying the role selection combo box. */
    private JLabel roleLabel;

    /**
     * Combo box allowing the user to select their intended login role.
     * Options: {@code "User"} and {@code "Admin"}. A non-admin account
     * attempting to log in as {@code "Admin"} will be rejected.
     */
    private JComboBox<String> roleComboBox;

    /**
     * Label used to display inline error messages such as
     * {@code "Authentication failed"} or {@code "Required field must not be empty"}.
     * Rendered in red text within the form panel.
     */
    private JLabel errorLabel;

    /**
     * Panel containing the Login and Register buttons, centered at the
     * bottom of the screen.
     */
    private JPanel buttonPanel;

    /**
     * Button that validates the entered credentials against
     * {@link CredentialRepository} and navigates to the appropriate screen
     * on success.
     */
    private JButton loginButton;

    /**
     * Button that closes the login window and opens {@link RegisterUI}
     * for new user account creation.
     */
    private JButton registerButton;

    /**
     * Private constructor that initializes all Swing components with default
     * values. Customization and layout are handled by
     * {@link #initalizeAllElement()}.
     */
    private LoginUI() {
        this.mainFrame = new JFrame("Ketchup");
        this.mainPanel = new JPanel();
        this.titleLabel = new JLabel("Ketchup Login", SwingConstants.CENTER);
        this.formPanel = new JPanel(new GridLayout(4, 1, 8, 8));
        this.emailRow = new JPanel(new BorderLayout(8, 0));
        this.emailLabel = new JLabel("Email:");
        this.emailField = new JTextField();
        this.passwordRow = new JPanel(new BorderLayout(8, 0));
        this.passwordLabel = new JLabel("Password:");
        this.passwordField = new JPasswordField();
        this.roleRow = new JPanel(new BorderLayout(8, 0));
        this.roleLabel = new JLabel("Login as:");
        this.roleComboBox = new JComboBox<>(new String[]{"User", "Admin"});
        this.errorLabel = new JLabel("", SwingConstants.CENTER);
        this.buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        this.loginButton = new JButton("Login");
        this.registerButton = new JButton("Register");
    }

    /**
     * Creates a new {@code LoginUI} instance and makes the login window
     * visible. Called at application startup and after every successful logout.
     */
    public static void initialize() {
        LoginUI.loginUI = new LoginUI();
        LoginUI.loginUI.initalizeAllElement();
        LoginUI.loginUI.mainFrame.add(LoginUI.loginUI.mainPanel);
        LoginUI.loginUI.mainFrame.setVisible(true);
    }

    /**
     * Configures and lays out all UI components within the main frame and
     * attaches action listeners to the Login and Register buttons.
     * <p>
     * On login: validates that the email and password fields are non-empty,
     * then calls {@link CredentialRepository#verifyCredential}. If credentials
     * match, checks that the selected role is consistent with the account's
     * admin flag. On success as {@code "Admin"}, opens {@link AdminMovieListUI}.
     * On success as {@code "User"}, loads the user's bookings from
     * {@link BookingCsvIO} into {@link BookingRepository} and opens
     * {@link CustomerHomeUI}.
     * <p>
     * On register: disposes the login window and opens {@link RegisterUI}.
     */
    private void initalizeAllElement() {
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setSize(400, 320);
        mainFrame.setLocationRelativeTo(null);
        mainFrame.setResizable(false);

        mainPanel.setLayout(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        mainPanel.add(titleLabel, BorderLayout.NORTH);

        emailLabel.setPreferredSize(new Dimension(80, 25));
        emailRow.add(emailLabel, BorderLayout.WEST);
        emailRow.add(emailField, BorderLayout.CENTER);
        passwordLabel.setPreferredSize(new Dimension(80, 25));
        passwordRow.add(passwordLabel, BorderLayout.WEST);
        passwordRow.add(passwordField, BorderLayout.CENTER);
        roleLabel.setPreferredSize(new Dimension(80, 25));
        roleRow.add(roleLabel, BorderLayout.WEST);
        roleRow.add(roleComboBox, BorderLayout.CENTER);
        errorLabel.setForeground(Color.RED);
        errorLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        formPanel.add(emailRow);
        formPanel.add(passwordRow);
        formPanel.add(roleRow);
        formPanel.add(errorLabel);
        mainPanel.add(formPanel, BorderLayout.CENTER);

        loginButton.setPreferredSize(new Dimension(100, 30));
        registerButton.setPreferredSize(new Dimension(100, 30));
        buttonPanel.add(loginButton);
        buttonPanel.add(registerButton);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        ////SUBSECTION - ADDING LISTENER TO THE LOGIN BUTTON
        this.loginButton.addActionListener(e -> {
            if (emailField.getText().isEmpty() || passwordField.getText().isEmpty()) {
                DisplayMessage.displayError(this.mainFrame, "Required field must not be empty");
            } else {
                if (CredentialRepository.verifyCredential(emailField.getText(),
                        new String(passwordField.getPassword()))) {
                    String role = (String) roleComboBox.getSelectedItem();
                    Credential c = CredentialRepository.getUser(emailField.getText());
                    if (!c.isAdmin() && role.equals("Admin")) {
                        DisplayMessage.displayError(this.mainFrame, "Authentication failed");
                    } else {
                        if (role.equals("Admin")) {
                            AdminMovieListUI.initialize(c.getUsername());
                        } else {
                            try {
                                BookingRepository.setBookings(
                                        BookingCsvIO.getIO().readCsvFile(c.getEmail()));
                            } catch (IOException ex) {
                                DisplayMessage.displayError(
                                        AdminMovieListUI.getAdminMovieListUI().getMainFrame(),
                                        ex.getMessage());
                            }
                            CustomerHomeUI.initialize(c.getUsername(), c.getEmail());
                        }
                        this.mainFrame.dispose();
                    }
                } else {
                    DisplayMessage.displayError(this.mainFrame, "Authentication failed");
                }
            }
        });

        ////SUBSECTION - ADDING LISTENER TO THE REGISTER BUTTON
        this.registerButton.addActionListener(e -> {
            this.mainFrame.dispose();
            RegisterUI.initialize();
        });
    }
}