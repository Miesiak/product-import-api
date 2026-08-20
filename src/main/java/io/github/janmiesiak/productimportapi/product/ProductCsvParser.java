package io.github.janmiesiak.productimportapi.product;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Component
public class ProductCsvParser {



    private static final List<String> HEADERS =
            List.of("ean", "name", "price", "quantity");

    private static final CSVFormat CSV_FORMAT =
            CSVFormat.RFC4180.builder()
                    .setHeader()
                    .setSkipHeaderRecord(true)
                    .setTrim(true)
                    .get();

    public List<ProductImportRow> parse(InputStream inputStream) {
        try (
                Reader reader = new InputStreamReader(
                        inputStream,
                        StandardCharsets.UTF_8
                );
                CSVParser parser = CSV_FORMAT.parse(reader)
        ) {
            validateHeaders(parser);

            List<ProductImportRow> products = new ArrayList<>();

            for (CSVRecord record : parser) {
                products.add(toProductImportRow(record));
            }

            return products;
        } catch (IOException exception) {
            throw new ProductImportException(
                    "Could not read CSV file",
                    exception
            );
        }
    }

    private void validateHeaders(CSVParser parser) {
        if (!parser.getHeaderNames().equals(HEADERS)) {
            throw new ProductImportException(
                    1,
                    "header",
                    "CSV header must be: " + HEADERS
            );
        }
    }

    private ProductImportRow toProductImportRow(CSVRecord record) {
        int csvRowNumber = (int) record.getRecordNumber() + 1;

        if (!record.isConsistent()) {
            throw new ProductImportException(
                    csvRowNumber,
                    null,
                    "CSV row has an incorrect number of columns"
            );
        }

        return new ProductImportRow(
                record.get("ean"),
                record.get("name"),
                parsePrice(record, csvRowNumber),
                parseQuantity(record, csvRowNumber)
        );
    }

    private Integer parseQuantity(
            CSVRecord record,
            int csvRowNumber
    ) {
        String value = record.get("quantity");

        if (value.isBlank()) {
            return null;
        }

        try {
            return Integer.valueOf(value);
        } catch (NumberFormatException exception) {
            throw new ProductImportException(
                    csvRowNumber,
                    "quantity",
                    "Invalid quantity: " + value
            );
        }
    }

    private BigDecimal parsePrice(
            CSVRecord record,
            int csvRowNumber
    ) {
        String value = record.get("price");

        if (value.isBlank()) {
            return null;
        }

        try {
            return new BigDecimal(value);
        } catch (NumberFormatException exception) {
            throw new ProductImportException(
                    csvRowNumber,
                    "price",
                    "Invalid price: " + value
            );
        }
    }
}