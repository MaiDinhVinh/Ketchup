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
 * File Name:       RegisterUI.java
 * Developer:       Tran Phan Anh*, Nguyen The Khoi Nguyen*, Nguyen Dinh Quy*,
 *                  Mai Dinh Vinh* (* equal contributions)
 * Description:     Registration screen that allows new users to create an
 *                  account by providing a username, email, password, and role,
 *                  with input validation before the account is committed to
 *                  the credential repository.
 ******************************************************************************/

package com.ducksabervn.projects.ketchup.frontend;

import com.ducksabervn.projects.ketchup.backend.model.Credential;
import com.ducksabervn.projects.ketchup.backend.repositories.CredentialRepository;
import com.ducksabervn.projects.ketchup.frontend.util.DisplayMessage;

import javax.swing.*;
import java.awt.*;

/**
 * The registration screen of the Ketchup application, opened when a new user
 * clicks Register on {@link LoginUI}. Collects a username, email address,
 * password, password confirmation, and role selection, then validates all
 * inputs before creating a new account via {@link CredentialRepository#register}.
 * On successful registration the user is returned to {@link LoginUI}.
 * The Back button navigates directly back to {@link LoginUI} without creating
 * an account.
 */
public class RegisterUI {

    /**
     * The sole instance of {@code RegisterUI}, replaced on each call to
     * {@link #initialize()}.
     */
    private static RegisterUI registerUI;

    /** The main application window for the registration screen. */
    private JFrame mainFrame;

    /** The root content panel using {@link BorderLayout}. */
    private JPanel mainPanel;

    /**
     * Label displaying the {@code "Ketchup - Register"} heading, centered
     * at the top of the screen.
     */
    private JLabel titleLabel;

    /**
     * Panel containing all registration input rows and the message label,
     * arranged in a 7-row {@link GridLayout}.
     */
    private JPanel formPanel;

    /** Row panel grouping the username label and text field. */
    private JPanel usernameRow;

    /** Label identifying the username input field. */
    private JLabel usernameLabel;

    /** Text field for entering the desired display name for the new account. */
    private JTextField usernameField;

    /** Row panel grouping the email label and text field. */
    private JPanel emailRow;

    /** Label identifying the email input field. */
    private JLabel emailLabel;

    /**
     * Text field for entering the email address that will serve as the
     * unique identifier for the new account. Validated against a regex
     * pattern via {@link Credential#isValidEmail(String)}.
     */
    private JTextField emailField;

    /** Row panel grouping the password label and password field. */
    private JPanel passwordRow;

    /** Label identifying the password input field. */
    private JLabel passwordLabel;

    /**
     * Password field for entering the desired account password.
     * Input is masked by default for security.
     */
    private JPasswordField passwordField;

    /** Row panel grouping the confirm password label and password field. */
    private JPanel confirmPasswordRow;

    /** Label identifying the confirm password input field. */
    private JLabel confirmPasswordLabel;

    /**
     * Password field for re-entering the password to confirm it matches
     * {@link #passwordField}. Registration is blocked if the two values differ.
     */
    private JPasswordField confirmPasswordField;

    /** Row panel grouping the role label and role combo box. */
    private JPanel roleRow;

    /** Label identifying the role selection combo box. */
    private JLabel roleLabel;

    /**
     * Combo box allowing the registrant to select their account role.
     * Options: {@code "User"} and {@code "Admin"}.
     */
    private JComboBox<String> roleComboBox;

    /**
     * Label used to display success or error messages inline within the
     * form panel, such as {@code "Account created successful"} or
     * {@code "Failed to create account"}.
     */
    private JLabel messageLabel;

    /**
     * Panel containing the Register and Back to Login buttons, centered
     * at the bottom of the screen.
     */
    private JPanel buttonPanel;

    /**
     * Button that validates all form inputs and, if valid, registers the
     * new account via {@link CredentialRepository#register} before
     * returning to {@link LoginUI}.
     */
    private JButton registerButton;

    /**
     * Button that discards all entered data, closes the registration window,
     * and returns the user to {@link LoginUI}.
     */
    private JButton backButton;

    /**
     * Private constructor that initializes all Swing components with default
     * values. Customization and layout are handled by
     * {@link #initializeAllElements()}.
     */
    private RegisterUI() {
        this.mainFrame = new JFrame("Ketchup");
        this.mainPanel = new JPanel();
        this.titleLabel = new JLabel("Ketchup - Register", SwingConstants.CENTER);
        this.formPanel = new JPanel(new GridLayout(7, 1, 8, 8));
        this.usernameRow = new JPanel(new BorderLayout(8, 0));
        this.usernameLabel = new JLabel("Username:");
        this.usernameField = new JTextField();
        this.emailRow = new JPanel(new BorderLayout(8, 0));
        this.emailLabel = new JLabel("Email:");
        this.emailField = new JTextField();
        this.passwordRow = new JPanel(new BorderLayout(8, 0));
        this.passwordLabel = new JLabel("Password:");
        this.passwordField = new JPasswordField();
        this.confirmPasswordRow = new JPanel(new BorderLayout(8, 0));
        this.confirmPasswordLabel = new JLabel("Confirm Password:");
        this.confirmPasswordField = new JPasswordField();
        this.roleRow = new JPanel(new BorderLayout(8, 0));
        this.roleLabel = new JLabel("Register as:");
        this.roleComboBox = new JComboBox<>(new String[]{"User", "Admin"});
        this.messageLabel = new JLabel("", SwingConstants.CENTER);
        this.buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        this.registerButton = new JButton("Register");
        this.backButton = new JButton("Back to Login");
    }

