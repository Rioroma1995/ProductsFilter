package com.tmg.parser;

import org.apache.commons.csv.*;

import java.io.*;
import java.nio.file.*;
import java.util.HashSet;
import java.util.Set;

import static com.tmg.parser.FileNameConstants.*;

public class FileParser {
    private final String targetDirectory;
    private final String catalogName;
    private Set<String> productVersions;
    private Set<String> originalProducts;
    private Set<String> categories;
    private Set<String> classifications;

    FileParser(final String targetDirectory, String catalogName) {
        productVersions = new HashSet<>();
        originalProducts = new HashSet<>();
        categories = new HashSet<>();
        classifications = new HashSet<>();
        this.catalogName = catalogName;
        this.targetDirectory = targetDirectory;
    }

    public void parseFile(Path path) {
        try (Reader reader = Files.newBufferedReader(path);
             CSVParser csvParser = new CSVParser(reader, CSVFormat.EXCEL);
             BufferedWriter writer = Files.newBufferedWriter(Paths.get(targetDirectory + path.getFileName()));
             CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.EXCEL)) {
            chooseParser(path.getFileName().toString(), csvParser, csvPrinter);
            csvPrinter.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void chooseParser(String fileName, CSVParser csvParser, CSVPrinter csvPrinter) throws IOException {
        if (fileName.contains(PRODUCT_MASTER)) {
            parseProductMaster(csvParser, csvPrinter);
        } else if (fileName.contains(CATEGORY_PRODUCT)) {
            parseCategoryProduct(csvParser, csvPrinter);
        } else if (fileName.contains(CATEGORIES)) {
            parseCategories(csvParser, csvPrinter);
        } else if (fileName.contains(PRODUCT_CLASSIFICATIONS)) {
            parseProductClassifications(csvParser, csvPrinter);
        } else if (fileName.contains(CLASS_ATTRIBUTE_ASSIGNMENT)) {
            parseClassAttributeAssignment(csvParser, csvPrinter);
        } else if (fileName.contains(PRODUCT_FEATURE)) {
            parseProductFeature(csvParser, csvPrinter);
        } else if (fileName.contains(PRODUCT_REFERENCE)) {
            parseProductReference(csvParser, csvPrinter);
        } else if (fileName.contains(PRODUCT_SECONDARY)) {
            parseProductSecondary(csvParser, csvPrinter);
        }
    }

    private void parseProductMaster(CSVParser csvParser, CSVPrinter csvPrinter) throws IOException {
        final Integer VERSION_PRODUCT_ATTR = 0;
        final Integer ORIGINAL_PRODUCT_ATTR = 1;
        final Integer CATALOG_ATTR = 12;
        for (CSVRecord csvRecord : csvParser) {
            if (csvRecord.get(CATALOG_ATTR).contains(catalogName)) {
                productVersions.add(csvRecord.get(VERSION_PRODUCT_ATTR));
                originalProducts.add(csvRecord.get(ORIGINAL_PRODUCT_ATTR));
                csvPrinter.printRecord(csvRecord);
            }
        }
    }

    private void parseCategoryProduct(CSVParser csvParser, CSVPrinter csvPrinter) throws IOException {
        final Integer CATEGORY_ATTR = 1;
        final Integer CATALOG_ATTR = 2;
        for (CSVRecord csvRecord : csvParser) {
            if (csvRecord.get(CATALOG_ATTR).contains(catalogName)) {
                categories.add(csvRecord.get(CATEGORY_ATTR));
                csvPrinter.printRecord(csvRecord);
            }
        }
    }

    private void parseCategories(CSVParser csvParser, CSVPrinter csvPrinter) throws IOException {
        final Integer CATEGORY_ATTR = 0;
        final Integer CATALOG_ATTR = 3;
        for (CSVRecord csvRecord : csvParser) {
            if (csvRecord.get(CATALOG_ATTR).contains(catalogName) && categories.contains(csvRecord.get(CATEGORY_ATTR))) {
                csvPrinter.printRecord(csvRecord);
            }
        }
    }

    private void parseProductClassifications(CSVParser csvParser, CSVPrinter csvPrinter) throws IOException {
        final Integer PRODUCT_ATTR = 0;
        final Integer CLASSIFICATION_ATTR = 2;
        for (CSVRecord csvRecord : csvParser) {
            if (productVersions.contains(csvRecord.get(PRODUCT_ATTR)) || originalProducts.contains(csvRecord.get(PRODUCT_ATTR))) {
                classifications.add(csvRecord.get(CLASSIFICATION_ATTR));
                csvPrinter.printRecord(csvRecord);
            }
        }
    }

    private void parseClassAttributeAssignment(CSVParser csvParser, CSVPrinter csvPrinter) throws IOException {
        final Integer CLASSIFICATION_ATTR = 3;
        for (CSVRecord csvRecord : csvParser) {
            if (classifications.contains(csvRecord.get(CLASSIFICATION_ATTR))) {
                csvPrinter.printRecord(csvRecord);
            }
        }
    }

    private void parseProductFeature(CSVParser csvParser, CSVPrinter csvPrinter) throws IOException {
        final Integer PRODUCT_ATTR = 0;
        for (CSVRecord csvRecord : csvParser) {
            if (productVersions.contains(csvRecord.get(PRODUCT_ATTR)) || originalProducts.contains(csvRecord.get(PRODUCT_ATTR))) {
                csvPrinter.printRecord(csvRecord);
            }
        }
    }

    private void parseProductReference(CSVParser csvParser, CSVPrinter csvPrinter) throws IOException {
        final Integer PRODUCT_ATTR = 0;
        final Integer PRODUCT_REFERENCE_ATTR = 2;
        for (CSVRecord csvRecord : csvParser) {
            if (productVersions.contains(csvRecord.get(PRODUCT_ATTR)) || productVersions.contains(csvRecord.get(PRODUCT_REFERENCE_ATTR))
                    || originalProducts.contains(csvRecord.get(PRODUCT_ATTR)) || originalProducts.contains(csvRecord.get(PRODUCT_REFERENCE_ATTR))) {
                csvPrinter.printRecord(csvRecord);
            }
        }
    }

    private void parseProductSecondary(final CSVParser csvParser, final CSVPrinter csvPrinter) throws IOException {
        final Integer PRODUCT_VERSION_ATTR = 0;
        final Integer PRODUCT_ATTR = 1;
        for (CSVRecord csvRecord : csvParser) {
            if (productVersions.contains(csvRecord.get(PRODUCT_VERSION_ATTR)) || originalProducts.contains(csvRecord.get(PRODUCT_ATTR))) {
                csvPrinter.printRecord(csvRecord);
            }
        }
    }
}