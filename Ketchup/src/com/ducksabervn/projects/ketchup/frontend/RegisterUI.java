package com.ducksabervn.projects.ketchup.frontend;

import javax.swing.*;
import java.awt.*;

public class RegisterUI {

    //SINGLETON DESIGN PATTERN => ONLY 1 UI INSTANCE CAN BE RUN AT THE SAME TIME
    private static RegisterUI registerUI;

    //general login form intialization
    private JFrame mainFrame;
    private JPanel mainPanel;
    private JLabel titleLabel;
    private JPanel formPanel;

    //login crediential fields input initalization
    private JPanel fullNameRow;
    private JLabel fullNameLabel;
    private JTextField fullNameField;
    private JPanel usernameRow;
    private JLabel usernameLabel;
    private JTextField usernameField;
    private JPanel emailRow;
    private JLabel emailLabel;
    private JTextField emailField;
    private JPanel passwordRow;
    private JLabel passwordLabel;
    private JPasswordField passwordField;
    private JPanel confirmPasswordRow;
    private JLabel confirmPasswordLabel;
    private JPasswordField confirmPasswordField;

    //all kind of message goes here
    private JLabel messageLabel;

    //login + register button initalizations
    private JPanel buttonPanel;
    private JButton registerButton;
    private JButton backButton;

    private RegisterUI() {
        //All field initializatons happen here, fields customization is delegated to another fields
        this.mainFrame = new JFrame("Ketchup");
        this.mainPanel = new JPanel();
        this.titleLabel = new JLabel("Ketchup - Register", SwingConstants.CENTER);
        this.formPanel = new JPanel(new GridLayout(6, 1, 8, 8));
        this.fullNameRow = new JPanel(new BorderLayout(8, 0));
        this.fullNameLabel = new JLabel("Full Name:");
        this.fullNameField = new JTextField();
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
        this.messageLabel = new JLabel("", SwingConstants.CENTER);
        this.buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        this.registerButton = new JButton("Register");
        this.backButton = new JButton("Back to Login");
    }

    public static void initialize() {
        if (RegisterUI.registerUI == null) {
            RegisterUI.registerUI = new RegisterUI();
        }
        RegisterUI.registerUI.initializeAllElements();
        RegisterUI.registerUI.mainFrame.add(RegisterUI.registerUI.mainPanel);
        RegisterUI.registerUI.mainFrame.setVisible(true);
    }

    private void initializeAllElements() {

        //set up the main register JFrame
        mainFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        mainFrame.setSize(420, 380);
        mainFrame.setLocationRelativeTo(null);
        mainFrame.setResizable(false);

        //setup the register content panel
        mainPanel.setLayout(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        //setup the title for the register panel
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        mainPanel.add(titleLabel, BorderLayout.NORTH);

        //setup the register credential form
        fullNameLabel.setPreferredSize(new Dimension(120, 25));
        fullNameRow.add(fullNameLabel, BorderLayout.WEST);
        fullNameRow.add(fullNameField, BorderLayout.CENTER);
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

        //setup the all-kind-of-messages label
        messageLabel.setFont(new Font("Arial", Font.PLAIN, 12));

        //adding all customized element to the content pane of the registration
        formPanel.add(fullNameRow);
        formPanel.add(usernameRow);
        formPanel.add(emailRow);
        formPanel.add(passwordRow);
        formPanel.add(confirmPasswordRow);
        formPanel.add(messageLabel);
        mainPanel.add(formPanel, BorderLayout.CENTER);

        //adding all of the customized buttom to the content panel or its own wrapping panel for better look
        registerButton.setPreferredSize(new Dimension(100, 30));
        backButton.setPreferredSize(new Dimension(120, 30));
        buttonPanel.add(registerButton);
        buttonPanel.add(backButton);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        ////SUBSECTION - ADDING ACTION LISTENER TO THE REGISTER BUTTON
        this.registerButton.addActionListener(e -> {
            // TODO: Input verification
            // TODO: Call AuthService.register(fullName, username, email, password)
            // TODO: If username already exists -> messageLabel.setForeground(Color.RED) + messageLabel.setText("Username already taken.")
            // TODO: If register success -> messageLabel.setForeground(Color.GREEN) + messageLabel.setText("Registration successful!")
            // TODO: After success -> close this frame and open LoginUI
        });

        ////SUBSECTION - ADDING ACTION LISTENER TO THE LOGIN BUTTON
        this.backButton.addActionListener(e -> {
            //shutdown the current window to move to a new one
            mainFrame.dispose();
            LoginUI.initalize();
        });
    }
}