package com.ducksabervn.projects.ketchup.main;

import com.ducksabervn.projects.ketchup.backend.admin.MovieRepository;
import com.ducksabervn.projects.ketchup.backend.credientials.CredentialRepository;
import com.ducksabervn.projects.ketchup.backend.helper.ReadCSVFile;
import com.ducksabervn.projects.ketchup.frontend.LoginUI;

import javax.swing.*;

public class KetchupMain {
    public static void main(String[] args) {
        try{
            UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
        }catch(ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e){
            e.printStackTrace();
        }
        ReadCSVFile.initalize();
        MovieRepository.setMovies(ReadCSVFile.readMoviesCsv());
        CredentialRepository.setCredentials(ReadCSVFile.readUserCredentialsCsv());
        SwingUtilities.invokeLater(LoginUI::initialize);
    }
}
