package com.tmg.parser;

import org.apache.commons.csv.*;

import java.io.*;
import java.nio.file.*;
import java.util.*;

import static com.tmg.parser.FileNameConstants.*;

public class FileParser {
    private final String targetDirectory;
    private final String catalogName;
    private Set<String> variantProducts;
    private Set<String> baseProducts;
    private Set<String> classifications;

    FileParser(final String targetDirectory, String catalogName) {
        variantProducts = new HashSet<>();
        baseProducts = new HashSet<>();
        classifications = new HashSet<>();
        this.catalogName = catalogName;
        this.targetDirectory = targetDirectory;
    }

    public void parseFile(Path path) {
        try (Reader reader = Files.newBufferedReader(path);
             CSVParser csvParser = new CSVParser(reader, CSVFormat.EXCEL.withQuote('"').withQuoteMode(QuoteMode.ALL));
             BufferedWriter writer = Files.newBufferedWriter(Paths.get(targetDirectory + path.getFileName()));
             CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.EXCEL.withQuote('"').withQuoteMode(QuoteMode.ALL))) {
            chooseParser(path.getFileName().toString(), csvParser, csvPrinter);
            csvPrinter.flush();
        } catch (Exception e) {
            System.out.println("File was: " + path);
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
        final Integer VARIANT_PRODUCT_ATTR = 0;
        final Integer BASE_PRODUCT_ATTR = 1;
        final Integer CATALOG_ATTR = 12;
        for (CSVRecord csvRecord : csvParser) {
            if (csvRecord.get(CATALOG_ATTR).contains(this.catalogName)) {
                variantProducts.add(csvRecord.get(VARIANT_PRODUCT_ATTR));
                baseProducts.add(csvRecord.get(BASE_PRODUCT_ATTR));

                final Collection<String> newRow = new ArrayList<>(csvRecord.size());
                Integer currentCol = 0;
                for(String columnValue: csvRecord) {
                    if (!Objects.equals(currentCol, CATALOG_ATTR)) {
                        newRow.add(columnValue);
                    } else {
                        newRow.add(this.catalogName);
                    }
                    ++currentCol;
                }
                csvPrinter.printRecord(newRow);
            }
        }
    }

    private void parseCategoryProduct(CSVParser csvParser, CSVPrinter csvPrinter) throws IOException {
        final Integer CATALOG_ATTR = 2;
        for (CSVRecord csvRecord : csvParser) {
            if (csvRecord.get(CATALOG_ATTR).contains(catalogName)) {
                csvPrinter.printRecord(csvRecord);
            }
        }
    }

    private void parseCategories(CSVParser csvParser, CSVPrinter csvPrinter) throws IOException {
        final Integer CATALOG_ATTR = 3;
        for (CSVRecord csvRecord : csvParser) {
            if (csvRecord.get(CATALOG_ATTR).contains(catalogName)) {
                csvPrinter.printRecord(csvRecord);
            }
        }
    }

    private void parseProductClassifications(CSVParser csvParser, CSVPrinter csvPrinter) throws IOException {
        final Integer PRODUCT_ATTR = 0;
        final Integer CLASSIFICATION_ATTR = 2;
        for (CSVRecord csvRecord : csvParser) {
            if (variantProducts.contains(csvRecord.get(PRODUCT_ATTR)) || baseProducts.contains(csvRecord.get(PRODUCT_ATTR))) {
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
            if (variantProducts.contains(csvRecord.get(PRODUCT_ATTR)) || baseProducts.contains(csvRecord.get(PRODUCT_ATTR))) {
                csvPrinter.printRecord(csvRecord);
            }
        }
    }

    private void parseProductReference(CSVParser csvParser, CSVPrinter csvPrinter) throws IOException {
        final Integer PRODUCT_ATTR = 0;
        final Integer PRODUCT_REFERENCE_ATTR = 2;
        for (CSVRecord csvRecord : csvParser) {
            if (variantProducts.contains(csvRecord.get(PRODUCT_ATTR)) || variantProducts.contains(csvRecord.get(PRODUCT_REFERENCE_ATTR))
                    || baseProducts.contains(csvRecord.get(PRODUCT_ATTR)) || baseProducts.contains(csvRecord.get(PRODUCT_REFERENCE_ATTR))) {
                csvPrinter.printRecord(csvRecord);
            }
        }
    }

    private void parseProductSecondary(final CSVParser csvParser, final CSVPrinter csvPrinter) throws IOException {
        // TO fix
        final Integer PRODUCT_VERSION_ATTR = 0;
        final Integer PRODUCT_ATTR = 1;
        for (CSVRecord csvRecord : csvParser) {
            if (variantProducts.contains(csvRecord.get(PRODUCT_VERSION_ATTR)) || baseProducts.contains(csvRecord.get(PRODUCT_ATTR))) {
                csvPrinter.printRecord(csvRecord);
            }
        }
    }

}