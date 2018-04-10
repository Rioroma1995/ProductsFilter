package com.tsimura.parser;

import org.apache.commons.csv.*;

import java.io.*;
import java.nio.file.*;
import java.util.HashSet;
import java.util.Set;

public class CategoryProductParser {
    private static final Integer CATEGORY_ATTR = 1;
    private static final Integer CATALOG_ATTR = 2;
    private Set<String> categories;

    CategoryProductParser() {
        categories = new HashSet<>();
    }

    public void parseFile(Path path, final String targetDirectory, String catalogName) throws IOException {
        try (Reader reader = Files.newBufferedReader(path);
             CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT);
             BufferedWriter writer = Files.newBufferedWriter(Paths.get(targetDirectory + path.getFileName()));
             CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.EXCEL)
        ) {
            for (CSVRecord csvRecord : csvParser) {
                if (csvRecord.get(CATALOG_ATTR).contains(catalogName)) {
                    categories.add(csvRecord.get(CATEGORY_ATTR));
                    csvPrinter.printRecord(csvRecord);
                }
            }
            csvPrinter.flush();
        }
    }

    public Set<String> getCategories() {
        return categories;
    }

    public void setCategories(Set<String> categories) {
        this.categories = categories;
    }
}