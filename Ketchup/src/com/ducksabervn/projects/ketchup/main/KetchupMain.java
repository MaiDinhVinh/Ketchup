package com.ducksabervn.projects.ketchup.main;

import com.ducksabervn.projects.ketchup.frontend.*;

import javax.swing.*;
import java.util.ArrayList;

public class KetchupMain {
    public static void main(String[] args) {
        try{
            UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
        }catch(ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e){
            e.printStackTrace();
        }
        SwingUtilities.invokeLater(() -> {
            CustomerSeatSelectionUI.initialize("Test");
        });
    }
}
