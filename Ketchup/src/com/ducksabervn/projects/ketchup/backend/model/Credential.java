package com.ducksabervn.projects.ketchup.backend.model;

import java.util.regex.Pattern;

public class Credential {
    private String username;
    private String email;
    private String password;
    private boolean isAdmin;

    private static final String EMAIL_REGEX_PATTERN =
            "^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^.-]+@([a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,}$";

    private static final Pattern EMAIL_PATTERN = Pattern.compile(EMAIL_REGEX_PATTERN);

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

    public static boolean isValidEmail(String email){
        return EMAIL_PATTERN.matcher(email).matches();
    }
}
