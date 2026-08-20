package io.github.janmiesiak.productimportapi.product;

//contains details of a future successful http response

public record ProductImportResult(
        int totalRows,
        int createdProducts,
        int updatedProducts
)
{
}