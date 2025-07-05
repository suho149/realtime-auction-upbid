package io.github.suho149.upbid.domain.product.service;

import io.github.suho149.upbid.domain.product.dto.ProductCreateRequest;
import io.github.suho149.upbid.domain.product.dto.ProductDetailResponse;
import io.github.suho149.upbid.domain.product.dto.ProductSimpleResponse;
import io.github.suho149.upbid.domain.product.entity.Product;
import io.github.suho149.upbid.domain.product.repository.ProductRepository;
import io.github.suho149.upbid.domain.user.entity.User;
import io.github.suho149.upbid.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true) // 기본적으로 모든 메소드는 읽기 전용 트랜잭션
public class ProductService {

    private final ProductRepository productRepository;
    private final UserRepository userRepository; // 판매자 정보를 가져오기 위해 필요

    @Transactional // 쓰기 작업이므로 readOnly=false 적용
    public Long createProduct(String userEmail, ProductCreateRequest request) {
        // 1. 사용자 조회
        User seller = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        // 2. DTO를 Entity로 변환
        Product product = request.toEntity(seller);

        // 3. DB에 저장
        Product savedProduct = productRepository.save(product);

        return savedProduct.getId();
    }

    public Page<ProductSimpleResponse> findAllProducts(Pageable pageable) {
        // Pageable을 사용하여 페이징된 상품 목록 조회
        return productRepository.findAll(pageable)
                .map(ProductSimpleResponse::from); // Page<Product> -> Page<ProductSimpleResponse>
    }

    public ProductDetailResponse findProductById(Long productId) {
        // findById 대신 findByIdWithSeller를 사용하여 N+1 문제 방지
        Product product = productRepository.findByIdWithSeller(productId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 상품입니다."));

        return ProductDetailResponse.from(product);
    }
}
