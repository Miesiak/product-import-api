package io.github.janmiesiak.productimportapi.product;

import io.github.janmiesiak.productimportapi.PostgresTestConfiguration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import static org.junit.jupiter.api.Assertions.assertThrows;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Import(PostgresTestConfiguration.class)
class ProductImportServiceIntegrationTest {

    @Autowired
    private ProductImportService productImportService;

    @Autowired
    private ProductCsvParser productCsvParser;

    @Autowired
    private ProductRepository productRepository;

    @BeforeEach
    void clearDatabase() {
        productRepository.deleteAll();
    }

    // Integration3 [Product import]
    // Import all 1,000 supplied rows into PostgreSQL.
    @Test
    @DisplayName(
            "Integration3 [Product import] — "
                    + "imports all 1,000 CSV rows"
    )
    void shouldImportAllProductsFromCsv() throws IOException {
        // Given
        List<ProductImportRow> expectedRows;

        try (InputStream inputStream = openCsv()) {
            expectedRows = productCsvParser.parse(inputStream);
        }

        ProductImportRow expectedLeadingZeroProduct =
                expectedRows.stream()
                        .filter(row -> row.ean().startsWith("0"))
                        .findFirst()
                        .orElseThrow();

        // When
        ProductImportResult result;

        try (InputStream inputStream = openCsv()) {
            result = productImportService
                    .importProducts(inputStream);
        }

        // Then
        assertThat(result.totalRows()).isEqualTo(1000);
        assertThat(result.createdProducts()).isEqualTo(1000);
        assertThat(result.updatedProducts()).isZero();

        assertThat(productRepository.count()).isEqualTo(1000);

        Product storedProduct = productRepository
                .findByEan(expectedLeadingZeroProduct.ean())
                .orElseThrow();

        assertThat(storedProduct.getEan())
                .isEqualTo(expectedLeadingZeroProduct.ean());

        assertThat(storedProduct.getName())
                .isEqualTo(expectedLeadingZeroProduct.name());

        assertThat(storedProduct.getPrice())
                .isEqualByComparingTo(
                        expectedLeadingZeroProduct.price()
                );

        assertThat(storedProduct.getQuantity())
                .isEqualTo(expectedLeadingZeroProduct.quantity());
    }

    // Integration4 [Updating an existing product by EAN]
    // Preserve ID/EAN while replacing mutable values.
    @Test
    @DisplayName(
            "Integration4 [Updating an existing product by EAN] — "
                    + "updates without creating a duplicate"
    )
    void shouldUpdateExistingProductByEan() {
        // Given
        Product existingProduct = productRepository.saveAndFlush(
                new Product(
                        "5555555555",
                        "Old name",
                        new BigDecimal("2.50"),
                        3
                )
        );

        Long existingId = existingProduct.getId();

        String csv = """
                ean,name,price,quantity
                5555555555,Updated name,9.99,25
                """;

        // When
        ProductImportResult result =
                productImportService.importProducts(
                        toInputStream(csv)
                );

        // Then
        assertThat(result.totalRows()).isEqualTo(1);
        assertThat(result.createdProducts()).isZero();
        assertThat(result.updatedProducts()).isEqualTo(1);

        assertThat(productRepository.count()).isEqualTo(1);

        Product updatedProduct = productRepository
                .findByEan("5555555555")
                .orElseThrow();

        assertThat(updatedProduct.getId())
                .isEqualTo(existingId);

        assertThat(updatedProduct.getEan())
                .isEqualTo("5555555555");

        assertThat(updatedProduct.getName())
                .isEqualTo("Updated name");

        assertThat(updatedProduct.getPrice())
                .isEqualByComparingTo("9.99");

        assertThat(updatedProduct.getQuantity())
                .isEqualTo(25);
    }

    private InputStream openCsv() {
        return Objects.requireNonNull(
                getClass().getResourceAsStream("/1_items.csv"),
                "Could not find 1_items.csv on the test classpath"
        );
    }

    private InputStream toInputStream(String csv) {
        return new ByteArrayInputStream(
                csv.getBytes(StandardCharsets.UTF_8)
        );
    }
    // Integration5 [Atomic import]
// An invalid row prevents every CSV row from being persisted.
    @Test
    @DisplayName(
            "Integration5 [Atomic import] — "
                    + "invalid row leaves database unchanged"
    )
    void shouldRejectCompleteImportWhenOneRowIsInvalid() {
        // Given
        productRepository.saveAndFlush(
                new Product(
                        "8888888888",
                        "Existing product",
                        new BigDecimal("7.50"),
                        4
                )
        );

        String csv = """
            ean,name,price,quantity
            6666666666,Valid new product,5.00,10
            7777777777,Invalid product,-1.00,5
            """;

        // When
        ProductImportException exception = assertThrows(
                ProductImportException.class,
                () -> productImportService.importProducts(
                        toInputStream(csv)
                )
        );

        // Then
        assertThat(exception.getRowNumber()).isEqualTo(3);
        assertThat(exception.getField()).isEqualTo("price");

        assertThat(productRepository.count()).isEqualTo(1);

        assertThat(productRepository.findByEan("8888888888"))
                .isPresent();

        assertThat(productRepository.findByEan("6666666666"))
                .isEmpty();

        assertThat(productRepository.findByEan("7777777777"))
                .isEmpty();
    }
}