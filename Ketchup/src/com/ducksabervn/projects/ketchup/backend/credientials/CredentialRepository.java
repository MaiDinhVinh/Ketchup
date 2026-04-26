package com.ducksabervn.projects.ketchup.backend.credientials;

import java.util.LinkedHashMap;

public class CredentialRepository {
    private static LinkedHashMap<String, Credential> credentials;

    public static LinkedHashMap<String, Credential> getCredentials() {
        return CredentialRepository.credentials;
    }

    public static void setCredentials(LinkedHashMap<String, Credential> credentials) {
        CredentialRepository.credentials = credentials;
    }

    public static boolean verifyCredential(String email){
        return CredentialRepository.credentials.containsKey(email);
    }

    public static Credential getUser(String email){
        return CredentialRepository.credentials.get(email);
    }
}
