package com.stepby.shop_backend.repository;

import com.stepby.shop_backend.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, String> {
    Optional<Product> findById(String id);
    List<Product> findByIsBestSellerTrue();
    List<Product> findByIsNewArrivalTrue();

    // 상품 이름에 특정 키워드가 포함된 상품 찾기
    List<Product> findByNameContainingIgnoreCase(String name);

    // 상품 이름 또는 브랜드에 특정 키워드가 포함된 상품 찾기
    List<Product> findByNameContainingIgnoreCaseOrBrandContainingIgnoreCase(String name, String brand);
}
