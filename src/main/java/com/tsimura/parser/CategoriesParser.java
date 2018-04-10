package com.tsimura.parser;

import org.apache.commons.csv.*;

import java.io.*;
import java.nio.file.*;
import java.util.Set;

public class CategoriesParser {
    private static final Integer CATEGORY_ATTR = 0;
    private static final Integer CATALOG_ATTR = 3;

    public void parseFile(Path path, final String targetDirectory, String catalogName, Set<String> categories) throws IOException {
        try (Reader reader = Files.newBufferedReader(path);
             CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT);
             BufferedWriter writer = Files.newBufferedWriter(Paths.get(targetDirectory + path.getFileName()));
             CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.EXCEL)
        ) {
            for (CSVRecord csvRecord : csvParser) {
                if (csvRecord.get(CATALOG_ATTR).contains(catalogName) && categories.contains(csvRecord.get(CATEGORY_ATTR))) {
                    csvPrinter.printRecord(csvRecord);
                }
            }
            csvPrinter.flush();
        }
    }
}