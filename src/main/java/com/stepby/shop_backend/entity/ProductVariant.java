package com.stepby.shop_backend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "product_variants", uniqueConstraints = @UniqueConstraint(columnNames = {"product_id", "color", "size"}))
@Getter
@Setter
@NoArgsConstructor
@ToString(exclude = "product")
public class ProductVariant {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(nullable = false)
    private String color;

    @Column(nullable = false)
    private int size;

    @Column(nullable = false)
    private  int stockQuantity;

    public ProductVariant(String id, Product product, String color, int size, int stockQuantity) {
        this.id = id;
        this.product = product;
        this.color = color;
        this.size = size;
        this.stockQuantity = stockQuantity;
    }
}
