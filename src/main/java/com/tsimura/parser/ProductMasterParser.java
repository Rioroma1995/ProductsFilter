package com.tsimura.parser;

import org.apache.commons.csv.*;

import java.io.*;
import java.nio.file.*;
import java.util.HashSet;
import java.util.Set;

public class ProductMasterParser {
    private static final Integer VERSION_PRODUCT_ATTR = 0;
    private static final Integer ORIGINAL_PRODUCT_ATTR = 1;
    private static final Integer CATALOG_ATTR = 12;
    private Set<String> productVersions;
    private Set<String> originalProducts;

    ProductMasterParser() {
        productVersions = new HashSet<>();
        originalProducts = new HashSet<>();
    }

    public void parseFile(Path path, final String targetDirectory, String catalogName) throws IOException {
        try (Reader reader = Files.newBufferedReader(path);
             CSVParser csvParser = new CSVParser(reader, CSVFormat.EXCEL);
             BufferedWriter writer = Files.newBufferedWriter(Paths.get(targetDirectory + path.getFileName()));
             CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.EXCEL)
        ) {
            for (CSVRecord csvRecord : csvParser) {
                if (csvRecord.get(CATALOG_ATTR).contains(catalogName)) {
                    productVersions.add(csvRecord.get(VERSION_PRODUCT_ATTR));
                    originalProducts.add(csvRecord.get(ORIGINAL_PRODUCT_ATTR));
                    csvPrinter.printRecord(csvRecord);
                }
            }
            csvPrinter.flush();
        }
        System.out.println(productVersions.size());
        System.out.println(originalProducts.size());
    }

    public Set<String> getProductVersions() {
        return productVersions;
    }

    public void setProductVersions(Set<String> productVersions) {
        this.productVersions = productVersions;
    }

    public Set<String> getOriginalProducts() {
        return originalProducts;
    }

    public void setOriginalProducts(Set<String> originalProducts) {
        this.originalProducts = originalProducts;
    }
}