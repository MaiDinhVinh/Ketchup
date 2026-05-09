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
 * File Name:       CsvIO.java
 * Developer:       Tran Phan Anh*, Nguyen The Khoi Nguyen*, Nguyen Dinh Quy*,
 *                  Mai Dinh Vinh* (* equal contributions)
 * Description:     Generic interface for CSV I/O operations that require
 *                  reading an entire file into a LinkedHashMap, extending
 *                  CsvPersistable to also enforce write-back capability.
 ******************************************************************************/

package com.ducksabervn.projects.ketchup.backend.io;

import java.io.IOException;
import java.util.LinkedHashMap;

/**
 * Generic interface for CSV data sources that can be fully loaded into memory.
 * Extends {@link CsvPersistable} to combine read and write responsibilities
 * into a single contract.
 *
 * @param <U> the type of the key used in the returned map
 * @param <V> the type of the value (model object) stored in the returned map
 */
interface CsvIO<U, V> extends CsvPersistable {

    /**
     * Reads all records from the associated CSV file and returns them
     * as a {@link LinkedHashMap}, preserving the order in which they
     * appear in the file.
     *
     * @return a {@link LinkedHashMap} mapping each record's key to its
     *         corresponding model object
     * @throws IOException if the CSV file cannot be found or read
     */
    LinkedHashMap<U, V> readCsvFile() throws IOException;
}