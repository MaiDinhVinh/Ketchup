package com.ducksabervn.projects.ketchup.backend.io;

import java.io.IOException;
import java.util.LinkedHashMap;

interface FilteredCsvIO<U, V> extends CsvPersistable{
    LinkedHashMap<U, V> readCsvFile(String requiredInformation) throws IOException;
}