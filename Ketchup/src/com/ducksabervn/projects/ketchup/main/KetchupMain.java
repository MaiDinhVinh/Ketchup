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

public class KetchupMain {
    public static void main(String[] args) {
        try{
            UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
        }catch(ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e){
            e.printStackTrace();
        }

        try{
            FileSystemInitializer.initalize();
            MovieRepository.setMovies(MovieCsvIO.getIO().readCsvFile());
            CredentialRepository.setCredentials(CredentialCsvIO.getIO().readCsvFile());
        }catch(IOException e){
            //I still cant figure out for which JFrame will responsible to display the
            //exception string, but this will work as a fallback for now
            DisplayMessage.displayError(AdminMovieListUI.getAdminMovieListUI().getMainFrame(),
                    e.getMessage());
        }
        SwingUtilities.invokeLater(LoginUI::initialize);
    }
}
