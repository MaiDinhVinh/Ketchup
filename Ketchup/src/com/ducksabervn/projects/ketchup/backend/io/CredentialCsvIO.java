package com.ducksabervn.projects.ketchup.backend.io;

import com.ducksabervn.projects.ketchup.backend.model.Credential;
import com.ducksabervn.projects.ketchup.backend.repositories.CredentialRepository;

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
    public LinkedHashMap<String, Credential> readCsvFile() throws IOException{
        return this.readUserCredentialsCsv();
    }

    @Override
    public void updateLatestData() throws IOException{
        this.updateCredCsv();
    }

    private LinkedHashMap<String, Credential> readUserCredentialsCsv() throws IOException{
        List<String> allCreds = Files.readAllLines(AppPath.USER_CREDENTIALS.getAppPath());
        allCreds.remove(0);
        LinkedHashMap<String, Credential> credMap = new LinkedHashMap<>();
        for(String str: allCreds){
            String[] split = str.split(";");
            credMap.put(split[1], new Credential(split[0], split[1], split[2],Boolean.valueOf(split[3])));
        }
        return credMap;
    }

    private void updateCredCsv() throws IOException{
        LinkedHashMap<String, Credential> updatedCredentials = CredentialRepository.getCredentials();
        try(BufferedWriter bw2 = new BufferedWriter(new FileWriter(AppPath.USER_CREDENTIALS.getAppPath().toFile()))){
            bw2.write("USERNAME;EMAIL;PASSWORD;IS_ADMIN");
            for(Credential c: updatedCredentials.values()){
                bw2.newLine();
                bw2.write(generateCredentialDataAsString(c));
            }
        }catch(IOException e){
            throw e;
        }
    }

    public static String generateCredentialDataAsString(Credential c){
        String data = "%s;%s;%s;%b".formatted(c.getUsername(),
                c.getEmail(), c.getPassword(), c.isAdmin());
        return data;
    }
}
