package io.github.suho149.upbid.domain.product.dto;

import io.github.suho149.upbid.domain.product.entity.Product;

import java.time.LocalDateTime;

public record ProductSimpleResponse(
        Long id,
        String title,
        Long currentPrice,
        LocalDateTime endTime,
        String thumbnailUrl // 예시를 위한 썸네일 URL
) {
    public static ProductSimpleResponse from(Product product) {
        return new ProductSimpleResponse(
                product.getId(),
                product.getTitle(),
                product.getCurrentPrice(),
                product.getEndTime(),
                "https://via.placeholder.com/150" // 실제로는 이미지 URL 필드 사용
        );
    }
}