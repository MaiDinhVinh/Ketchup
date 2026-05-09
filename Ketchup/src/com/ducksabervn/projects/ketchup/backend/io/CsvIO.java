package com.ducksabervn.projects.ketchup.backend.io;

import java.io.IOException;
import java.util.LinkedHashMap;

interface CsvIO<U, V> extends CsvPersistable{
    LinkedHashMap<U, V> readCsvFile() throws IOException;
}