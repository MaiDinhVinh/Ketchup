/*******************************************************************************
 * Project Name:    Ketchup - A movie management system
 * Course:          COMP1020 - OOP and Data Structure
 * Semester:        Spring 2026
 * Members: Tran Phan Anh <25anh.tp@vinuni.edu.vn>,
 *          Nguyen Trong Khoi Nguyen <25nguyen.ntk@vinuni.edu.vn>,
 *          Nguyen Dinh Quy <25quy.nd@vinuni.edu.vn>,
 *          Hoang Duc Phat <25phat.hd@vinuni.edu.vn>,
 *          Mai Dinh Vinh <25vinh.md@vinuni.edu.vn>
 * <p>
 * Developer:       Tran Phan Anh*, Nguyen Trong Khoi Nguyen*, Nguyen Dinh Quy*,
 *                  Hoang Duc Phat*, Mai Dinh Vinh* (* equal contributions)
 * File Name:       CredentialRepository.java
 * Description:     In-memory repository for registered user credentials,
 *                  backed by the MySQL `users` table. Replaces the previous
 *                  CSV-based implementation; all reads and writes now go
 *                  directly to the database via JDBC.
 *******************************************************************************/

package com.ducksabervn.projects.ketchup.backend.repositories;

import com.ducksabervn.projects.ketchup.backend.database.DatabaseService;
import com.ducksabervn.projects.ketchup.backend.model.Credential;

import java.sql.*;
import java.util.LinkedHashMap;

/**
 * Static in-memory repository that holds all registered {@link Credential}
 * records. The in-memory map is loaded once at startup via
 * {@link #loadCredentials()} and kept in sync with the {@code users} table
 * for every write operation ({@link #register}).
 */
public class CredentialRepository {

    /**
     * In-memory credential store, keyed by email address.
     * Loaded once at startup; updated on every {@link #register} call.
     */
    private static LinkedHashMap<String, Credential> credentials = new LinkedHashMap<>();

    // -------------------------------------------------------------------------
    // Bootstrap
    // -------------------------------------------------------------------------

    /**
     * Loads all rows from the {@code users} table into the in-memory map.
     * Must be called once at application startup, replacing the previous
     * {@code CredentialCsvIO.getIO().readCsvFile()} call in {@code KetchupMain}.
     *
     * @throws SQLException if the database cannot be queried
     */
    public static void loadCredentials() throws SQLException {
        LinkedHashMap<String, Credential> result = new LinkedHashMap<>();
        String sql = "SELECT email, username, password, is_admin FROM users";

        try (PreparedStatement ps = DatabaseService.getConnection().prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Credential c = new Credential(
                        rs.getString("username"),
                        rs.getString("email"),
                        rs.getString("password"),
                        rs.getBoolean("is_admin")
                );
                result.put(c.getEmail(), c);
            }
        }
        credentials = result;
    }

    // -------------------------------------------------------------------------
    // Getters (unchanged public API)
    // -------------------------------------------------------------------------

    /**
     * Returns the entire in-memory credential map.
     *
     * @return a {@link LinkedHashMap} mapping email → {@link Credential}
     */
    public static LinkedHashMap<String, Credential> getCredentials() {
        return credentials;
    }

    /**
     * Retrieves the {@link Credential} for the given email address.
     *
     * @param email the email address to look up
     * @return the matching {@link Credential}, or {@code null} if not found
     */
    public static Credential getUser(String email) {
        return credentials.get(email);
    }

    // -------------------------------------------------------------------------
    // Authentication (unchanged public API)
    // -------------------------------------------------------------------------

    /**
     * Verifies whether the supplied email and password match a stored account.
     *
     * @param email    the email address to look up
     * @param password the password to verify
     * @return {@code true} if the email exists and the password matches
     */
    public static boolean verifyCredential(String email, String password) {
        return credentials.containsKey(email) &&
                credentials.get(email).getPassword().equals(password);
    }

    // -------------------------------------------------------------------------
    // Mutations
    // -------------------------------------------------------------------------

    /**
     * Registers a new user account. The account is inserted into the
     * {@code users} table and added to the in-memory map atomically.
     * Returns {@code false} (without touching the DB) if an account with
     * the given email already exists.
     *
     * @param username the display name for the new account
     * @param email    the unique email address for the new account
     * @param password the account password
     * @param isAdmin  {@code true} to grant administrator privileges
     * @return {@code true} if registration succeeded, {@code false} if the
     *         email is already taken
     * @throws SQLException if the INSERT fails for a database reason
     */
    public static boolean register(String username,
                                   String email,
                                   String password,
                                   boolean isAdmin) throws SQLException {
        if (credentials.containsKey(email)) {
            return false;
        }

        String sql = "INSERT INTO users (email, username, password, is_admin) VALUES (?, ?, ?, ?)";

        try (PreparedStatement ps = DatabaseService.getConnection().prepareStatement(sql)) {
            ps.setString(1, email);
            ps.setString(2, username);
            ps.setString(3, password);
            ps.setBoolean(4, isAdmin);
            ps.executeUpdate();
        }

        credentials.put(email, new Credential(username, email, password, isAdmin));
        return true;
    }
}