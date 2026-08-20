package io.github.janmiesiak.productimportapi.product;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {

    //Spring Data interprets the methods name
    Optional<Product> findByEan(String ean);

    //no need to declare getAll() method here - it is inherited from JpaRepository

    //filtering on database end, case-insensitive pattern at any position in record name
    List<Product> findByNameContainingIgnoreCase(String name);
    /*
    SELECT *
    FROM products
    WHERE LOWER(name) LIKE LOWER('%milk%');
    */
    //save(), saveAll(), findAll() inherited from JpaRepository


}