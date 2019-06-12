package com.tmg.parser;

import java.io.*;
import java.net.URL;
import java.nio.file.*;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class DirectoryParser {
    private static final String CATALOG_NAME = "kitchenAidProductCatalog";
    private static final String SOURCE_DIRECTORY = "source.directory";
    private static final String TARGET_DIRECTORY = "target.directory";

    public static void main(String[] args) {
        Properties properties = new Properties();
        URL url = DirectoryParser.class.getClassLoader().getResource("csv.properties");
        if (url != null) {
            try (InputStream is = url.openStream()) {
                properties.load(is);
            } catch (IOException ex) {
                throw new UncheckedIOException("Failed to load resource", ex);
            }
            parseDirectory(properties.getProperty(SOURCE_DIRECTORY), properties.getProperty(TARGET_DIRECTORY));
        }
    }

    private static void parseDirectory(final String sourceDirectory, final String targetDirectory) {
        try {
            FileParser fileParser = new FileParser(targetDirectory, CATALOG_NAME);
            List<Path> paths = Files.walk(Paths.get(sourceDirectory)).filter(Files::isRegularFile).collect(Collectors.toList());

            List<Path> withoutFilteringPath = paths.stream().filter(e -> e.getFileName().toString().contains(FileNameConstants.COLOR_ENUM) || e.getFileName().toString().contains(FileNameConstants.PRODUCT_DISCLAIMER)).collect(Collectors.toList());
            for (Path path : withoutFilteringPath) {
                Files.copy(path, Paths.get(targetDirectory + path.getFileName()));
            }

            Optional<Path> productMasterPath = paths.stream().filter(e -> e.getFileName().toString().contains(FileNameConstants.PRODUCT_MASTER)).findFirst();
            if (productMasterPath.isPresent()) {
                fileParser.parseFile(productMasterPath.get());

                Optional<Path> categoryProductPath = paths.stream().filter(e -> e.getFileName().toString().contains(FileNameConstants.CATEGORY_PRODUCT)).findFirst();
                if (categoryProductPath.isPresent()) {
                    fileParser.parseFile(categoryProductPath.get());

                    List<Path> categoriesPath = paths.stream().filter(e -> e.getFileName().toString().contains(FileNameConstants.CATEGORIES)).collect(Collectors.toList());
                    for (Path path : categoriesPath) {
                        fileParser.parseFile(path);
                    }

                    Optional<Path> productClassificationsPath = paths.stream().filter(e -> e.getFileName().toString().contains(FileNameConstants.PRODUCT_CLASSIFICATIONS)).findFirst();
                    if (productClassificationsPath.isPresent()) {
                        fileParser.parseFile(productClassificationsPath.get());

                        List<Path> classAttributeAssignmentPath = paths.stream().filter(e -> e.getFileName().toString().contains(FileNameConstants.CLASS_ATTRIBUTE_ASSIGNMENT)).collect(Collectors.toList());
                        for (Path path : classAttributeAssignmentPath) {
                            fileParser.parseFile(path);
                        }
                    }
                    paths.stream().filter(e -> e.getFileName().toString().contains(FileNameConstants.PRODUCT_REFERENCE)).findFirst().ifPresent(fileParser::parseFile);

                    List<Path> productSecondaryPath = paths.stream().filter(e -> e.getFileName().toString().contains(FileNameConstants.PRODUCT_SECONDARY)).collect(Collectors.toList());
                    for (Path path : productSecondaryPath) {
                        fileParser.parseFile(path);
                    }

                    List<Path> productFeaturePath = paths.stream().filter(e -> e.getFileName().toString().contains(FileNameConstants.PRODUCT_FEATURE)).collect(Collectors.toList());
                    for (Path path : productFeaturePath) {
                        fileParser.parseFile(path);
                    }

                    final List<Path> productPriceFilesPath = paths.stream().filter(e -> e.getFileName().toString().contains(FileNameConstants.PRODUCT_PRICE)).collect(Collectors.toList());
                    for (Path path : productPriceFilesPath) {
                        fileParser.parseFile(path);
                    }

                    final List<Path> productSalesFilesPath = paths.stream().filter(e -> e.getFileName().toString().contains(FileNameConstants.PRODUCT_SALES)).collect(Collectors.toList());
                    for (Path path : productSalesFilesPath) {
                        fileParser.parseFile(path);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}