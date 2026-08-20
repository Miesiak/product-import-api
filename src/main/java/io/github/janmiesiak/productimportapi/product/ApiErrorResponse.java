package io.github.janmiesiak.productimportapi.product;

import java.time.Instant;

public record ApiErrorResponse(
        Instant timestamp,
        int status,
        String error,
        String message,
        String path,
        Integer rowNumber,
        String field
) {
}
