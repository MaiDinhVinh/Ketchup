package com.ducksabervn.projects.ketchup.backend.credientials;

import java.util.TreeMap;

public class Credential {
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
}
