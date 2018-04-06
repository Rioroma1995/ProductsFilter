import java.io.IOException;
import java.nio.file.*;
import java.util.Optional;
import java.util.stream.Stream;

public class DirectoryParser {
    private static final String PRODUCT_MASTER = "productmaster";

    public static void main(String[] args) {
        DirectoryParser.parseDirectory("D:\\mdmextract_hybris");
    }

    private static void parseDirectory(final String directory) {
        try {

            Stream<Path> paths = Files.walk(Paths.get(directory)).filter(Files::isRegularFile);
            Optional<Path> path = paths.filter(e -> e.getFileName().toString().contains(PRODUCT_MASTER)).findFirst();
            if (path.isPresent()) {
                ProductMasterParser.parseProductMasterFile(path.get());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
