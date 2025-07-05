package io.github.suho149.upbid.domain.product.dto;

import io.github.suho149.upbid.domain.product.entity.Product;
import io.github.suho149.upbid.domain.product.entity.ProductStatus;

import java.time.LocalDateTime;

public record ProductDetailResponse(
        Long id,
        String sellerName,
        String title,
        String description,
        Long startPrice,
        Long currentPrice,
        LocalDateTime startTime,
        LocalDateTime endTime,
        ProductStatus status
) {
    public static ProductDetailResponse from(Product product) {
        return new ProductDetailResponse(
                product.getId(),
                product.getSeller().getName(),
                product.getTitle(),
                product.getDescription(),
                product.getStartPrice(),
                product.getCurrentPrice(),
                product.getStartTime(),
                product.getEndTime(),
                product.getStatus()
        );
    }
}
