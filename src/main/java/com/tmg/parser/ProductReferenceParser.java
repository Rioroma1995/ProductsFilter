package com.tmg.parser;

import org.apache.commons.csv.*;

import java.io.*;
import java.nio.file.*;
import java.util.List;
import java.util.Set;

public class ProductReferenceParser {
    private static final Integer PRODUCT_ATTR = 0;
    private static final Integer PRODUCT_REFERENCE_ATTR = 2;

    public void parseFile(Path path, final String targetDirectory, Set<String> productVersions, Set<String> originalProducts) throws IOException {
        try (Reader reader = Files.newBufferedReader(path);
             CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT);
             BufferedWriter writer = Files.newBufferedWriter(Paths.get(targetDirectory + path.getFileName()));
             CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.EXCEL)
        ) {
            for (CSVRecord csvRecord : csvParser) {
                if (productVersions.contains(csvRecord.get(PRODUCT_ATTR)) || productVersions.contains(csvRecord.get(PRODUCT_REFERENCE_ATTR))
                        || originalProducts.contains(csvRecord.get(PRODUCT_ATTR)) || originalProducts.contains(csvRecord.get(PRODUCT_REFERENCE_ATTR))) {
                    csvPrinter.printRecord(csvRecord);
                }
            }
            csvPrinter.flush();
        }
    }
}
