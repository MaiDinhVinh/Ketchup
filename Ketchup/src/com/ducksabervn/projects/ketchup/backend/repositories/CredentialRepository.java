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
 * File Name:       CredentialRepository.java
 * Developer:       Tran Phan Anh*, Nguyen The Khoi Nguyen*, Nguyen Dinh Quy*,
 *                  Mai Dinh Vinh* (* equal contributions)
 * Description:     In-memory repository for all registered user credentials,
 *                  providing methods to verify login credentials, retrieve
 *                  user accounts, and register new users during a session.
 ******************************************************************************/

package com.ducksabervn.projects.ketchup.backend.repositories;

import com.ducksabervn.projects.ketchup.backend.model.Credential;

import java.util.LinkedHashMap;

/**
 * Static in-memory repository that holds all registered {@link Credential}
 * records loaded from {@code USER_CREDENTIALS.csv} at application startup.
 * Uses a {@link LinkedHashMap} keyed by email address to preserve insertion
 * order and support constant-time credential lookups. Any accounts registered
 * during the session are persisted to disk on logout or exit via
 * {@link com.ducksabervn.projects.ketchup.backend.io.CredentialCsvIO}.
 */
public class CredentialRepository {

    /**
     * The in-memory store of all user credentials, keyed by email address.
     * A {@link LinkedHashMap} is used to maintain the order in which
     * accounts were registered.
     */
    private static LinkedHashMap<String, Credential> credentials;

    /**
     * Returns the entire in-memory credential map.
     *
     * @return a {@link LinkedHashMap} mapping email → {@link Credential}
     *         for all registered users
     */
    public static LinkedHashMap<String, Credential> getCredentials() {
        return CredentialRepository.credentials;
    }

    /**
     * Replaces the current in-memory credential store with the given map.
     * Called on application startup after reading all accounts from
     * {@code USER_CREDENTIALS.csv}.
     *
     * @param credentials the {@link LinkedHashMap} of email → {@link Credential}
     *                    to set as the active credential store
     */
    public static void setCredentials(LinkedHashMap<String, Credential> credentials) {
        CredentialRepository.credentials = credentials;
    }

    /**
     * Verifies whether the supplied email and password match an existing
     * account in the repository. Both the email's existence and the
     * password's equality are checked.
     *
     * @param email    the email address to look up
     * @param password the password to verify against the stored value
     * @return {@code true} if the email exists and the password matches,
     *         {@code false} otherwise
     */
    public static boolean verifyCredential(String email, String password) {
        return CredentialRepository.credentials.containsKey(email) &&
                CredentialRepository.credentials.get(email).getPassword().equals(password);
    }

    /**
     * Retrieves the {@link Credential} associated with the given email address.
     *
     * @param email the email address of the account to retrieve
     * @return the {@link Credential} for the given email, or {@code null}
     *         if no account with that email exists
     */
    public static Credential getUser(String email) {
        return CredentialRepository.credentials.get(email);
    }

    /**
     * Registers a new user account by creating a {@link Credential} and
     * adding it to the in-memory store. Registration fails if an account
     * with the same email address already exists.
     *
     * @param username the display name for the new account
     * @param email    the unique email address for the new account
     * @param password the password for the new account
     * @param isAdmin  {@code true} to grant administrator privileges,
     *                 {@code false} for a regular user account
     * @return {@code true} if the account was successfully registered,
     *         {@code false} if an account with the given email already exists
     */
    public static boolean register(String username, String email, String password, boolean isAdmin) {
        if (CredentialRepository.credentials.containsKey(email)) {
            return false;
        } else {
            CredentialRepository.credentials.put(email, new Credential(username, email, password, isAdmin));
            return true;
        }
    }
}