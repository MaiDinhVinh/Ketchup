package com.ducksabervn.projects.ketchup.backend.io;

import java.io.IOException;

interface CsvPersistable {
    void updateLatestData() throws IOException;
}