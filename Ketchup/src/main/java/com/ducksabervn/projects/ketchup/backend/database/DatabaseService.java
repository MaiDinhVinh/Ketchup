/******************************************************************************
 * Project Name:    Ketchup — Movie Management System
 * Course:          COMP1020 — OOP and Data Structure
 * Semester:        Spring 2026
 *
 * Members: Tran Phan Anh <25anh.tp@vinuni.edu.vn>,
 *          Nguyen Trong Khoi Nguyen <25nguyen.ntk@vinuni.edu.vn>,
 *          Nguyen Dinh Quy <25quy.nd@vinuni.edu.vn>,
 *          Hoang Duc Phat <25phat.hd@vinuni.edu.vn>,
 *          Mai Dinh Vinh <25vinh.md@vinuni.edu.vn>
 *
 * File Name:       DatabaseService.java
 * Developer:       Tran Phan Anh*, Nguyen Trong Khoi Nguyen*, Nguyen Dinh Quy*,
 *                  Mai Dinh Vinh* (* equal contributions)
 * Description:     Singleton service that manages the full lifecycle of the
 *                  embedded MariaDB database — starting and stopping the server
 *                  process, running initialization on first launch, and
 *                  providing JDBC connections to the rest of the application.
 *                  Replaces the original MySQLService, which required an
 *                  externally installed MySQL instance.
 ******************************************************************************/

package com.ducksabervn.projects.ketchup.backend.database;

import ch.vorburger.mariadb4j.DB;
import ch.vorburger.mariadb4j.DBConfigurationBuilder;
import com.ducksabervn.projects.ketchup.frontend.util.DisplayMessage;

import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Singleton service responsible for two concerns:
 *
 * <ol>
 *   <li><b>Lifecycle</b> — starts and stops the embedded MariaDB server via
 *       MariaDB4j. The server process is kept alive for the entire duration
 *       of the application session.</li>
 *   <li><b>Connectivity</b> — exposes {@link #getConnection()} so that any
 *       repository or DAO in the application can obtain a JDBC connection
 *       without knowing where credentials or the URL are defined.</li>
 * </ol>
 *
 * <p>Call {@link #startup()} once inside {@code KetchupMain.init()}, and
 * {@link #shutdown()} once inside {@code KetchupMain.stop()}. Every other
 * class only ever calls {@link #getConnection()}.</p>
 *
 * <p>On the very first launch the service detects the absence of a flag file
 * ({@code ~/Ketchup/.db_initialized}) and automatically delegates to
 * {@link SchemaInitializer} (DDL) then {@link DatasetSeeder} (seed data),
 * mirroring the pattern used by {@code FileSystemInitializer} for CSV
 * bootstrapping.</p>
 */
public final class DatabaseService {

    private static final String DB_NAME = "ketchup";
    private static final int    DB_PORT = 3306;
    private static final String DB_USER = "root";
    private static final String DB_PASS = "";

    /** JDBC URL pointing at the embedded MariaDB instance. */
    private static final String JDBC_URL =
            "jdbc:mariadb://localhost:" + DB_PORT + "/" + DB_NAME;

    /**
     * Folder where MariaDB4j stores its data files between sessions.
     * Placed inside ~/Ketchup so it sits alongside the rest of the
     * application data, consistent with {@code AppPath}.
     */
    private static final Path DATA_DIR =
            Path.of(System.getProperty("user.home"), "Ketchup", "db_data");

    /**
     * Flag file whose existence indicates that {@link SchemaInitializer} and
     * {@link DatasetSeeder} have already been executed on this machine.
     * Modelled after the file-existence checks in {@code FileSystemInitializer}.
     */
    private static final Path INIT_FLAG =
            Path.of(System.getProperty("user.home"), "Ketchup", ".db_initialized");

    /** The running MariaDB4j server process. Non-null only after startup(). */
    private static DB db;

    /**
     * Starts the embedded MariaDB server and, on first launch, runs the DDL
     * and seed-data scripts via {@link SchemaInitializer} and
     * {@link DatasetSeeder}.
     *
     * <p>Must be called from {@code KetchupMain.init()} — i.e. before the
     * JavaFX Application Thread starts — so that the database is ready before
     * any UI controller requests a connection.</p>
     *
     * @throws Exception if the server cannot start, the database cannot be
     *                   created, or either initialization step fails
     */
    public static void startup(boolean seedData) throws Exception {
        // Ensure ~/Ketchup/ exists before MariaDB4j tries to write into it
        Files.createDirectories(DATA_DIR);

        DBConfigurationBuilder config = DBConfigurationBuilder.newBuilder();
        config.setPort(DB_PORT);
        config.setDataDir(DATA_DIR.toFile());
        config.setDeletingTemporaryBaseAndDataDirsOnShutdown(false);

        db = DB.newEmbeddedDB(config.build());
        db.start();

        // createDB is a no-op if the schema already exists
        db.createDB(DB_NAME);

        // First-launch bootstrap: DDL → seed data → mark as done
        if (!Files.exists(INIT_FLAG)) {
            try (Connection conn = getConnection()) {
                SchemaInitializer.initialize(conn);
                if (seedData) {
                    DatasetSeeder.seed(conn);
                }
            }
            Files.createFile(INIT_FLAG);
        }
    }

    /**
     * Stops the embedded MariaDB server cleanly.
     *
     * <p>Must be called from {@code KetchupMain.stop()} so that all pending
     * writes are flushed to disk before the JVM exits.</p>
     *
     * @throws Exception if the server process cannot be stopped gracefully
     */
    public static void shutdown() throws Exception {
        if (db != null) {
            db.stop();
        }
    }

    // -------------------------------------------------------------------------
    // Connectivity
    // -------------------------------------------------------------------------

    /**
     * Opens and returns a new JDBC {@link Connection} to the embedded
     * {@code ketchup} database.
     *
     * <p>Callers are responsible for closing the connection (preferably via
     * try-with-resources) after use to avoid connection leaks.</p>
     *
     * @return a new, open {@link Connection}
     * @throws SQLException if a database access error occurs or the embedded
     *                      server has not been started yet
     */
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(JDBC_URL, DB_USER, DB_PASS);
    }

    public static boolean isInitialized(){
        return Files.exists(INIT_FLAG);
    }
}