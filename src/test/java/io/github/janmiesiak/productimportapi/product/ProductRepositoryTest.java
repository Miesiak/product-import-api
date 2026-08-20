package io.github.janmiesiak.productimportapi.product;

import io.github.janmiesiak.productimportapi.PostgresTestConfiguration;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.context.annotation.Import;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(
        replace = AutoConfigureTestDatabase.Replace.NONE
)
@Import(PostgresTestConfiguration.class)
class ProductRepositoryTest {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private TestEntityManager entityManager;

    // Integration1 [Getting a product by EAN]
    // Persist and retrieve the same Product by its EAN.
    @Test
    @DisplayName(
            "Integration1 [Getting a product by EAN] — "
                    + "persists and retrieves a product"
    )
    void shouldPersistAndRetrieveProductByEan() {
        // Given
        Product product = new Product(
                "3502632480",
                "Camera Lens Cleaning Kit",
                new BigDecimal("14.99"),
                12
        );

        // When
        Product savedProduct =
                productRepository.saveAndFlush(product);

        entityManager.clear();

        Optional<Product> result =
                productRepository.findByEan("3502632480");

        // Then
        assertThat(savedProduct.getId()).isNotNull();
        assertThat(result).isPresent();

        Product foundProduct = result.orElseThrow();

        assertThat(foundProduct.getEan())
                .isEqualTo("3502632480");

        assertThat(foundProduct.getName())
                .isEqualTo("Camera Lens Cleaning Kit");

        assertThat(foundProduct.getPrice())
                .isEqualByComparingTo("14.99");

        assertThat(foundProduct.getQuantity())
                .isEqualTo(12);
    }

    // Integration2 [Searching products by name]
    // Verify partial and case-insensitive database search.
    @Test
    @DisplayName(
            "Integration2 [Searching products by name] — "
                    + "finds partial matches ignoring case"
    )
    void shouldSearchProductsByNameIgnoringCase() {
        // Given
        productRepository.saveAllAndFlush(List.of(
                new Product(
                        "1111111111",
                        "Whole Milk",
                        new BigDecimal("4.99"),
                        10
                ),
                new Product(
                        "2222222222",
                        "Milk Chocolate",
                        new BigDecimal("6.50"),
                        5
                ),
                new Product(
                        "3333333333",
                        "Bread",
                        new BigDecimal("3.20"),
                        20
                )
        ));

        entityManager.clear();

        // When
        List<Product> matchingProducts =
                productRepository
                        .findByNameContainingIgnoreCase("mIlK");

        List<Product> missingProducts =
                productRepository
                        .findByNameContainingIgnoreCase("cheese");

        // Then
        assertThat(matchingProducts)
                .extracting(Product::getName)
                .containsExactlyInAnyOrder(
                        "Whole Milk",
                        "Milk Chocolate"
                );

        assertThat(missingProducts).isEmpty();
    }
}