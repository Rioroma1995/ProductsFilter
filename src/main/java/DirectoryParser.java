import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Collectors;

public class DirectoryParser {
    private static final String CATALOG_NAME = "mcLaundryProductCatalog";
    //file names
    private static final String PRODUCT_MASTER = "productmaster";
    private static final String CATEGORY_PRODUCT = "categoryproduct";
    private static final String CATEGORIES = "categories";
    private static final String PRODUCT_CLASSIFICATIONS = "product_classifications";

    private static final String SOURCE_DIRECTORY = "source.directory";
    private static final String TARGET_DIRECTORY = "target.directory";

    public static void main(String[] args) {
        Properties properties = new Properties();
        try (InputStream is = DirectoryParser.class.getResource("csv.properties").openStream()) {
            properties.load(is);
        } catch (IOException ex) {
            throw new UncheckedIOException("Failed to load resource", ex);
        }
        parseDirectory(properties.getProperty(SOURCE_DIRECTORY), properties.getProperty(TARGET_DIRECTORY));
    }

    private static void parseDirectory(final String sourceDirectory, final String targetDirectory) {
        try {
            List<Path> paths = Files.walk(Paths.get(sourceDirectory)).filter(Files::isRegularFile).collect(Collectors.toList());
            Optional<Path> productMasterPath = paths.stream().filter(e -> e.getFileName().toString().contains(PRODUCT_MASTER)).findFirst();
            if (productMasterPath.isPresent()) {
                ProductMasterParser prodMastParser = new ProductMasterParser();
                prodMastParser.parseFile(productMasterPath.get(), targetDirectory, CATALOG_NAME);

                Optional<Path> categoryProductPath = paths.stream().filter(e -> e.getFileName().toString().contains(CATEGORY_PRODUCT)).findFirst();
                if (categoryProductPath.isPresent()) {
                    CategoryProductParser categoryProdParser = new CategoryProductParser();
                    categoryProdParser.parseFile(categoryProductPath.get(), targetDirectory, CATALOG_NAME);

                    List<Path> categoriesPath = paths.stream().filter(e -> e.getFileName().toString().contains(CATEGORIES)).collect(Collectors.toList());
                    if (!categoriesPath.isEmpty()) {
                        CategoriesParser categoriesParser = new CategoriesParser();
                        for (Path path : categoriesPath) {
                            categoriesParser.parseFile(path, targetDirectory, CATALOG_NAME, categoryProdParser.getCategories());
                        }
                    }

                    Optional<Path> productClassificationsPath = paths.stream().filter(e -> e.getFileName().toString().contains(PRODUCT_CLASSIFICATIONS)).findFirst();
                    if (productClassificationsPath.isPresent()) {
                        ProductClassificationsParser productClassificationsParser = new ProductClassificationsParser();
                        productClassificationsParser.parseFile(productClassificationsPath.get(), targetDirectory, prodMastParser.getProductVersions(), prodMastParser.getOriginalProducts());
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
