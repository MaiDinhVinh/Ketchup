package com.ducksabervn.projects.ketchup.backend.helper;

import javax.swing.*;
import java.io.PrintWriter;
import java.io.StringWriter;

public final class DisplayMessage {
    public static void displayError(JFrame parentComponent, String message){
        JOptionPane.showMessageDialog(
                parentComponent,
                message,
                "Ketchup",
                JOptionPane.ERROR_MESSAGE
        );
    }

    public static boolean displayConfirmationDialog(JFrame mainFrame,
                                                 String message,
                                                 String title,
                                                 int optionPane){
        return JOptionPane.showConfirmDialog(mainFrame,
                message,
                title,
                optionPane) == JOptionPane.YES_OPTION;
    }

    public static void displayWarning(JFrame mainFrame,
                                      String message,
                                      String title,
                                      int optionPane){
        JOptionPane.showMessageDialog(mainFrame, message, title, optionPane);
    }
}
