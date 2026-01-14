package com.stepby.shop_backend.controller;

import com.stepby.shop_backend.dto.product.ProductResponseDto;
import com.stepby.shop_backend.entity.Product;
import com.stepby.shop_backend.repository.ProductRepository;
import com.stepby.shop_backend.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/products")
public class ProductController {
    private final ProductService productService;
    private final ProductRepository productRepository;

    @Autowired
    public ProductController(ProductRepository productRepository, ProductService productService) {
        this.productRepository = productRepository;
        this.productService = productService;
    }


    // 모든 상품 조회
    @GetMapping
    public ResponseEntity<List<Product>> getAllProducts() {
        List<Product> products = productService.getAllProducts();
        return ResponseEntity.ok(products);
    }


    //  ID를 통해 특정 상품 상세 정보 조회
    @GetMapping("/{id}")
    public ResponseEntity<Product> getProductById(@PathVariable String id) {
        return productService.getProductById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/bestsellers")
    public ResponseEntity<List<ProductResponseDto>> getBestSellers() {
        List<ProductResponseDto> bestSellers = productService.getBestSellers();
        return ResponseEntity.ok(bestSellers);
    }

    @GetMapping("/new-arrivals")
    public ResponseEntity<List<ProductResponseDto>> getNewArrivals() {
        List<ProductResponseDto> newArrivals = productService.getNewArrivals();
        return ResponseEntity.ok(newArrivals);
    }

    @GetMapping("/search")
    public ResponseEntity<List<ProductResponseDto>> searchProducts(@RequestParam("q") String query) {
        // q 파라미터로 검색어를 받습니다.
        if (query == null || query.trim().isEmpty()) {
            // 검색어가 없으면 빈 리스트 반환 또는 모든 상품 반환
            return ResponseEntity.ok(List.of()); // 빈 리스트 반환 예시
        }

        List<Product> products = productRepository.findByNameContainingIgnoreCaseOrBrandContainingIgnoreCase(query, query);

        List<ProductResponseDto> productDtos = products.stream()
                .map(ProductResponseDto::fromEntity)
                .collect(Collectors.toList());

        return ResponseEntity.ok(productDtos);
    }

}
