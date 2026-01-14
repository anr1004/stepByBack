package com.stepby.shop_backend.dto.product;

import com.stepby.shop_backend.dto.auth.UserResponse;
import com.stepby.shop_backend.entity.Product;
import com.stepby.shop_backend.entity.User;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder // DTO 생성을 쉽게 하기 위해 Builder 패턴을 사용합니다.
public class ProductResponseDto {
    private String id;
    private String name;
    private String brand; // 브랜드 정보 추가
    private int price;
    private Integer discountPrice; // 할인 가격
    private String category;
    private String gender; // 성별 정보 추가
    private String description; // 간략한 설명 (ProductCard에서는 모든 details 대신 이 정도만?)
    private String mainImageUrl; // 메인 이미지 URL
    private double averageRating; // 평균 리뷰
    private int reviewCount; // 리뷰 수
    private boolean isBestSeller;
    private boolean isNewArrival;
    private boolean isKids; // 키즈 상품 여부

    public static ProductResponseDto fromEntity(Product product){
        if (product == null) {
            return null;
        }
        return ProductResponseDto.builder()
                .id(product.getId()) // Product 엔티티의 ID 사용
                .name(product.getName()) // Product 엔티티의 Name 사용
                .brand(product.getBrand()) // Product 엔티티의 Brand 사용
                .price(product.getPrice()) // Product 엔티티의 Price 사용
                .discountPrice(product.getDiscountPrice()) // Product 엔티티의 DiscountPrice 사용
                .category(product.getCategory()) // Product 엔티티의 Category 사용
                .gender(product.getGender()) // Product 엔티티의 Gender 사용
                .description(product.getDescription()) // Product 엔티티의 Description 사용
                .mainImageUrl(product.getMainImageUrl()) // Product 엔티티의 MainImageUrl 사용
                .averageRating(product.getAverageRating()) // Product 엔티티의 AverageRating 사용
                .reviewCount(product.getReviewCount()) // Product 엔티티의 ReviewCount 사용
                .isBestSeller(product.isBestSeller()) // Product 엔티티의 isBestSeller 사용
                .isNewArrival(product.isNewArrival()) // Product 엔티티의 isNewArrival 사용
                .isKids(product.isKids()) // Product 엔티티의 isKids 사용
                .build();
    }
}
