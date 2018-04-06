import org.apache.commons.csv.*;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;

public class ProductMasterParser {
    private static Set<String> productVersions = new HashSet<>();
    private static Set<String> originalProducts = new HashSet<>();

    public static void parseProductMasterFile(Path path) throws IOException {
        try (Reader reader = Files.newBufferedReader(path);
             CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT)) {
            for (CSVRecord csvRecord : csvParser) {
                productVersions.add(csvRecord.get(0));
                originalProducts.add(csvRecord.get(1));
            }
        }
        System.out.println(productVersions.size());
        System.out.println(originalProducts.size());
    }
}
