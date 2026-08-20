package io.github.janmiesiak.productimportapi.product;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

class ProductImportRowValidationTest {

    private static ValidatorFactory validatorFactory;
    private static Validator validator;

    @BeforeAll
    static void setUpValidator() {
        validatorFactory =
                Validation.buildDefaultValidatorFactory();

        validator = validatorFactory.getValidator();
    }

    @AfterAll
    static void closeValidatorFactory() {
        validatorFactory.close();
    }

    @Test
    void shouldAcceptValidProductData() {
        ProductImportRow row = new ProductImportRow(
                "0123456789",
                "Milk",
                new BigDecimal("4.99"),
                0
        );

        assertThat(validator.validate(row)).isEmpty();
    }

    @Test
    void shouldRejectInvalidEans() {
        for (String ean : Arrays.asList(
                null,
                "",
                " ",
                "123",
                "12345678901",
                "12345abcde"
        )) {
            ProductImportRow row = new ProductImportRow(
                    ean,
                    "Milk",
                    new BigDecimal("4.99"),
                    10
            );

            assertViolationFor(row, "ean");
        }
    }

    @Test
    void shouldRejectInvalidNames() {
        for (String name : Arrays.asList(null, "", " ")) {
            ProductImportRow row = new ProductImportRow(
                    "0123456789",
                    name,
                    new BigDecimal("4.99"),
                    10
            );

            assertViolationFor(row, "name");
        }
    }

    @Test
    void shouldRejectInvalidPrices() {
        for (BigDecimal price : Arrays.asList(
                null,
                BigDecimal.ZERO,
                new BigDecimal("-0.01")
        )) {
            ProductImportRow row = new ProductImportRow(
                    "0123456789",
                    "Milk",
                    price,
                    10
            );

            assertViolationFor(row, "price");
        }
    }

    @Test
    void shouldRejectInvalidQuantities() {
        for (Integer quantity : Arrays.asList(null, -1)) {
            ProductImportRow row = new ProductImportRow(
                    "0123456789",
                    "Milk",
                    new BigDecimal("4.99"),
                    quantity
            );

            assertViolationFor(row, "quantity");
        }
    }

    private void assertViolationFor(
            ProductImportRow row,
            String field
    ) {
        boolean violationFound = validator.validate(row)
                .stream()
                .anyMatch(violation ->
                        violation.getPropertyPath()
                                .toString()
                                .equals(field)
                );

        assertThat(violationFound)
                .as("Expected validation error for field: %s", field)
                .isTrue();
    }
}