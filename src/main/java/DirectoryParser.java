import java.io.IOException;
import java.nio.file.*;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class DirectoryParser {
    private static final String CATALOG_NAME = "mcLaundryProductCatalog";
    private static final String PRODUCT_MASTER = "productmaster";
    private static final String CATEGORY_PRODUCT = "categoryproduct";

    public static void main(String[] args) {
        DirectoryParser.parseDirectory("D:\\mdmextract_hybris");
    }

    private static void parseDirectory(final String directory) {
        try {
            List<Path> paths = Files.walk(Paths.get(directory)).filter(Files::isRegularFile).collect(Collectors.toList());
            Optional<Path> productMasterPath = paths.stream().filter(e -> e.getFileName().toString().contains(PRODUCT_MASTER)).findFirst();
            if (productMasterPath.isPresent()) {
                ProductMasterParser parser = new ProductMasterParser();
                parser.parseFile(productMasterPath.get(), CATALOG_NAME);
            }
            Optional<Path> categoryProductPath = paths.stream().filter(e -> e.getFileName().toString().contains(CATEGORY_PRODUCT)).findFirst();
            if (categoryProductPath.isPresent()) {
                CategoryProductParser parser = new CategoryProductParser();
                parser.parseFile(categoryProductPath.get(), CATALOG_NAME);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
