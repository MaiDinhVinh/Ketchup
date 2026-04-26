package com.ducksabervn.projects.ketchup.backend.credientials;

import java.util.TreeMap;

public class Credential {
    private String username;
    private String email;
    private String password;
    private boolean isAdmin;

    public Credential(String username, String email, String password, boolean isAdmin){
        this.username = username;
        this.email = email;
        this.password = password;
        this.isAdmin = isAdmin;
    }

    public String getUsername() {
        return username;
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
}
