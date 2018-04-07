import org.apache.commons.csv.*;

import java.io.*;
import java.nio.file.*;

public class CategoriesParser {
    private static final Integer CATALOG_ATTR = 3;

    public void parseFile(Path path, String catalogName) throws IOException {
        try (Reader reader = Files.newBufferedReader(path);
             CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT);
             BufferedWriter writer = Files.newBufferedWriter(Paths.get("D:\\newCategory.csv"));
             CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.EXCEL)
        ) {
            for (CSVRecord csvRecord : csvParser) {
                if (csvRecord.get(CATALOG_ATTR).contains(catalogName)) {
                    csvPrinter.printRecord(csvRecord);
                }
            }
            csvPrinter.flush();
        }
    }
}