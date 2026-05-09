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
 * File Name:       DisplayMessage.java
 * Developer:       Tran Phan Anh*, Nguyen The Khoi Nguyen*, Nguyen Dinh Quy*,
 *                  Mai Dinh Vinh* (* equal contributions)
 * Description:     Utility class providing static helper methods for displaying
 *                  standardized Swing dialog boxes across the application,
 *                  including error, warning, information, and confirmation dialogs.
 ******************************************************************************/

package com.ducksabervn.projects.ketchup.frontend.util;

import javax.swing.*;
import java.awt.*;

/**
 * Utility class that centralizes all user-facing dialog box interactions
 * in the Ketchup application. All methods are static; this class is not
 * meant to be instantiated. Each method wraps a {@link JOptionPane} call
 * with a consistent title and message type to ensure a uniform look and
 * feel across all UI screens.
 */
public final class DisplayMessage {

    /**
     * Displays a modal error dialog with the given message and an
     * {@link JOptionPane#ERROR_MESSAGE} icon. Used to notify the user
     * of invalid input, authentication failures, or I/O errors.
     *
     * @param component the parent {@link Component} to center the dialog over;
     *                  may be {@code null} to center on screen
     * @param message   the error message to display
     */
    public static void displayError(Component component, String message) {
        JOptionPane.showMessageDialog(
                component,
                message,
                "Ketchup",
                JOptionPane.ERROR_MESSAGE
        );
    }

    /**
     * Displays a modal Yes/No confirmation dialog with the given message
     * and title, and returns whether the user selected Yes.
     *
     * @param component the parent {@link Component} to center the dialog over;
     *                  may be {@code null} to center on screen
     * @param message   the confirmation question to display
     * @param title     the title text shown in the dialog's title bar
     * @return {@code true} if the user clicked Yes, {@code false} if the
     *         user clicked No or closed the dialog
     */
    public static boolean displayConfirmationDialog(Component component,
                                                    String message,
                                                    String title) {
        return JOptionPane.showConfirmDialog(component,
                message,
                title,
                JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION;
    }

    /**
     * Displays a modal warning dialog with the given message and a
     * {@link JOptionPane#WARNING_MESSAGE} icon. Used to alert the user
     * to missing selections or incomplete actions that do not constitute
     * a hard error.
     *
     * @param component the parent {@link Component} to center the dialog over;
     *                  may be {@code null} to center on screen
     * @param message   the warning message to display
     */
    public static void displayWarning(Component component, String message) {
        JOptionPane.showMessageDialog(component, message, "Ketchup", JOptionPane.WARNING_MESSAGE);
    }

    /**
     * Displays a modal information dialog with the given message and an
     * {@link JOptionPane#INFORMATION_MESSAGE} icon. Used to confirm
     * successful operations such as account creation or booking confirmation.
     *
     * @param component the parent {@link Component} to center the dialog over;
     *                  may be {@code null} to center on screen
     * @param message   the informational message to display
     */
    public static void displayInformation(Component component, String message) {
        JOptionPane.showMessageDialog(component, message, "Ketchup", JOptionPane.INFORMATION_MESSAGE);
    }
}