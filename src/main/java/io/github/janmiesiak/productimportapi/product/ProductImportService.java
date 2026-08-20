package io.github.janmiesiak.productimportapi.product;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.HashSet;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Set;

@Service
public class ProductImportService {

    private final ProductCsvParser productCsvParser;
    private final ProductRepository productRepository;
    private final Validator validator;

    public ProductImportService(
            ProductCsvParser productCsvParser,
            ProductRepository productRepository,
            Validator validator
    ) {
        this.productCsvParser = productCsvParser;
        this.productRepository = productRepository;
        this.validator = validator;
    }

    @Transactional
    public ProductImportResult importProducts(InputStream inputStream) {
        List<ProductImportRow> rows = parseRows(inputStream);

        validateRows(rows);

        int createdProducts = 0;
        int updatedProducts = 0;

        for (ProductImportRow row : rows) {
            var existingProduct =
                    productRepository.findByEan(row.ean());

            if (existingProduct.isPresent()) {
                Product product = existingProduct.get();

                product.updateDetails(
                        row.name(),
                        row.price(),
                        row.quantity()
                );

                updatedProducts++;
            } else {
                Product product = new Product(
                        row.ean(),
                        row.name(),
                        row.price(),
                        row.quantity()
                );

                productRepository.save(product);
                createdProducts++;
            }
        }

        return new ProductImportResult(
                rows.size(),
                createdProducts,
                updatedProducts
        );
    }

    private List<ProductImportRow> parseRows(
            InputStream inputStream
    ) {
        List<ProductImportRow> rows =
                productCsvParser.parse(inputStream);

        if (rows.isEmpty()) {
            throw new ProductImportException(
                    "CSV file contains no products"
            );
        }

        return rows;
    }

    private void validateRows(List<ProductImportRow> rows) {
        Set<String> seenEans = new HashSet<>();

        for (int index = 0; index < rows.size(); index++) {
            ProductImportRow row = rows.get(index);

            Set<ConstraintViolation<ProductImportRow>> violations =
                    validator.validate(row);

            if (!violations.isEmpty()) {
                ConstraintViolation<ProductImportRow> violation =
                        violations.iterator().next();

                throw new ProductImportException(
                        index + 2,
                        violation.getPropertyPath().toString(),
                        violation.getMessage()
                );
            }

            if (!seenEans.add(row.ean())) {
                throw new ProductImportException(
                        index + 2,
                        "ean",
                        "Duplicate EAN in CSV file: " + row.ean()
                );
            }
        }
    }

}