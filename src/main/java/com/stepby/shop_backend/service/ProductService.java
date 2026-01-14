package com.stepby.shop_backend.service;

import com.stepby.shop_backend.dto.product.ProductResponseDto;
import com.stepby.shop_backend.entity.Product;
import com.stepby.shop_backend.entity.ProductVariant;
import com.stepby.shop_backend.repository.ProductRepository;
import com.stepby.shop_backend.repository.ProductVariantRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class ProductService {
    private final ProductRepository productRepository;
    private final ProductVariantRepository productVariantRepository;
    private final Random random = new Random(); // 랜덤 객체 초기화
    private final int productCount = 20; // 생성할 상품 개수

    @Autowired
    public ProductService(ProductRepository productRepository, ProductVariantRepository productVariantRepository) {
        this.productRepository = productRepository;
        this.productVariantRepository = productVariantRepository;
    }

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public Optional<Product> getProductById(String id) {
        return productRepository.findById(id);
    }

    @PostConstruct
    @Transactional
    public void init() {
        if(productRepository.count() == 0){
            productVariantRepository.deleteAll();
            productRepository.deleteAll();

            System.out.println("========= 더미 데이터 생성 시작 ===========");
            generateDummyData();
            System.out.println("========= 더미 데이터 생성 완료 ===========" + productRepository.count() + "개의 제품 생성");
        } else {
            System.out.println("이미 데이터베이스에 제품이 존재하여 더미 데이터를 생성하지 않습니다.");
        }
    }

    @Transactional(readOnly = true)
    public List<ProductResponseDto> getBestSellers() {
        List<Product> bestSellerProducts = productRepository.findByIsBestSellerTrue();

        return bestSellerProducts.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ProductResponseDto> getNewArrivals() {
        // ProductRepository에 isNewArrival이 true인 제품을 조회하는 메서드가 필요합니다.
        List<Product> newArrivalProducts = productRepository.findByIsNewArrivalTrue();
        return newArrivalProducts.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }


    private ProductResponseDto convertToDto(Product product) {
        // ProductResponseDto의 필드에 맞춰 Product 엔티티의 데이터를 매핑합니다.
        // 예시: Builder 패턴 사용
        return ProductResponseDto.builder()
                .id(product.getId())
                .name(product.getName())
                .brand(product.getBrand()) // brand 추가
                .price(product.getPrice())
                .discountPrice(product.getDiscountPrice())
                .category(product.getCategory())
                .gender(product.getGender()) // gender 추가
                .description(product.getDescription()) // description 추가
                .mainImageUrl(product.getMainImageUrl())
                .averageRating(product.getAverageRating())
                .reviewCount(product.getReviewCount())
                .isBestSeller(product.isBestSeller()) // isBestSeller getter는 isBestSeller() 형태로
                .isNewArrival(product.isNewArrival()) // isNewArrival getter는 isNewArrival() 형태로
                .isKids(product.isKids()) // isKids getter는 isKids() 형태로
                .build();
    }

    private void generateDummyData() {
        String[] brands = {"스텝바이브랜드", "SOBOOM 슈즈", "워커월드", "스피드런", "스텝바이키즈"};
        String[] categories = {"sneakers", "sports", "shoes", "sandles", "boots"};
        String[] genders = {"남성", "여성", "공용"};
        String[] colors = {"블랙", "화이트", "그레이", "레드", "블루", "그린"};
        int[] sizes = {230, 240, 250, 260, 270, 280, 290};

        for (int i = 0; i < productCount; i++){
            Product product = new Product();
            product.setName("StepBy " + (i + 1) + "번 제품");
            product.setBrand(brands[random.nextInt(brands.length)]);
            product.setPrice(random.nextInt(100) * 1000 + 50000); // 50,000 ~ 149,000
            product.setDiscountPrice(random.nextBoolean() ? (int) (product.getPrice() * (0.7 + random.nextDouble() * 0.2)) : null); // 70~90% 할인 또는 null
            product.setDetails("이 제품은 StepBy " + product.getName() + "의 상세 정보입니다.");
            product.setCategory(categories[random.nextInt(categories.length)]);
            product.setGender(genders[random.nextInt(genders.length)]);
            product.setDescription("최고급 소재로 제작된 " + product.getBrand() + "의 " + product.getCategory() + "입니다. 착화감이 뛰어나며 스타일을 완성해줍니다.");
            product.setMainImageUrl("https://cdn.shoemarker.co.kr/Upload/ProductImage/080207/49447_1_0320_0320.jpg");

            List<String> imageUrls= new ArrayList<>();
            imageUrls.add("https://cdn.shoemarker.co.kr/Upload/ProductImage/080207/49447_1_0320_0320.jpg");
            imageUrls.add("https://cdn.shoemarker.co.kr/Upload/ProductImage/080207/49447_1_0320_0320.jpg");
            product.setImageUrls(imageUrls);

            // 소재 리스트
            List<String> materials = new ArrayList<>();
            materials.add("가죽");
            materials.add(random.nextBoolean() ? "합성 섬유" : "고무");
            product.setMaterials(materials);

            // 색상 리스트 (Product entity의 colors 필드)
            List<String> productColors = new ArrayList<>();
            productColors.add(colors[random.nextInt(colors.length)]);
            if (random.nextBoolean()) productColors.add(colors[random.nextInt(colors.length)]); // 두 번째 색상 추가
            product.setColors(productColors);


            // 사이즈 리스트 (Product entity의 availableSizes 필드)
            List<Integer> productSizes = new ArrayList<>();
            for(int s=0; s< random.nextInt(3)+3; s++){ // 3~5개 사이즈
                productSizes.add(sizes[random.nextInt(sizes.length)]);
            }
            product.setAvailableSizes(productSizes);


            product.setAverageRating(Math.round((random.nextDouble() * 4 + 1) * 10.0) / 10.0); // 1.0 ~ 5.0
            product.setReviewCount(random.nextInt(200));

            product.setBestSeller(random.nextBoolean());
            product.setNewArrival(random.nextBoolean());
            // 카테고리가 "키즈"인 경우 isKids를 true로 설정 (SOBOOM님 기존 설정을 고려)
            if (product.getCategory().equals("키즈")) {
                product.setKids(true);
            } else {
                product.setKids(random.nextBoolean()); // 키즈 제품 아닐 때는 랜덤으로
            }

            // Product 저장
            Product savedProduct = productRepository.save(product);

            // ProductVariant 생성 및 저장 (고유성 보장)
            // 각 색상과 사이즈 조합이 유니크하게 생성되도록 로직 변경
            Set<String> generatedVariantCombinations = new HashSet<>(); // (color, size) 조합 중복 방지

            for (String color : productColors) { // Product에 선택된 색상
                List<Integer> availableSizesForCurrentColor = new ArrayList<>(productSizes);
                Collections.shuffle(availableSizesForCurrentColor); // 사이즈 순서를 섞음

                // 각 색상별로 최소 1개에서 최대 3개의 사이즈 조합만 생성
                int numSizesToPick = random.nextInt(Math.min(availableSizesForCurrentColor.size(), 3)) + 1;
                for (int sIdx = 0; sIdx < numSizesToPick; sIdx++) {
                    int size = availableSizesForCurrentColor.get(sIdx);

                    String combinationKey = color + "-" + size;
                    if (generatedVariantCombinations.contains(combinationKey)) {
                        continue; // 이미 생성된 조합이면 건너뛰기
                    }
                    generatedVariantCombinations.add(combinationKey);

                    ProductVariant variant = new ProductVariant();
                    // ID는 @GeneratedValue에 의해 자동으로 생성됩니다. 수동 설정 제거
                    variant.setProduct(savedProduct);
                    variant.setColor(color);
                    variant.setSize(size);
                    variant.setStockQuantity(random.nextInt(100) + 10); // 재고 10 ~ 109

                    productVariantRepository.save(variant);
                }
            }
        }
    }
}
