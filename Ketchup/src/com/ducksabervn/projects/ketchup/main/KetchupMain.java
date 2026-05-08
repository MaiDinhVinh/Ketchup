package com.ducksabervn.projects.ketchup.main;

import com.ducksabervn.projects.ketchup.backend.io.CredentialCsvIO;
import com.ducksabervn.projects.ketchup.backend.movie.MovieRepository;
import com.ducksabervn.projects.ketchup.backend.auth.CredentialRepository;
import com.ducksabervn.projects.ketchup.backend.io.FileSystemInitializer;
import com.ducksabervn.projects.ketchup.backend.io.MovieCsvIO;
import com.ducksabervn.projects.ketchup.frontend.LoginUI;

import javax.swing.*;

public class KetchupMain {
    public static void main(String[] args) {
        try{
            UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
        }catch(ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e){
            e.printStackTrace();
        }
        FileSystemInitializer.initalize();
        MovieRepository.setMovies(MovieCsvIO.getIO().readCsvFile());
        CredentialRepository.setCredentials(CredentialCsvIO.getIO().readCsvFile());
        SwingUtilities.invokeLater(LoginUI::initialize);
    }
}
