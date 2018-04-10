package com.tmg.parser;

import org.apache.commons.csv.*;

import java.io.*;
import java.nio.file.*;
import java.util.Set;

public class ClassAttributeAssignmentParser {
    private static final Integer CLASSIFICATION_ATTR = 3;

    public void parseFile(Path path, final String targetDirectory, Set<String> classifications) throws IOException {
        try (Reader reader = Files.newBufferedReader(path);
             CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT);
             BufferedWriter writer = Files.newBufferedWriter(Paths.get(targetDirectory + path.getFileName()));
             CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.EXCEL)
        ) {
            for (CSVRecord csvRecord : csvParser) {
                if (classifications.contains(csvRecord.get(CLASSIFICATION_ATTR))) {
                    csvPrinter.printRecord(csvRecord);
                }
            }
            csvPrinter.flush();
        }
    }
}