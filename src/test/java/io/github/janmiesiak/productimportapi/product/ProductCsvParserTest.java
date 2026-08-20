package io.github.janmiesiak.productimportapi.product;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ProductCsvParserTest {

    private final ProductCsvParser parser =
            new ProductCsvParser();

    @Test
    void shouldParseValidCsvRow() {
        String csv = """
                ean,name,price,quantity
                0123456789,"Milk, lactose free",4.99,10
                """;

        List<ProductImportRow> rows =
                parser.parse(toInputStream(csv));

        assertThat(rows).hasSize(1);

        ProductImportRow row = rows.get(0);

        assertThat(row.ean()).isEqualTo("0123456789");
        assertThat(row.name()).isEqualTo("Milk, lactose free");
        assertThat(row.price()).isEqualByComparingTo("4.99");
        assertThat(row.quantity()).isEqualTo(10);
    }

    @Test
    void shouldReportInvalidPrice() {
        String csv = """
                ean,name,price,quantity
                0123456789,Milk,abc,10
                """;

        ProductImportException exception = assertThrows(
                ProductImportException.class,
                () -> parser.parse(toInputStream(csv))
        );

        assertThat(exception.getRowNumber()).isEqualTo(2);
        assertThat(exception.getField()).isEqualTo("price");
        assertThat(exception.getMessage())
                .isEqualTo("Invalid price: abc");
    }

    @Test
    void shouldRejectIncorrectHeader() {
        String csv = """
                code,name,price,quantity
                0123456789,Milk,4.99,10
                """;

        ProductImportException exception = assertThrows(
                ProductImportException.class,
                () -> parser.parse(toInputStream(csv))
        );

        assertThat(exception.getRowNumber()).isEqualTo(1);
        assertThat(exception.getField()).isEqualTo("header");
    }

    private InputStream toInputStream(String csv) {
        return new ByteArrayInputStream(
                csv.getBytes(StandardCharsets.UTF_8)
        );
    }
}