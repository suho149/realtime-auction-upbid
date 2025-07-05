package io.github.suho149.upbid.domain.product.repository;

import io.github.suho149.upbid.domain.product.entity.Product;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {

    // N+1 문제를 해결하기 위한 페치 조인(Fetch Join) 쿼리 (상품 상세 조회 시 사용)
    // "select p from Product p join fetch p.seller where p.id = :productId"
    // 상품(p)을 조회할 때, 연관된 판매자(p.seller) 정보도 한 번의 쿼리로 함께 가져옵니다.
    @Query("select p from Product p join fetch p.seller where p.id = :productId")
    Optional<Product> findByIdWithSeller(@Param("productId") Long productId);
}
