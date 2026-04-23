package com.ducksabervn.projects.ketchup.backend.helper;

import javax.swing.*;

public final class DisplayMessage {
    public static void displayError(JFrame parentComponent, String message){
        JOptionPane.showMessageDialog(
                parentComponent,
                message,
                "Ketchup",
                JOptionPane.ERROR_MESSAGE
        );
    }
}
