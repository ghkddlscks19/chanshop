package com.chanshop.wishlist.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "wishlists", indexes = {
    @Index(name = "idx_user_id", columnList = "user_id"),
    @Index(name = "idx_product_id", columnList = "product_id")
}, uniqueConstraints = {
    @UniqueConstraint(name = "uk_user_product", columnNames = {"user_id", "product_id"})
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Wishlist {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "product_id", nullable = false)
    private Long productId;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Builder
    public Wishlist(Long userId, Long productId) {
        this.userId = userId;
        this.productId = productId;
    }
}
