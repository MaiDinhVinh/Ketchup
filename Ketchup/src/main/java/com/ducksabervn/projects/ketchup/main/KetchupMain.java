/******************************************************************************
 * Project Name:    Ketchup - A movie management system
 * Course:          COMP1020 - OOP and Data Structure
 * Semester:        Spring 2026
 *
 * Members: Tran Phan Anh <25anh.tp@vinuni.edu.vn>,
 *          Nguyen Trong Khoi Nguyen <25nguyen.ntk@vinuni.edu.vn>,
 *          Nguyen Dinh Quy <25quy.nd@vinuni.edu.vn>,
 *          Hoang Duc Phat <25phat.hd@vinuni.edu.vn>,
 *          Mai Dinh Vinh <25vinh.md@vinuni.edu.vn>
 *
 * File Name:       KetchupMain.java
 * Developer:       Tran Phan Anh*, Nguyen Trong Khoi Nguyen*, Nguyen Dinh Quy*,
 *                  Mai Dinh Vinh* (* equal contributions)
 * Description:     JavaFX application entry point that bootstraps the file
 *                  system, loads all persistent data into memory, and launches
 *                  the login screen. Migrated from Swing (SwingUtilities,
 *                  Nimbus look and feel) to JavaFX Application lifecycle.
 ******************************************************************************/

package com.ducksabervn.projects.ketchup.main;

import com.ducksabervn.projects.ketchup.backend.database.DatabaseService;
import com.ducksabervn.projects.ketchup.backend.repositories.CredentialRepository;
import com.ducksabervn.projects.ketchup.backend.repositories.MovieRepository;
import com.ducksabervn.projects.ketchup.frontend.LoginUIController;
import com.ducksabervn.projects.ketchup.frontend.util.DisplayMessage;
import io.github.palexdev.materialfx.theming.JavaFXThemes;
import io.github.palexdev.materialfx.theming.MaterialFXStylesheets;
import io.github.palexdev.materialfx.theming.UserAgentBuilder;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;

import java.sql.SQLException;
import java.util.concurrent.CountDownLatch;

/**
 * The main entry point of the Ketchup application.
 *
 * Responsible for three sequential bootstrap tasks before handing control
 * to the UI layer:
 *
 * 1. Initializing the file system via FileSystemInitializer, ensuring the
 *    application directory and all required CSV files exist.
 * 2. Loading all movie records from MOVIES.csv into MovieRepository.
 * 3. Loading all credential records from USER_CREDENTIALS.csv into
 *    CredentialRepository.
 *
 * Once bootstrapping is complete, LoginUIController.initialize() is called
 * on the JavaFX Application Thread (already the case inside start()).
 *
 * The Nimbus look-and-feel setup from the original Swing version is removed
 * as it is Swing-specific and has no equivalent in JavaFX.
 */
public class KetchupMain extends Application {

    @Override
    public void init() throws Exception {
        if(!DatabaseService.isInitialized()){
            CountDownLatch latch = new CountDownLatch(1);
            final boolean[] shouldSeed = {false};

            Platform.runLater(() -> {
                shouldSeed[0] = DisplayMessage.displayConfirmationDialog(
                        "Do you want to initialize test dataset?");
                latch.countDown();
            });
            latch.await();
            DatabaseService.startup(shouldSeed[0]);
        }else{
            DatabaseService.startup(false);
        }
    }

    @Override
    public void stop() throws Exception {
        DatabaseService.shutdown();
    }

    /**
     * JavaFX application entry point. Called on the JavaFX Application Thread
     * after the toolkit is initialized.
     *
     * The primaryStage provided by the framework is unused here because each
     * screen manages its own Stage. This keeps the controller pattern consistent
     * with the rest of the migrated UI layer.
     *
     * Any IOException thrown during file system initialization or CSV reading
     * is caught and shown via a blocking error Alert before the application exits.
     *
     * @param primaryStage the initial Stage provided by the JavaFX runtime;
     *                     not used by this application
     */
    @Override
    public void start(Stage primaryStage) {
        try {
            MovieRepository.loadMovies();
            CredentialRepository.loadCredentials();
        }catch(SQLException e){
            DisplayMessage.displayError(e.getMessage());
        }
        UserAgentBuilder.builder()
                .themes(JavaFXThemes.MODENA) // Optional if you don't need JavaFX's default theme, still recommended though
                .themes(MaterialFXStylesheets.forAssemble(true)) // Adds the MaterialFX's default theme. The boolean argument is to include legacy controls
                .setDeploy(true) // Whether to deploy each theme's assets on a temporary dir on the disk
                .setResolveAssets(true) // Whether to try resolving @import statements and resources urls
                .build() // Assembles all the added themes into a single CSSFragment (very powerful class check its documentation)
                .setGlobal(); // Finally, sets the produced stylesheet as the global User-Agent stylesheet//Once everything has been set up, redirect the user to the authenication form
        LoginUIController.initialize();
    }

    /**
     * Standard Java main method. Delegates to Application.launch() which
     * initializes the JavaFX toolkit and calls start() on the
     * JavaFX Application Thread.
     *
     * Replaces the original Swing bootstrap sequence:
     * UIManager.setLookAndFeel(...) and SwingUtilities.invokeLater(...).
     *
     * @param args command-line arguments; not used by this application
     */
    public static void main(String[] args) {
        launch(args);
    }
}