package com.chanshop.promotion.domain;

import com.chanshop.common.domain.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "time_deals", indexes = {
    @Index(name = "idx_product_id", columnList = "product_id"),
    @Index(name = "idx_status_start", columnList = "status, start_time"),
    @Index(name = "idx_end_time", columnList = "end_time")
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TimeDeal extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "product_id", nullable = false, unique = true)
    private Long productId;

    @Column(name = "original_price", nullable = false)
    private Integer originalPrice;

    @Column(name = "deal_price", nullable = false)
    private Integer dealPrice;

    @Column(name = "discount_rate", nullable = false)
    private Integer discountRate;

    @Column(name = "deal_stock", nullable = false)
    private Integer dealStock;

    @Column(name = "remaining_stock", nullable = false)
    private Integer remainingStock;

    @Column(name = "start_time", nullable = false)
    private LocalDateTime startTime;

    @Column(name = "end_time", nullable = false)
    private LocalDateTime endTime;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private DealStatus status;

    @Builder
    public TimeDeal(Long productId, Integer originalPrice, Integer dealPrice,
                    Integer dealStock, LocalDateTime startTime, LocalDateTime endTime) {
        this.productId = productId;
        this.originalPrice = originalPrice;
        this.dealPrice = dealPrice;
        this.discountRate = calculateDiscountRate(originalPrice, dealPrice);
        this.dealStock = dealStock;
        this.remainingStock = dealStock;
        this.startTime = startTime;
        this.endTime = endTime;
        this.status = DealStatus.SCHEDULED;
    }

    public enum DealStatus {
        SCHEDULED,  // 예정
        ONGOING,    // 진행중
        ENDED       // 종료
    }

    private Integer calculateDiscountRate(Integer original, Integer deal) {
        return (int) ((1 - (double) deal / original) * 100);
    }

    public void start() {
        this.status = DealStatus.ONGOING;
    }

    public void end() {
        this.status = DealStatus.ENDED;
    }

    public void decreaseStock(int quantity) {
        if (this.remainingStock < quantity) {
            throw new IllegalArgumentException("타임딜 재고가 부족합니다.");
        }
        this.remainingStock -= quantity;
        if (this.remainingStock == 0) {
            this.status = DealStatus.ENDED;
        }
    }

    public boolean isOngoing() {
        LocalDateTime now = LocalDateTime.now();
        return this.status == DealStatus.ONGOING &&
               now.isAfter(startTime) &&
               now.isBefore(endTime) &&
               this.remainingStock > 0;
    }
}
