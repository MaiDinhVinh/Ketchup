/******************************************************************************
 * Project Name:    Ketchup - A movie management system
 * Course:          COMP1020 - OOP and Data Structure
 * Semester:        Spring 2026
 * <p>
 * Members: Tran Phan Anh <25anh.tp@vinuni.edu.vn>,
 *          Nguyen The Khoi Nguyen <25nguyen.ntk@vinuni.edu.vn>,
 *          Nguyen Dinh Quy <25quy.nd@vinuni.edu.vn>,
 *          Hoang Duc Phat <25phat.hd@vinuni.edu.vn>,
 *          Mai Dinh Vinh <25vinh.md@vinuni.edu.vn>
 * <p>
 * File Name:       Credential.java
 * Developer:       Tran Phan Anh*, Nguyen The Khoi Nguyen*, Nguyen Dinh Quy*,
 *                  Mai Dinh Vinh* (* equal contributions)
 * Description:     Model class representing a registered user's account
 *                  information, including login credentials, display name,
 *                  admin status, and email format validation logic.
 ******************************************************************************/

package com.ducksabervn.projects.ketchup.backend.model;

import java.util.regex.Pattern;

/**
 * Represents a registered user account in the Ketchup application.
 * Stores the user's display name, email address, password, and admin
 * status. Also provides a static utility method for validating email
 * format using a pre-compiled regular expression.
 */
public class Credential {

    /**
     * The display name of the user, shown in the UI after login.
     */
    private String username;

    /**
     * The email address of the user, used as the unique account identifier
     * and primary key in {@link com.ducksabervn.projects.ketchup.backend.repositories.CredentialRepository}.
     */
    private String email;

    /**
     * The password associated with this account, stored in plain text
     * and used for authentication at login.
     */
    private String password;

    /**
     * Indicates whether this account has administrator privileges.
     * Admin users have access to movie management features unavailable
     * to regular users.
     */
    private boolean isAdmin;

    /**
     * The regular expression pattern used to validate email addresses.
     * Accepts standard email formats including subdomains and common
     * special characters in the local part.
     */
    private static final String EMAIL_REGEX_PATTERN =
            "^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^.-]+@([a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,}$";

    /**
     * Pre-compiled {@link Pattern} derived from {@link #EMAIL_REGEX_PATTERN},
     * reused across all email validation calls to avoid recompilation overhead.
     */
    private static final Pattern EMAIL_PATTERN = Pattern.compile(EMAIL_REGEX_PATTERN);

    /**
     * Constructs a new {@code Credential} with all required account fields.
     *
     * @param username the display name of the user
     * @param email    the unique email address used to identify the account
     * @param password the account password used for authentication
     * @param isAdmin  {@code true} if the user has administrator privileges,
     *                 {@code false} for a regular user
     */
    public Credential(String username, String email, String password, boolean isAdmin) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.isAdmin = isAdmin;
    }

    /**
     * Returns the display name of the user.
     *
     * @return the username string
     */
    public String getUsername() {
        return username;
    }

    /**
     * Returns whether this account has administrator privileges.
     *
     * @return {@code true} if the user is an admin, {@code false} otherwise
     */
    public boolean isAdmin() {
        return isAdmin;
    }

    /**
     * Returns the email address associated with this account.
     *
     * @return the user's email address
     */
    public String getEmail() {
        return email;
    }

    /**
     * Returns the password associated with this account.
     *
     * @return the account password in plain text
     */
    public String getPassword() {
        return password;
    }

    /**
     * Validates whether the given string conforms to a standard email format
     * using a pre-compiled regular expression.
     *
     * @param email the email string to validate
     * @return {@code true} if the email matches the expected format,
     *         {@code false} otherwise
     */
    public static boolean isValidEmail(String email) {
        return EMAIL_PATTERN.matcher(email).matches();
    }
}