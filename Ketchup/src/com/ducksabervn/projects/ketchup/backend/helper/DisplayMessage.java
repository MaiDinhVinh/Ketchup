package com.ducksabervn.projects.ketchup.backend.helper;

import javax.swing.*;
import java.awt.*;
import java.io.PrintWriter;
import java.io.StringWriter;

public final class DisplayMessage {
    public static void displayError(Component component, String message){
        JOptionPane.showMessageDialog(
                component,
                message,
                "Ketchup",
                JOptionPane.ERROR_MESSAGE
        );
    }

    public static boolean displayConfirmationDialog(Component component,
                                                    String message,
                                                    String title){
        return JOptionPane.showConfirmDialog(component,
                message,
                title,
                JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION;
    }

    public static void displayWarning(Component component,
                                      String message){
        JOptionPane.showMessageDialog(component, message, "Ketchup", JOptionPane.WARNING_MESSAGE);
    }

    public static void displayInformation(Component component,
                                          String message){
        JOptionPane.showMessageDialog(component, message, "Ketchup", JOptionPane.INFORMATION_MESSAGE);
    }
}
