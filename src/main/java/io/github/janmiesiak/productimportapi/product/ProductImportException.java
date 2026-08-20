package io.github.janmiesiak.productimportapi.product;


public class ProductImportException extends RuntimeException {

    private final Integer rowNumber;
    private final String field;

    public ProductImportException(String message) {
        this(null, null, message, null);
    }

    public ProductImportException(
            String message,
            Throwable cause
    ) {
        this(null, null, message, cause);
    }

    public ProductImportException(
            Integer rowNumber,
            String field,
            String message
    ) {
        this(rowNumber, field, message, null);
    }

    private ProductImportException(
            Integer rowNumber,
            String field,
            String message,
            Throwable cause
    ) {
        super(message, cause);
        this.rowNumber = rowNumber;
        this.field = field;
    }

    public Integer getRowNumber() {
        return rowNumber;
    }

    public String getField() {
        return field;
    }
}