package com.tsimura.parser;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Collectors;

public class DirectoryParser {
    private static final String CATALOG_NAME = "mcLaundryProductCatalog";
    private static final String SOURCE_DIRECTORY = "source.directory";
    private static final String TARGET_DIRECTORY = "target.directory";

    public static void main(String[] args) {
        Properties properties = new Properties();
        try (InputStream is = DirectoryParser.class.getClassLoader().getResource("csv.properties").openStream()) {
            properties.load(is);
        } catch (IOException ex) {
            throw new UncheckedIOException("Failed to load resource", ex);
        }
        parseDirectory(properties.getProperty(SOURCE_DIRECTORY), properties.getProperty(TARGET_DIRECTORY));
    }

    private static void parseDirectory(final String sourceDirectory, final String targetDirectory) {
        try {
            List<Path> paths = Files.walk(Paths.get(sourceDirectory)).filter(Files::isRegularFile).collect(Collectors.toList());

            List<Path> withoutFilteringPath = paths.stream().filter(e -> e.getFileName().toString().contains(FileNameConstants.COLOR_ENUM) || e.getFileName().toString().contains(FileNameConstants.PRODUCT_DISCLAIMER)).collect(Collectors.toList());
            if (!withoutFilteringPath.isEmpty()) {
                for (Path path : withoutFilteringPath) {
                    Files.copy(path, Paths.get(targetDirectory + path.getFileName()));
                }
            }

            Optional<Path> productMasterPath = paths.stream().filter(e -> e.getFileName().toString().contains(FileNameConstants.PRODUCT_MASTER)).findFirst();
            if (productMasterPath.isPresent()) {
                ProductMasterParser prodMastParser = new ProductMasterParser();
                prodMastParser.parseFile(productMasterPath.get(), targetDirectory, CATALOG_NAME);

                Optional<Path> categoryProductPath = paths.stream().filter(e -> e.getFileName().toString().contains(FileNameConstants.CATEGORY_PRODUCT)).findFirst();
                if (categoryProductPath.isPresent()) {
                    CategoryProductParser categoryProdParser = new CategoryProductParser();
                    categoryProdParser.parseFile(categoryProductPath.get(), targetDirectory, CATALOG_NAME);

                    List<Path> categoriesPath = paths.stream().filter(e -> e.getFileName().toString().contains(FileNameConstants.CATEGORIES)).collect(Collectors.toList());
                    if (!categoriesPath.isEmpty()) {
                        CategoriesParser categoriesParser = new CategoriesParser();
                        for (Path path : categoriesPath) {
                            categoriesParser.parseFile(path, targetDirectory, CATALOG_NAME, categoryProdParser.getCategories());
                        }
                    }

                    Optional<Path> productClassificationsPath = paths.stream().filter(e -> e.getFileName().toString().contains(FileNameConstants.PRODUCT_CLASSIFICATIONS)).findFirst();
                    if (productClassificationsPath.isPresent()) {
                        ProductClassificationsParser productClassificationsParser = new ProductClassificationsParser();
                        productClassificationsParser.parseFile(productClassificationsPath.get(), targetDirectory, prodMastParser.getProductVersions(), prodMastParser.getOriginalProducts());

                        List<Path> classAttributeAssignmentPath = paths.stream().filter(e -> e.getFileName().toString().contains(FileNameConstants.CLASS_ATTRIBUTE_ASSIGNMENT)).collect(Collectors.toList());
                        if (!classAttributeAssignmentPath.isEmpty()) {
                            ClassAttributeAssignmentParser classAttributeAssignmentParser = new ClassAttributeAssignmentParser();
                            for (Path path : classAttributeAssignmentPath) {
                                classAttributeAssignmentParser.parseFile(path, targetDirectory, productClassificationsParser.getClassifications());
                            }
                        }
                    }

                    Optional<Path> productReferencePath = paths.stream().filter(e -> e.getFileName().toString().contains(FileNameConstants.PRODUCT_REFERENCE)).findFirst();
                    if (productReferencePath.isPresent()) {
                        ProductReferenceParser productReferenceParser = new ProductReferenceParser();
                        productReferenceParser.parseFile(productReferencePath.get(), targetDirectory, prodMastParser.getProductVersions(), prodMastParser.getOriginalProducts());
                    }

                    List<Path> productSecondaryPath = paths.stream().filter(e -> e.getFileName().toString().contains(FileNameConstants.PRODUCT_SECONDARY)).collect(Collectors.toList());
                    if (!productSecondaryPath.isEmpty()) {
                        ProductSecondaryParser productSecondaryParser = new ProductSecondaryParser();
                        for (Path path : productSecondaryPath) {
                            productSecondaryParser.parseFile(path, targetDirectory, prodMastParser.getProductVersions(), prodMastParser.getOriginalProducts());
                        }
                    }

                    List<Path> productFeaturePath = paths.stream().filter(e -> e.getFileName().toString().contains(FileNameConstants.PRODUCT_FEATURE)).collect(Collectors.toList());
                    if (!productFeaturePath.isEmpty()) {
                        ProductFeatureParser productFeatureParser = new ProductFeatureParser();
                        for (Path path : productFeaturePath) {
                            productFeatureParser.parseFile(path, targetDirectory, prodMastParser.getProductVersions(), prodMastParser.getOriginalProducts());
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
