package com.ducksabervn.projects.ketchup.backend.credientials;

import java.util.TreeMap;

public class Credential {

    private static TreeMap<String, Credential> credentials;

    private String email;
    private String password;
    private boolean isAdmin;

    public Credential(String email, String password, boolean isAdmin){
        this.email = email;
        this.password = password;
        this.isAdmin = isAdmin;
    }

    public boolean isAdmin() {
        return isAdmin;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public static TreeMap<String, Credential> getCredentials() {
        return credentials;
    }

    public static void setCredentials(TreeMap<String, Credential> credentials) {
        Credential.credentials = credentials;
    }

    public static boolean verifyCredential(String email){
        return Credential.credentials.containsKey(email);
    }

    public static Credential getUser(String email){
        return Credential.credentials.get(email);
    }
}
