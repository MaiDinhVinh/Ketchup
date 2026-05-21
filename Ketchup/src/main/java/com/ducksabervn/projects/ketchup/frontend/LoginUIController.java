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
 * File Name:       LoginUIController.java
 * Developer:       Tran Phan Anh*, Nguyen Trong Khoi Nguyen*, Nguyen Dinh Quy*,
 *                  Mai Dinh Vinh* (* equal contributions)
 * Description:     JavaFX controller for LoginUI.fxml. Preserves all original
 *                  authentication logic from the Swing LoginUI class, now wired
 *                  to MaterialFX components via FXML injection. Migrated from
 *                  Swing JFrame / JOptionPane to JavaFX Stage / Alert.
 ******************************************************************************/

package com.ducksabervn.projects.ketchup.frontend;

import com.ducksabervn.projects.ketchup.backend.io.BookingCsvIO;
import com.ducksabervn.projects.ketchup.backend.model.Credential;
import com.ducksabervn.projects.ketchup.backend.repositories.BookingRepository;
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
 * Controller for the Login screen (LoginUI.fxml).
 * Responsibilities mirror the original {@code LoginUI.initalizeAllElement()} logic:
 *   Validate that email and password fields are non-empty.
 *   Call {@link CredentialRepository#verifyCredential} to authenticate.
 *   Check that the selected role matches the account's admin flag.
 *   On success as "Admin" -> navigate to {@code AdminMovieListUI.fxml}.
 *   On success as "User" -> load bookings via {@link BookingCsvIO}, then
 *       navigate to {@code CustomerHomeUI.fxml}.
 *   Register button -> navigate to {@code RegisterUI.fxml}.
 */
public class LoginUIController implements Initializable {

    /** Email input field. */
    @FXML private MFXTextField emailField;

    /** Password input field (masked). */
    @FXML private MFXPasswordField passwordField;

    /**
     * Role selector. Populated with "User" and "Admin" in {@link #initialize}.
     * Mirrors original {@code JComboBox<>(new String[]{"User","Admin"})}.
     */
    @FXML private MFXComboBox<String> roleComboBox;

    /** Inline error label — shown instead of a dialog for minor validation errors. */
    @FXML private Label errorLabel;

    /** Primary action button — triggers {@link #handleLogin()}. */
    @FXML private MFXButton loginButton;

    /** Secondary action button — triggers {@link #handleRegister()}. */
    @FXML private MFXButton registerButton;

    /**
     * Called automatically by the FXMLLoader after all @FXML fields are injected.
     * Populates the role combo box with the same options as the original Swing version.
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        roleComboBox.getItems().addAll("User", "Admin");
        roleComboBox.selectFirst(); // default: "User"
        errorLabel.setText("");
        emailField.setFloatMode(FloatMode.DISABLED);
        passwordField.setFloatMode(FloatMode.DISABLED);
        roleComboBox.setFloatMode(FloatMode.DISABLED);
    }

    /**
     * Static factory — loads LoginUI.fxml into a new {@link Stage} and shows it.
     * Replaces the original {@code LoginUI.initialize()} static method.
     */
    public static void initialize() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    LoginUIController.class.getResource(
                            "/fxml/LoginUI.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Ketchup");
            stage.setScene(new Scene(root, 600, 600));
            stage.setResizable(false);
            stage.show();
        } catch (IOException e) {
            DisplayMessage.displayError(e.getMessage());
        }
    }

    /**
     * Handles the "Sign In" button click.
     *
     * <p>Validation order (mirrors original):
     * <ol>
     *   <li>Email or password empty → show inline error.</li>
     *   <li>{@link CredentialRepository#verifyCredential} fails → show error dialog.</li>
     *   <li>Non-admin user attempts Admin login → show error dialog.</li>
     *   <li>Success as Admin → open {@code AdminMovieListUI.fxml}, close this stage.</li>
     *   <li>Success as User → load bookings, open {@code CustomerHomeUI.fxml},
     *       close this stage.</li>
     * </ol>
     */
    @FXML
    private void handleLogin() {
        if (emailField.getText().isBlank() || passwordField.getText().isBlank()) {
            DisplayMessage.displayError("Required field must not be empty");
            return;
        }

        String email    = emailField.getText().trim();
        String password = passwordField.getText();
        String role     = roleComboBox.getValue();

        if (!CredentialRepository.verifyCredential(email, password)) {
            DisplayMessage.displayError("Authentication failed");
            return;
        }
        Credential c = CredentialRepository.getUser(email);
        if (!c.isAdmin() && "Admin".equals(role)) {
            DisplayMessage.displayError("Authentication failed");
            return;
        }
        if ("Admin".equals(role)) {
            AdminMovieListUIController.initialize(c.getUsername());
        } else {
            try {
                BookingRepository.loadBookingsForUser(c.getEmail());
            } catch (SQLException ex) {
                DisplayMessage.displayError(ex.getMessage());
                return;
            }
            CustomerHomeUIController.initialize(c.getUsername(), c.getEmail());
        }

        getStage().close();
    }

    /**
     * Handles the "Create New Account" button click.
     * Closes this window and opens the Register screen.
     * Mirrors original: {@code mainFrame.dispose(); RegisterUI.initialize();}.
     */
    @FXML
    private void handleRegister() {
        RegisterUIController.initialize();
        getStage().close();
    }

    /**
     * Retrieves the {@link Stage} that owns this controller's scene.
     * Used to pass the window reference to {@link DisplayMessage} dialogs.
     *
     * @return the current {@link Stage}
     */
    private Stage getStage() {
        return (Stage) loginButton.getScene().getWindow();
    }
}