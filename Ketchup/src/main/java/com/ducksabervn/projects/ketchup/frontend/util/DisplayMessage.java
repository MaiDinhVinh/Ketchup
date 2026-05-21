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
 * File Name:       DisplayMessage.java
 * Developer:       Tran Phan Anh*, Nguyen Trong Khoi Nguyen*, Nguyen Dinh Quy*,
 *                  Mai Dinh Vinh* (* equal contributions)
 * Description:     Utility class providing static helper methods for displaying
 *                  standardized JavaFX Alert dialogs across the application,
 *                  including error, warning, information, and confirmation dialogs.
 *                  Migrated from Swing JOptionPane to JavaFX Alert.
 ******************************************************************************/

package com.ducksabervn.projects.ketchup.frontend.util;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.stage.Window;

import java.util.Optional;

/**
 * Utility class that centralizes all user-facing dialog box interactions
 * in the Ketchup application. All methods are static; this class is not
 * meant to be instantiated. Each method wraps a JavaFX {@link Alert} call
 * with a consistent title and message type to ensure a uniform look and
 * feel across all UI screens.
 *
 * <p>Migrated from {@code javax.swing.JOptionPane} to {@code javafx.scene.control.Alert}.
 * The {@code component} parameter (AWT {@code Component}) has been replaced with
 * {@code javafx.stage.Window} for proper dialog ownership and centering in JavaFX.</p>
 */
public final class DisplayMessage {

    /**
     * Private constructor — this class is not meant to be instantiated.
     */
    private DisplayMessage() {}

    /**
     * Builds and styles a {@link Alert} dialog with Netflix-themed dark CSS,
     * optionally owned by the provided {@link Window}.
     *
     * @param type    the {@link AlertType} of the dialog
     * @param message the message body to display
     * @return a fully configured {@link Alert} ready to be shown
     */
    private static Alert buildAlert(AlertType type, String message) {
        Alert alert = new Alert(type);
        alert.setTitle("Ketchup");
        alert.setHeaderText(null);
        alert.setContentText(message);

        // Apply Netflix-themed dark stylesheet to the dialog
        alert.getDialogPane()
                .getStylesheets()
                .add(DisplayMessage.class
                        .getResource("/css/Alerts.css")
                        .toExternalForm());
        alert.getDialogPane().getStyleClass().add("ketchup-dialog");

        return alert;
    }

    /**
     * Displays a modal error dialog with the given message and an
     * {@link AlertType#ERROR} icon. Used to notify the user
     * of invalid input, authentication failures, or I/O errors.
     *
     * @param message the error message to display
     */
    public static void displayError(String message) {
        Alert alert = buildAlert(AlertType.ERROR, message);
        alert.showAndWait();
    }

    /**
     * Displays a modal warning dialog with the given message and a
     * {@link AlertType#WARNING} icon. Used to alert the user
     * to missing selections or incomplete actions that do not constitute
     * a hard error.
     *
     * @param message the warning message to display
     */
    public static void displayWarning(String message) {
        Alert alert = buildAlert(AlertType.WARNING, message);
        alert.showAndWait();
    }

    /**
     * Displays a modal information dialog with the given message and an
     * {@link AlertType#INFORMATION} icon. Used to confirm
     * successful operations such as account creation or booking confirmation.
     *
     * @param message the informational message to display
     */
    public static void displayInformation(String message) {
        Alert alert = buildAlert(AlertType.INFORMATION, message);
        alert.showAndWait();
    }

    /**
     * Displays a modal Yes/No confirmation dialog with the given message
     * and title, and returns whether the user selected Yes.
     *
     * @param message the confirmation question to display
     * @return {@code true} if the user clicked Yes, {@code false} if the
     *         user clicked No or closed the dialog
     */
    public static boolean displayConfirmationDialog(String message) {
        Alert alert = buildAlert(AlertType.CONFIRMATION, message);
        alert.setTitle("Ketchup");
        alert.getButtonTypes().setAll(ButtonType.YES, ButtonType.NO);

        Optional<ButtonType> result = alert.showAndWait();
        return result.isPresent() && result.get() == ButtonType.YES;
    }
}