package com.ducksabervn.projects.ketchup.backend.auth;

import java.util.LinkedHashMap;

public class CredentialRepository {
    private static LinkedHashMap<String, Credential> credentials;

    public static LinkedHashMap<String, Credential> getCredentials() {
        return CredentialRepository.credentials;
    }

    public static void setCredentials(LinkedHashMap<String, Credential> credentials) {
        CredentialRepository.credentials = credentials;
    }

    public static boolean verifyCredential(String email, String password){
        return CredentialRepository.credentials.containsKey(email) &&
                CredentialRepository.credentials.get(email).getPassword().equals(password);
    }

    public static Credential getUser(String email){
        return CredentialRepository.credentials.get(email);
    }

    public static boolean register(String username, String email, String password, boolean isAdmin){
        if(CredentialRepository.credentials.containsKey(email)){
            return false;
        }else{
            CredentialRepository.credentials.put(email, new Credential(username, email, password, isAdmin));
            return true;
        }
    }
}
