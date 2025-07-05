package io.github.suho149.upbid.domain.product.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ProductStatus {
    PREPARED("판매 준비중"),
    ON_SALE("판매중"),
    SOLD_OUT("판매 완료");

    private final String description;
}
