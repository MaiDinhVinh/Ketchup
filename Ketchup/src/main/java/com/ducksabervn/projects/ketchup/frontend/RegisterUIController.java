/******************************************************************************
 * Project Name:    Ketchup - A movie management system
 * Course:          COMP1020 - OOP and Data Structure
 * Semester:        Spring 2026
 * <p>
 * Members: Tran Phan Anh <25anh.tp@vinuni.edu.vn>,
 *          Nguyen Trong Khoi Nguyen <25nguyen.ntk@vinuni.edu.vn>,
 *          Nguyen Dinh Quy <25quy.nd@vinuni.edu.vn>,
 *          Hoang Duc Phat <25phat.hd@vinuni.edu.vn>,
 *          Mai Dinh Vinh <25vinh.md@vinuni.edu.vn>
 * <p>
 * File Name:       RegisterUIController.java
 * Developers:       Hoang Duc Phat*, Mai Dinh Vinh* (* equal contributions)
 * Description:     JavaFX controller for RegisterUI.fxml. Preserves all original
 *                  input-validation and account-creation logic from the Swing
 *                  RegisterUI class, now wired to MaterialFX components via FXML
 *                  injection. Migrated from Swing JFrame / JOptionPane to
 *                  JavaFX Stage / Alert.
 ******************************************************************************/

package com.ducksabervn.projects.ketchup.frontend;

import com.ducksabervn.projects.ketchup.backend.model.Credential;
import com.ducksabervn.projects.ketchup.backend.repositories.CredentialRepository;
import com.ducksabervn.projects.ketchup.frontend.util.DisplayMessage;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXComboBox;
import io.github.palexdev.materialfx.controls.MFXPasswordField;
import io.github.palexdev.materialfx.controls.MFXTextField;
import io.github.palexdev.materialfx.enums.FloatMode;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

/**
 * Controller for the Register screen (RegisterUI.fxml).
 * Validation order mirrors the original {@code RegisterUI.initializeAllElements()} logic:
 *   Username, email, or password field is blank → inline error.
 *   Password and confirm-password do not match → inline error.
 *   Email fails {@link Credential#isValidEmail} regex → inline error.
 *   {@link CredentialRepository#register} returns {@code false}
 *       (email already taken) → error dialog.
 *   Success → success dialog → close this window → open {@code LoginUI.fxml}.
 * The Back button closes this window and re-opens {@code LoginUI.fxml}.
 */
public class RegisterUIController implements Initializable {

    /** Desired display-name for the new account. */
    @FXML private MFXTextField     usernameField;

    /** Email address that will serve as the unique account identifier. */
    @FXML private MFXTextField     emailField;

    /** Desired password — masked. */
    @FXML private MFXPasswordField passwordField;

    /**
     * Password confirmation — must match {@link #passwordField}.
     * Registration is blocked when the two values differ.
     */
    @FXML private MFXPasswordField confirmPasswordField;

    /**
     * Role selector populated with "User" and "Admin" in {@link #initialize}.
     * Mirrors original {@code JComboBox<>(new String[]{"User","Admin"})}.
     */
    @FXML private MFXComboBox<String> roleComboBox;

    /** Triggers {@link #handleRegister()} — primary action. */
    @FXML private MFXButton registerButton;

    /** Triggers {@link #handleBack()} — returns to login. */
    @FXML private MFXButton backButton;

    /**
     * Called automatically by the FXMLLoader after all @FXML fields are injected.
     * Populates the role combo box identically to the original Swing version.
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        roleComboBox.getItems().addAll("User", "Admin");
        roleComboBox.selectFirst(); // default: "User"
        usernameField.setFloatMode(FloatMode.DISABLED);
        emailField.setFloatMode(FloatMode.DISABLED);
        passwordField.setFloatMode(FloatMode.DISABLED);
        confirmPasswordField.setFloatMode(FloatMode.DISABLED);
        roleComboBox.setFloatMode(FloatMode.DISABLED);
    }

    /**
     * Static factory — loads RegisterUI.fxml into a new {@link Stage} and shows it.
     * Replaces the original {@code RegisterUI.initialize()} static method.
     */
    public static void initialize() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    RegisterUIController.class.getResource(
                            "/fxml/RegisterUI.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Ketchup");
            stage.setScene(new Scene(root, 700, 700));
            stage.setResizable(false);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Handles the "Create Account" button click.
     *
     * <p>Validation steps (identical to original Swing logic):
     * <ol>
     *   <li>Any required field blank → show inline error, abort.</li>
     *   <li>Passwords do not match → show inline error, abort.</li>
     *   <li>Email format invalid ({@link Credential#isValidEmail}) → inline error, abort.</li>
     *   <li>{@link CredentialRepository#register} returns {@code false}
     *       → show error dialog (email already registered), abort.</li>
     *   <li>Success → show information dialog, navigate back to Login.</li>
     * </ol>
     */
    @FXML
    private void handleRegister() {
        String username        = usernameField.getText().trim();
        String email           = emailField.getText().trim();
        String password        = passwordField.getText();
        String confirmPassword = confirmPasswordField.getText();
        String role            = roleComboBox.getValue();

        if (username.isEmpty() || email.isEmpty() || password.isEmpty()) {
            DisplayMessage.displayError("Required field must not be empty");
            return;
        }

        if (!password.equals(confirmPassword)) {
            DisplayMessage.displayError("Passwords do not match");
            return;
        }

        if (!Credential.isValidEmail(email)) {
            DisplayMessage.displayError("Invalid email type");
            return;
        }

        boolean isAdmin = "Admin".equals(role);
        try{
            if (!CredentialRepository.register(username, email, password, isAdmin)) {
                DisplayMessage.displayError("Failed to create account");
                return;
            }
        }catch(SQLException e){
            DisplayMessage.displayError(e.getMessage());
        }

        DisplayMessage.displayInformation("Account created successfully");
        navigateToLogin();
    }

    /**
     * Handles the "Back to Sign In" button click.
     * Closes this window and returns the user to the Login screen.
     * Mirrors original: {@code mainFrame.dispose(); LoginUI.initialize();}.
     */
    @FXML
    private void handleBack() {
        navigateToLogin();
    }

    /**
     * Closes this stage and opens the Login screen.
     * Mirrors original {@code mainFrame.dispose(); LoginUI.initialize();}.
     */
    private void navigateToLogin() {
        getStage().close();
        LoginUIController.initialize();
    }

    /**
     * Retrieves the {@link Stage} that owns this controller's scene.
     * Used to pass the window reference to {@link DisplayMessage} dialogs.
     *
     * @return the current {@link Stage}
     */
    private Stage getStage() {
        return (Stage) registerButton.getScene().getWindow();
    }
}