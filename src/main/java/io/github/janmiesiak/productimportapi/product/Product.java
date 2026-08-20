package io.github.janmiesiak.productimportapi.product;

import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(name="products")
public class Product {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, unique = true, length = 10)
    private String ean;
    @Column(nullable = false)
    private  String name;
    @Column(nullable = false, precision = 19, scale = 2)
    private  BigDecimal price;
    @Column(nullable = false)
    private  int quantity;

    //required by JPA
    protected Product(){}

    //testing purposes
    public Product(
            String ean,
            String name,
            BigDecimal price,
            int quantity
    ) {
        this.ean = ean;
        this.name = name;
        this.price = price;
        this.quantity = quantity;
    }
    public Long getId() {
        return id;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public int getQuantity() {
        return quantity;
    }

    public String getEan() {
        return ean;
    }

    public String getName() {
        return name;
    }
    public void updateDetails(
            String name,
            BigDecimal price,
            int quantity
    ) {
        this.name = name;
        this.price = price;
        this.quantity = quantity;
    }
}
