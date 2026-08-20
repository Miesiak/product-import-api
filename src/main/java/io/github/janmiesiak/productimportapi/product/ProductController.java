package io.github.janmiesiak.productimportapi.product;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductQueryService productQueryService;
    private final ProductImportService productImportService;

    public ProductController(
            ProductQueryService productQueryService,
            ProductImportService productImportService
    ) {
        this.productQueryService = productQueryService;
        this.productImportService = productImportService;
    }

    @GetMapping
    public List<Product> getProducts(
            @RequestParam(required = false) String name
    ) {
        if (name == null) {
            return productQueryService.getAllProducts();
        }

        return productQueryService.searchByName(name);
    }

    @GetMapping("/{ean}")
    public ResponseEntity<Product> getProductByEan(
            @PathVariable String ean
    ) {
/*        return productQueryService.findByEan(ean)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());*/
        return productQueryService.findByEan(ean)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ProductNotFoundException(ean));

    }

    @PostMapping(
            value = "/import",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<ProductImportResult> importProducts(
            @RequestParam("file") MultipartFile file
    ) throws IOException {
        ProductImportResult result =
                productImportService.importProducts(
                        file.getInputStream()
                );

        return ResponseEntity.ok(result);
    }
}