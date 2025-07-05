package io.github.suho149.upbid.domain.product.dto;

import io.github.suho149.upbid.domain.product.entity.Product;
import io.github.suho149.upbid.domain.user.entity.User;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record ProductCreateRequest(
        @NotBlank(message = "상품 제목은 필수입니다.")
        String title,

        @NotBlank(message = "상품 설명은 필수입니다.")
        String description,

        @NotNull(message = "시작 가격은 필수입니다.")
        @Min(value = 100, message = "시작 가격은 100원 이상이어야 합니다.")
        Long startPrice,

        @NotNull(message = "경매 시작 시간은 필수입니다.")
        LocalDateTime startTime,

        @NotNull(message = "경매 종료 시간은 필수입니다.")
        @Future(message = "경매 종료 시간은 현재 이후여야 합니다.")
        LocalDateTime endTime
) {
    public Product toEntity(User seller) {
        return Product.builder()
                .seller(seller)
                .title(title)
                .description(description)
                .startPrice(startPrice)
                .startTime(startTime)
                .endTime(endTime)
                .build();
    }
}
