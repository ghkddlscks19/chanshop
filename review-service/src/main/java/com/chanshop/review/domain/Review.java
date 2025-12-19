package com.chanshop.review.domain;

import com.chanshop.common.domain.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "reviews", indexes = {
    @Index(name = "idx_product_id", columnList = "product_id"),
    @Index(name = "idx_user_id", columnList = "user_id"),
    @Index(name = "idx_created_at", columnList = "created_at")
}, uniqueConstraints = {
    @UniqueConstraint(name = "uk_order_item", columnNames = "order_item_id")
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Review extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "product_id", nullable = false)
    private Long productId;

    @Column(name = "order_item_id", nullable = false, unique = true)
    private Long orderItemId;

    @Column(nullable = false)
    private Integer rating;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Builder
    public Review(Long userId, Long productId, Long orderItemId, Integer rating, String content) {
        validateRating(rating);
        this.userId = userId;
        this.productId = productId;
        this.orderItemId = orderItemId;
        this.rating = rating;
        this.content = content;
    }

    private void validateRating(Integer rating) {
        if (rating == null || rating < 1 || rating > 5) {
            throw new IllegalArgumentException("평점은 1~5 사이의 값이어야 합니다.");
        }
    }

    public void updateContent(String content, Integer rating) {
        validateRating(rating);
        this.content = content;
        this.rating = rating;
    }
}
