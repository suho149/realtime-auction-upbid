package io.github.suho149.upbid.domain.product.entity;

import io.github.suho149.upbid.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class) // Auditing 기능 활성화
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY) // 지연 로딩 설정
    @JoinColumn(name = "user_id", nullable = false)
    private User seller; // 판매자 (User 엔티티와 연관 관계)

    @Column(nullable = false, length = 100)
    private String title;

    @Lob // 대용량 텍스트를 위한 어노테이션
    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private Long startPrice; // 시작 가격

    private Long currentPrice; // 현재 가격 (경매 진행 시 업데이트됨)

    @Column(nullable = false)
    private LocalDateTime startTime; // 경매 시작 시간

    @Column(nullable = false)
    private LocalDateTime endTime; // 경매 종료 시간

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ProductStatus status;

    @CreatedDate // 엔티티 생성 시 시간 자동 저장
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @Builder
    public Product(User seller, String title, String description, Long startPrice, LocalDateTime startTime, LocalDateTime endTime) {
        this.seller = seller;
        this.title = title;
        this.description = description;
        this.startPrice = startPrice;
        this.currentPrice = startPrice; // 처음엔 현재가 = 시작가
        this.startTime = startTime;
        this.endTime = endTime;
        this.status = ProductStatus.PREPARED; // 처음엔 준비중 상태
    }
}
