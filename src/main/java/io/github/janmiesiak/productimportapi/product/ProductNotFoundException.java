package io.github.janmiesiak.productimportapi.product;

public class ProductNotFoundException extends RuntimeException {

    public ProductNotFoundException(String ean) {
        super("Product with EAN " + ean + " was not found");
    }
}
