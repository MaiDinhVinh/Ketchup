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
 * File Name:       CredentialCsvIO.java
 * Developer:       Tran Phan Anh*, Nguyen The Khoi Nguyen*, Nguyen Dinh Quy*,
 *                  Mai Dinh Vinh* (* equal contributions)
 * Description:     Handles all CSV read and write operations for user credential
 *                  data, including loading all registered accounts on startup and
 *                  persisting any newly registered users on logout or exit.
 ******************************************************************************/

package com.ducksabervn.projects.ketchup.backend.io;

import com.ducksabervn.projects.ketchup.backend.model.Credential;
import com.ducksabervn.projects.ketchup.backend.repositories.CredentialRepository;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * Singleton class responsible for reading and writing user credential data
 * to {@code USER_CREDENTIALS.csv}. Implements {@link CsvIO} to support
 * full file reads on startup and full file overwrites on save.
 */
public class CredentialCsvIO implements CsvIO<String, Credential> {

    /**
     * The sole instance of {@code CredentialCsvIO}, lazily initialized.
     */
    private static CredentialCsvIO IO;

    /**
     * Private constructor to enforce the singleton pattern.
     */
    private CredentialCsvIO() {
    }

    /**
     * Returns the singleton instance of {@code CredentialCsvIO},
     * creating it on first call.
     *
     * @return the singleton {@code CredentialCsvIO} instance
     */
    public static CredentialCsvIO getIO() {
        if (CredentialCsvIO.IO == null) {
            CredentialCsvIO.IO = new CredentialCsvIO();
        }
        return CredentialCsvIO.IO;
    }

    /**
     * Reads all user credential records from {@code USER_CREDENTIALS.csv}
     * and returns them as a map keyed by email address.
     *
     * @return a {@link LinkedHashMap} mapping email → {@link Credential}
     *         for every registered user
     * @throws IOException if the CSV file cannot be read
     */
    @Override
    public LinkedHashMap<String, Credential> readCsvFile() throws IOException {
        return this.readUserCredentialsCsv();
    }

    /**
     * Overwrites {@code USER_CREDENTIALS.csv} with the current state of all
     * credentials held in {@link CredentialRepository}, preserving insertion order.
     *
     * @throws IOException if the CSV file cannot be written to
     */
    @Override
    public void updateLatestData() throws IOException {
        this.updateCredCsv();
    }

    /**
     * Parses {@code USER_CREDENTIALS.csv} line by line, skipping the header row,
     * and constructs a {@link Credential} object for each record.
     *
     * @return a {@link LinkedHashMap} of email → {@link Credential}
     *         preserving the order in which users appear in the file
     * @throws IOException if the CSV file cannot be read
     */
    private LinkedHashMap<String, Credential> readUserCredentialsCsv() throws IOException {
        List<String> allCreds = Files.readAllLines(AppPath.USER_CREDENTIALS.getAppPath());
        allCreds.remove(0);
        LinkedHashMap<String, Credential> credMap = new LinkedHashMap<>();
        for (String str : allCreds) {
            String[] split = str.split(";");
            credMap.put(split[1], new Credential(split[0], split[1], split[2], Boolean.valueOf(split[3])));
        }
        return credMap;
    }

    /**
     * Rewrites {@code USER_CREDENTIALS.csv} from scratch using the current contents
     * of {@link CredentialRepository}. This ensures any accounts registered during
     * the session are persisted to disk.
     *
     * @throws IOException if the CSV file cannot be opened or written to
     */
    private void updateCredCsv() throws IOException {
        LinkedHashMap<String, Credential> updatedCredentials = CredentialRepository.getCredentials();
        try (BufferedWriter bw2 = new BufferedWriter(
                new FileWriter(AppPath.USER_CREDENTIALS.getAppPath().toFile()))) {
            bw2.write("USERNAME;EMAIL;PASSWORD;IS_ADMIN");
            for (Credential c : updatedCredentials.values()) {
                bw2.newLine();
                bw2.write(generateCredentialDataAsString(c));
            }
        } catch (IOException e) {
            throw e;
        }
    }

    /**
     * Serializes a {@link Credential} object into a semicolon-delimited CSV row string.
     * The format is: {@code USERNAME;EMAIL;PASSWORD;IS_ADMIN}
     *
     * @param c the {@link Credential} to serialize
     * @return a formatted CSV row representing the given credential
     */
    public static String generateCredentialDataAsString(Credential c) {
        return "%s;%s;%s;%b".formatted(
                c.getUsername(),
                c.getEmail(),
                c.getPassword(),
                c.isAdmin());
    }
}