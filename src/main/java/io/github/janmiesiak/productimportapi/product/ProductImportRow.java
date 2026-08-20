package io.github.janmiesiak.productimportapi.product;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public record ProductImportRow(

        @NotBlank(message = "EAN is mandatory")
        @Pattern(
                regexp = "^[0-9]{10}$",
                message = "EAN must contain exactly 10 digits"
        )
        String ean,
        @NotBlank(message = "Name is mandatory")
        String name,
        @NotNull(message = "Price is mandatory")
        @Positive(message = "Price must be greater than zero")
        BigDecimal price,
        @NotNull(message = "Quantity is mandatory")
        @PositiveOrZero(
                message = "Quantity must be greater than or equal to zero"
        )Integer quantity
)
{ }