    /**
     * Creates a new {@code RegisterUI} instance and makes the registration
     * window visible. Called from {@link LoginUI} when the Register button
     * is clicked.
     */
    public static void initialize() {
        RegisterUI.registerUI = new RegisterUI();
        RegisterUI.registerUI.initializeAllElements();
        RegisterUI.registerUI.mainFrame.add(RegisterUI.registerUI.mainPanel);
        RegisterUI.registerUI.mainFrame.setVisible(true);
    }

    /**
     * Configures and lays out all UI components within the main frame and
     * attaches action listeners to the Register and Back to Login buttons.
     * <p>
     * On register: performs the following validations in order before
     * committing the new account:
     * <ul>
     *   <li>Username, email, and password fields must be non-empty.</li>
     *   <li>Password and confirm password fields must match.</li>
     *   <li>Email must conform to the standard format validated by
     *       {@link Credential#isValidEmail(String)}.</li>
     *   <li>The email must not already be registered in
     *       {@link CredentialRepository}.</li>
     * </ul>
     * On success, displays a confirmation dialog and returns to
     * {@link LoginUI}. On failure, displays an appropriate error message
     * via {@link DisplayMessage#displayError}.
     * <p>
     * On back: disposes the registration window and opens {@link LoginUI}.
     */
    private void initializeAllElements() {
        mainFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        mainFrame.setSize(420, 420);
        mainFrame.setLocationRelativeTo(null);
        mainFrame.setResizable(false);

        mainPanel.setLayout(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        mainPanel.add(titleLabel, BorderLayout.NORTH);

        usernameLabel.setPreferredSize(new Dimension(120, 25));
        usernameRow.add(usernameLabel, BorderLayout.WEST);
        usernameRow.add(usernameField, BorderLayout.CENTER);

        emailLabel.setPreferredSize(new Dimension(120, 25));
        emailRow.add(emailLabel, BorderLayout.WEST);
        emailRow.add(emailField, BorderLayout.CENTER);

        passwordLabel.setPreferredSize(new Dimension(120, 25));
        passwordRow.add(passwordLabel, BorderLayout.WEST);
        passwordRow.add(passwordField, BorderLayout.CENTER);

        confirmPasswordLabel.setPreferredSize(new Dimension(120, 25));
        confirmPasswordRow.add(confirmPasswordLabel, BorderLayout.WEST);
        confirmPasswordRow.add(confirmPasswordField, BorderLayout.CENTER);

        roleLabel.setPreferredSize(new Dimension(120, 25));
        roleRow.add(roleLabel, BorderLayout.WEST);
        roleRow.add(roleComboBox, BorderLayout.CENTER);

        messageLabel.setFont(new Font("Arial", Font.PLAIN, 12));

        formPanel.add(usernameRow);
        formPanel.add(emailRow);
        formPanel.add(passwordRow);
        formPanel.add(confirmPasswordRow);
        formPanel.add(roleRow);
        formPanel.add(messageLabel);
        mainPanel.add(formPanel, BorderLayout.CENTER);

        registerButton.setPreferredSize(new Dimension(100, 30));
        backButton.setPreferredSize(new Dimension(120, 30));
        buttonPanel.add(registerButton);
        buttonPanel.add(backButton);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        ////SUBSECTION - ADDING ACTION LISTENER TO THE REGISTER BUTTON
        this.registerButton.addActionListener(e -> {
            if (this.usernameField.getText().isEmpty() ||
                    this.emailField.getText().isEmpty() ||
                    new String(this.passwordField.getPassword()).isEmpty()) {
                DisplayMessage.displayError(this.mainFrame, "Required field must not be empty");
            } else if (!new String(this.passwordField.getPassword()).equals(
                    new String(this.confirmPasswordField.getPassword()))) {
                DisplayMessage.displayError(this.mainFrame, "Passwords do not match");
            } else if (!Credential.isValidEmail(this.emailField.getText())) {
                DisplayMessage.displayError(this.mainFrame, "Invalid email type");
            } else {
                boolean isAdmin = roleComboBox.getSelectedItem().equals("Admin");
                if (CredentialRepository.register(
                        this.usernameField.getText().trim(),
                        this.emailField.getText().trim(),
                        new String(this.passwordField.getPassword()),
                        isAdmin)) {
                    DisplayMessage.displayInformation(this.mainFrame, "Account created successful");
                    mainFrame.dispose();
                    LoginUI.initialize();
                } else {
                    DisplayMessage.displayError(this.mainFrame, "Failed to create account");
                }
            }
        });

        ////SUBSECTION - ADDING ACTION LISTENER TO THE BACK BUTTON
        this.backButton.addActionListener(e -> {
            mainFrame.dispose();
            LoginUI.initialize();
        });
    }
}