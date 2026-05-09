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
 * File Name:       KetchupMain.java
 * Developer:       Tran Phan Anh*, Nguyen The Khoi Nguyen*, Nguyen Dinh Quy*,
 *                  Mai Dinh Vinh* (* equal contributions)
 * Description:     Application entry point that bootstraps the file system,
 *                  loads all persistent data into memory, applies the Nimbus
 *                  look and feel, and launches the login screen to begin
 *                  the user session.
 ******************************************************************************/

package com.ducksabervn.projects.ketchup.main;

import com.ducksabervn.projects.ketchup.backend.io.CredentialCsvIO;
import com.ducksabervn.projects.ketchup.backend.repositories.MovieRepository;
import com.ducksabervn.projects.ketchup.backend.repositories.CredentialRepository;
import com.ducksabervn.projects.ketchup.backend.io.FileSystemInitializer;
import com.ducksabervn.projects.ketchup.backend.io.MovieCsvIO;
import com.ducksabervn.projects.ketchup.frontend.util.DisplayMessage;
import com.ducksabervn.projects.ketchup.frontend.AdminMovieListUI;
import com.ducksabervn.projects.ketchup.frontend.LoginUI;

import javax.swing.*;
import java.io.IOException;

/**
 * The main entry point of the Ketchup application. Responsible for three
 * sequential bootstrap tasks before handing control to the UI layer:
 * <ol>
 *   <li>Applying the Nimbus look and feel to all Swing components.</li>
 *   <li>Initializing the file system via {@link FileSystemInitializer},
 *       ensuring the application directory and all required CSV files exist.</li>
 *   <li>Loading all movie and credential records from their respective CSV
 *       files into {@link MovieRepository} and {@link CredentialRepository}.</li>
 * </ol>
 * Once bootstrapping is complete, {@link LoginUI} is launched on the Swing
 * Event Dispatch Thread via {@link SwingUtilities#invokeLater}.
 */
public class KetchupMain {

    /**
     * The application entry point. Performs all bootstrapping tasks in order —
     * look and feel setup, file system initialization, and CSV data loading —
     * then launches the login screen on the Event Dispatch Thread.
     * <p>
     * Any {@link IOException} thrown during file system initialization or CSV
     * reading is caught and displayed via
     * {@link DisplayMessage#displayError(java.awt.Component, String)}.
     * Any {@link javax.swing.UnsupportedLookAndFeelException} or related
     * reflective exceptions thrown during look and feel setup are printed to
     * the standard error stream and the application continues with the default
     * look and feel.
     *
     * @param args command-line arguments; not used by this application
     */
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
        } catch (ClassNotFoundException | InstantiationException |
                 IllegalAccessException | UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }

        try {
            FileSystemInitializer.initalize();
            MovieRepository.setMovies(MovieCsvIO.getIO().readCsvFile());
            CredentialRepository.setCredentials(CredentialCsvIO.getIO().readCsvFile());
        } catch (IOException e) {
            DisplayMessage.displayError(
                    AdminMovieListUI.getAdminMovieListUI().getMainFrame(),
                    e.getMessage());
        }

        SwingUtilities.invokeLater(LoginUI::initialize);
    }
}