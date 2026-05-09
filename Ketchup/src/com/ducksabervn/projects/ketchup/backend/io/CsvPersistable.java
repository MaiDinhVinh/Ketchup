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
 * File Name:       CsvPersistable.java
 * Developer:       Tran Phan Anh*, Nguyen The Khoi Nguyen*, Nguyen Dinh Quy*,
 *                  Mai Dinh Vinh* (* equal contributions)
 * Description:     Base interface that enforces a write-back contract on all
 *                  CSV I/O classes, ensuring in-memory data can be persisted
 *                  back to disk.
 ******************************************************************************/

package com.ducksabervn.projects.ketchup.backend.io;

import java.io.IOException;

/**
 * Base interface for all CSV I/O classes in the Ketchup application.
 * Any class that writes application data back to a CSV file must implement
 * this interface, guaranteeing a consistent save mechanism across all
 * data sources.
 */
interface CsvPersistable {

    /**
     * Persists the current in-memory state of the associated data store
     * back to its corresponding CSV file on disk, overwriting or appending
     * as appropriate for each implementation.
     *
     * @throws IOException if the CSV file cannot be opened or written to
     */
    void updateLatestData() throws IOException;
}