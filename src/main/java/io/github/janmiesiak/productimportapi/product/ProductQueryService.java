package io.github.janmiesiak.productimportapi.product;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProductQueryService {
    private final ProductRepository productRepository;
    //Spring Data automatically creates and passes productRepository to the constructor (dependency injection)
    public ProductQueryService(ProductRepository productRepository) {

        this.productRepository = productRepository;
    }

    public List<Product> getAllProducts() {

        return productRepository.findAll();
    }

    public Optional<Product> findByEan(String ean){
        return productRepository.findByEan(ean);
    }
    public List<Product> searchByName(String name){
        if (name == null || name.isBlank()) {
            throw new InvalidProductSearchException(
                    "Name filter must not be blank"
            );
        }
        //.strip() to handle accidental spaces (old .trim() method)
        return productRepository.findByNameContainingIgnoreCase(name.strip());
    }

}
