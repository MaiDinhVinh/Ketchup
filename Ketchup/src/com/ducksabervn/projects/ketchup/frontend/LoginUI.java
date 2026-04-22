package com.ducksabervn.projects.ketchup.frontend;

import javax.swing.*;
import java.awt.*;

public class LoginUI {

    //SINGLETON DESIGN PATTERN => 1 UI INSTANCE AT A TIME
    private static LoginUI loginUI;

    //The login main frame
    private JFrame mainFrame;

    //The login main content panel
    private JPanel mainPanel;

    //login window main label
    private JLabel titleLabel;

    //the login's form panel
    private JPanel formPanel;

    //user's login credential form
    private JPanel usernameRow;
    private JLabel usernameLabel;
    private JTextField usernameField;
    private JPanel passwordRow;
    private JLabel passwordLabel;
    private JPasswordField passwordField;

    //login as <row> form
    private JPanel roleRow;
    private JLabel roleLabel;
    private JComboBox<String> roleComboBox;

    //error label for all kind of erros
    private JLabel errorLabel;
    private JPanel buttonPanel;

    //login button or register button for users
    private JButton loginButton;
    private JButton registerButton;

    private LoginUI(){
        //initalize all fields here, customization task is delegated to a separate method
        this.mainFrame = new JFrame("Ketchup");
        this.mainPanel = new JPanel();
        this.titleLabel = new JLabel("Ketchup Login", SwingConstants.CENTER);
        this.formPanel = new JPanel(new GridLayout(4, 1, 8, 8));
        this.usernameRow = new JPanel(new BorderLayout(8, 0));
        this.usernameLabel = new JLabel("Username:");
        this.usernameField = new JTextField();
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
     * Since we are using singleton design pattern, this UI will only be initialized once
     * */
    public static void initalize(){
        if(LoginUI.loginUI == null){
            LoginUI.loginUI = new LoginUI();
        }
        LoginUI.loginUI.initalizeAllElement();
        LoginUI.loginUI.mainFrame.add(LoginUI.loginUI.mainPanel);
        LoginUI.loginUI.mainFrame.setVisible(true);
    }

    private void initalizeAllElement(){
        //intialize the main login Frame
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setSize(400, 320);
        mainFrame.setLocationRelativeTo(null);
        mainFrame.setResizable(false);

        //intialize the main login content panel
        mainPanel.setLayout(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        //intialize the title label
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        mainPanel.add(titleLabel, BorderLayout.NORTH);

        //initalize the login form panel
        usernameLabel.setPreferredSize(new Dimension(80, 25));
        usernameRow.add(usernameLabel, BorderLayout.WEST);
        usernameRow.add(usernameField, BorderLayout.CENTER);
        passwordLabel.setPreferredSize(new Dimension(80, 25));
        passwordRow.add(passwordLabel, BorderLayout.WEST);
        passwordRow.add(passwordField, BorderLayout.CENTER);
        roleLabel.setPreferredSize(new Dimension(80, 25));
        roleRow.add(roleLabel, BorderLayout.WEST);
        roleRow.add(roleComboBox, BorderLayout.CENTER);
        errorLabel.setForeground(Color.RED);
        errorLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        formPanel.add(usernameRow);
        formPanel.add(passwordRow);
        formPanel.add(roleRow);
        formPanel.add(errorLabel);
        mainPanel.add(formPanel, BorderLayout.CENTER);

        //initalize the login button section
        loginButton.setPreferredSize(new Dimension(100, 30));
        registerButton.setPreferredSize(new Dimension(100, 30));
        buttonPanel.add(loginButton);
        buttonPanel.add(registerButton);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        ////SUBSECTION - ADDING LISTENER TO THE LOGIN BUTTON
        this.loginButton.addActionListener(e -> {
            // TODO: Login input verification
            // TODO: Call AuthService.login(username, password, role)
            // TODO: If login success and role == "Admin"  -> open AdminDashboardUI
            // TODO: If login success and role == "Customer" -> open MovieListUI
            // TODO: If login failed -> errorLabel.setText("Invalid username or password.")
        });

        ////SUBSECTION - ADDING LISTENER TO THE REGISTER BUTTON
        this.registerButton.addActionListener(e -> {
            //shutdown the current window to move to a new one
            this.mainFrame.dispose();
            RegisterUI.initialize();
        });
    }
}
