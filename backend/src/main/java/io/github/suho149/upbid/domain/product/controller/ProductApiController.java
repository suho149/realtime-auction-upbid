package io.github.suho149.upbid.domain.product.controller;

import io.github.suho149.upbid.domain.product.dto.ProductCreateRequest;
import io.github.suho149.upbid.domain.product.dto.ProductDetailResponse;
import io.github.suho149.upbid.domain.product.dto.ProductSimpleResponse;
import io.github.suho149.upbid.domain.product.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductApiController {

    private final ProductService productService;

    // 상품 등록 API
    @PostMapping
    public ResponseEntity<Void> createProduct(
            @AuthenticationPrincipal OAuth2User oAuth2User,
            @Valid @RequestBody ProductCreateRequest request) {

        String userEmail = oAuth2User.getAttribute("email");
        Long productId = productService.createProduct(userEmail, request);

        return ResponseEntity.created(URI.create("/api/products/" + productId)).build();
    }

    // 상품 목록 조회 API
    @GetMapping
    public ResponseEntity<Page<ProductSimpleResponse>> getAllProducts(
            @PageableDefault(size = 10, sort = "createdAt,desc") Pageable pageable) {

        Page<ProductSimpleResponse> products = productService.findAllProducts(pageable);
        return ResponseEntity.ok(products);
    }

    // 상품 상세 조회 API
    @GetMapping("/{productId}")
    public ResponseEntity<ProductDetailResponse> getProductById(@PathVariable Long productId) {
        ProductDetailResponse product = productService.findProductById(productId);
        return ResponseEntity.ok(product);
    }
}
