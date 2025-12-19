package com.chanshop.product.domain;

import com.chanshop.common.domain.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Entity
@Table(name = "products", indexes = {
    @Index(name = "idx_category_id", columnList = "category_id"),
    @Index(name = "idx_status", columnList = "status"),
    @Index(name = "idx_created_at", columnList = "created_at"),
    @Index(name = "idx_view_count", columnList = "view_count"),
    @Index(name = "idx_order_count", columnList = "order_count"),
    @Index(name = "idx_covering", columnList = "category_id, id")
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Product extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    private Integer price;

    @Column(name = "discount_price")
    private Integer discountPrice;

    @Column(nullable = false)
    private Integer stock;

    @Column(name = "category_id", nullable = false)
    private Long categoryId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ProductStatus status;

    @Column(name = "is_featured", nullable = false)
    private Boolean isFeatured;

    @Column(name = "view_count", nullable = false)
    private Long viewCount;

    @Column(name = "order_count", nullable = false)
    private Integer orderCount;

    @Column(name = "like_count", nullable = false)
    private Integer likeCount;

    @Column(name = "review_count", nullable = false)
    private Integer reviewCount;

    @Column(name = "avg_rating", nullable = false, precision = 3, scale = 2)
    private BigDecimal avgRating;

    @Builder
    public Product(String name, String description, Integer price, Integer discountPrice,
                   Integer stock, Long categoryId) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.discountPrice = discountPrice;
        this.stock = stock != null ? stock : 0;
        this.categoryId = categoryId;
        this.status = ProductStatus.SALE;
        this.isFeatured = false;
        this.viewCount = 0L;
        this.orderCount = 0;
        this.likeCount = 0;
        this.reviewCount = 0;
        this.avgRating = BigDecimal.ZERO;
    }

    public enum ProductStatus {
        SALE,           // 판매중
        SOLD_OUT,       // 품절
        DISCONTINUED    // 판매중단
    }

    public void increaseViewCount() {
        this.viewCount++;
    }

    public void increaseStock(int amount) {
        this.stock += amount;
    }

    public void decreaseStock(int amount) {
        if (this.stock < amount) {
            throw new IllegalArgumentException("재고가 부족합니다.");
        }
        this.stock -= amount;
        if (this.stock == 0) {
            this.status = ProductStatus.SOLD_OUT;
        }
    }

    public void updateStatus(ProductStatus status) {
        this.status = status;
    }

    public void feature() {
        this.isFeatured = true;
    }

    public void unfeature() {
        this.isFeatured = false;
    }

    public void addReview(int rating) {
        BigDecimal currentTotal = this.avgRating.multiply(BigDecimal.valueOf(this.reviewCount));
        this.reviewCount++;
        BigDecimal newTotal = currentTotal.add(BigDecimal.valueOf(rating));
        this.avgRating = newTotal.divide(BigDecimal.valueOf(this.reviewCount), 2, RoundingMode.HALF_UP);
    }

    public void increaseLikeCount() {
        this.likeCount++;
    }

    public void decreaseLikeCount() {
        if (this.likeCount > 0) {
            this.likeCount--;
        }
    }

    public void increaseOrderCount() {
        this.orderCount++;
    }
}
