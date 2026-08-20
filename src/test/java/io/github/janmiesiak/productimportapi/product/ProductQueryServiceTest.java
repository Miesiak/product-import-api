package io.github.janmiesiak.productimportapi.product;

import io.github.janmiesiak.productimportapi.PostgresTestConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(
        replace = AutoConfigureTestDatabase.Replace.NONE
)
@Import({
        PostgresTestConfiguration.class,
        ProductQueryService.class
})
class ProductQueryServiceTest {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductQueryService productQueryService;

    @Test
    void shouldGetAllProducts() {
        // Given
        Product product = new Product(
                "3502632480",
                "Camera Lens Cleaning Kit",
                new BigDecimal("14.99"),
                12
        );

        productRepository.saveAndFlush(product);

        // When
        List<Product> products =
                productQueryService.getAllProducts();

        // Then
        assertThat(products).hasSize(1);
        assertThat(products.getFirst().getEan())
                .isEqualTo("3502632480");
    }
}
