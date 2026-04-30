package com.ducksabervn.projects.ketchup.frontend;

import com.ducksabervn.projects.ketchup.backend.credientials.CredentialRepository;
import com.ducksabervn.projects.ketchup.backend.helper.DisplayMessage;
import com.ducksabervn.projects.ketchup.backend.credientials.Credential;
import com.ducksabervn.projects.ketchup.backend.helper.ReadCSVFile;

import javax.swing.*;
import java.awt.*;

public class LoginUI {

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
    private JPanel emailRow;
    private JLabel emailLabel;
    private JTextField emailField;
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
     * Since we are using singleton design pattern, this UI will only be initialized once
     * */
    public static void initialize(){
        LoginUI.loginUI = new LoginUI();
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

        //initalize the login button section
        loginButton.setPreferredSize(new Dimension(100, 30));
        registerButton.setPreferredSize(new Dimension(100, 30));
        buttonPanel.add(loginButton);
        buttonPanel.add(registerButton);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        ////SUBSECTION - ADDING LISTENER TO THE LOGIN BUTTON
        this.loginButton.addActionListener(e -> {
            if(emailField.getText().isEmpty() || passwordField.getText().isEmpty()){
                DisplayMessage.displayError(this.mainFrame, "Required field must not be empty");
            }else{
                if(CredentialRepository.verifyCredential(emailField.getText())){
                    String role = (String) roleComboBox.getSelectedItem();
                    Credential c = CredentialRepository.getUser(emailField.getText());
                    if(!c.isAdmin() && role.equals("Admin")){
                        DisplayMessage.displayError(this.mainFrame, "Authentication failed");
                    }else{
                        if(role.equals("Admin")){
                            AdminMovieListUI.initialize(c.getUsername());
                        }
                    }
                    this.mainFrame.dispose();
                }else{
                    DisplayMessage.displayError(this.mainFrame, "Authentication failed");
                }
            }
        });

        ////SUBSECTION - ADDING LISTENER TO THE REGISTER BUTTON
        this.registerButton.addActionListener(e -> {
            //shutdown the current window to move to a new one
            this.mainFrame.dispose();
            RegisterUI.initialize();
        });
    }
}
