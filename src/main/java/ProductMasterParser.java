import org.apache.commons.csv.*;

import java.io.*;
import java.nio.file.*;
import java.util.HashSet;
import java.util.Set;

public class ProductMasterParser {
    private Set<String> productVersions;
    private Set<String> originalProducts;

    ProductMasterParser() {
        productVersions = new HashSet<>();
        originalProducts = new HashSet<>();
    }

    public void parseFile(Path path, String catalogName) throws IOException {
        try (Reader reader = Files.newBufferedReader(path);
             CSVParser csvParser = new CSVParser(reader, CSVFormat.EXCEL);

             BufferedWriter writer = Files.newBufferedWriter(Paths.get("D:\\newFile.csv"));
             CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.EXCEL)
        ) {
            for (CSVRecord csvRecord : csvParser) {
                if (csvRecord.get(12).contains(catalogName)) {
                    productVersions.add(csvRecord.get(0));
                    originalProducts.add(csvRecord.get(1));
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