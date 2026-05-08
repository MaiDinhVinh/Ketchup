package com.ducksabervn.projects.ketchup.backend.io;

import com.ducksabervn.projects.ketchup.backend.auth.Credential;
import com.ducksabervn.projects.ketchup.backend.auth.CredentialRepository;
import com.ducksabervn.projects.ketchup.backend.ui.DisplayMessage;
import com.ducksabervn.projects.ketchup.frontend.AdminMovieListUI;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.LinkedHashMap;
import java.util.List;

public class CredentialCsvIO implements CsvIO<String, Credential> {

    private static CredentialCsvIO IO;

    private CredentialCsvIO(){
    }

    public static CredentialCsvIO getIO(){
        if(CredentialCsvIO.IO == null){
            CredentialCsvIO.IO = new CredentialCsvIO();
        }
        return CredentialCsvIO.IO;
    }

    @Override
    public LinkedHashMap<String, Credential> readCsvFile(){
        return this.readUserCredentialsCsv();
    }

    @Override
    public LinkedHashMap<String, Credential> readCsvFile(String requiredInformation) {
        throw new UnsupportedOperationException("Unsupported Operation");
    }

    @Override
    public void updateLatestData() {
        this.updateCredCsv();
    }

    private LinkedHashMap<String, Credential> readUserCredentialsCsv(){
        try{
            List<String> allCreds = Files.readAllLines(FileSystemInitializer.getUserCredentials());
            allCreds.remove(0);
            LinkedHashMap<String, Credential> credMap = new LinkedHashMap<>();
            for(String str: allCreds){
                String[] split = str.split(";");
                credMap.put(split[1], new Credential(split[0], split[1], split[2],Boolean.valueOf(split[3])));
            }
            return credMap;
        }catch(IOException e){
            //I still cant figure out for which JFrame will responsible to display the
            //exception string, but this will work as a fallback for now
            DisplayMessage.displayError(AdminMovieListUI.getAdminMovieListUI().getMainFrame(),
                    e.getMessage());
            return null;
        }
    }

    private void updateCredCsv(){
        LinkedHashMap<String, Credential> updatedCredentials = CredentialRepository.getCredentials();
        try(BufferedWriter bw2 = new BufferedWriter(new FileWriter(FileSystemInitializer.getUserCredentials().toFile()))){
            bw2.write("USERNAME;EMAIL;PASSWORD;IS_ADMIN");
            for(Credential c: updatedCredentials.values()){
                bw2.newLine();
                bw2.write(CredentialRepository.generateCredentialDataAsString(c));
            }
        }catch(IOException e){
            //I still cant figure out for which JFrame will responsible to display the
            //exception string, but this will work as a fallback for now
            DisplayMessage.displayError(AdminMovieListUI.getAdminMovieListUI().getMainFrame(),
                    e.getMessage());
        }
    }
}
