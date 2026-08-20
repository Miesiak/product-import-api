package io.github.janmiesiak.productimportapi.product;

public class InvalidProductSearchException extends RuntimeException {

    public InvalidProductSearchException(String message) {
        super(message);
    }
}
