package com.ducksabervn.projects.ketchup.backend.io;

import java.nio.file.Path;
import java.util.LinkedHashMap;

interface CsvIO<U, V> extends CsvPersistable{
    LinkedHashMap<U, V> readCsvFile();
}