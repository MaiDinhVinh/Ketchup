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
 * File Name:       FilteredCsvIO.java
 * Developer:       Tran Phan Anh*, Nguyen The Khoi Nguyen*, Nguyen Dinh Quy*,
 *                  Mai Dinh Vinh* (* equal contributions)
 * Description:     Generic interface for CSV I/O operations that require
 *                  a filter parameter at read time, extending CsvPersistable
 *                  to also enforce write-back capability.
 ******************************************************************************/

package com.ducksabervn.projects.ketchup.backend.io;

import java.io.IOException;
import java.util.LinkedHashMap;

/**
 * Generic interface for CSV data sources that support filtered reads,
 * where only a subset of records matching a given criterion is loaded
 * into memory. Extends {@link CsvPersistable} to combine filtered read
 * and write responsibilities into a single contract.
 * <p>
 * Unlike {@link CsvIO}, which reads the entire file unconditionally,
 * this interface is intended for cases where loading all records is
 * unnecessary or would violate data privacy (e.g. loading only the
 * bookings that belong to the currently logged-in user).
 *
 * @param <U> the type of the key used in the returned map
 * @param <V> the type of the value (model object) stored in the returned map
 */
interface FilteredCsvIO<U, V> extends CsvPersistable {

    /**
     * Reads records from the associated CSV file that satisfy the given
     * filter criterion and returns them as a {@link LinkedHashMap},
     * preserving the order in which they appear in the file.
     *
     * @param requiredInformation the filter value used to select matching
     *                            records (e.g. a user's email address)
     * @return a {@link LinkedHashMap} mapping each matching record's key
     *         to its corresponding model object
     * @throws IOException if the CSV file cannot be found or read
     */
    LinkedHashMap<U, V> readCsvFile(String requiredInformation) throws IOException;
}