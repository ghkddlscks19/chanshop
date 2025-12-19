package com.chanshop.cart.domain;

import com.chanshop.common.domain.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "cart_items", indexes = {
    @Index(name = "idx_cart_id", columnList = "cart_id"),
    @Index(name = "idx_product_id", columnList = "product_id")
}, uniqueConstraints = {
    @UniqueConstraint(name = "uk_cart_product", columnNames = {"cart_id", "product_id"})
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CartItem extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "cart_id", nullable = false)
    private Long cartId;

    @Column(name = "product_id", nullable = false)
    private Long productId;

    @Column(nullable = false)
    private Integer quantity;

    @Builder
    public CartItem(Long cartId, Long productId, Integer quantity) {
        validateQuantity(quantity);
        this.cartId = cartId;
        this.productId = productId;
        this.quantity = quantity;
    }

    private void validateQuantity(Integer quantity) {
        if (quantity == null || quantity < 1) {
            throw new IllegalArgumentException("수량은 1개 이상이어야 합니다.");
        }
    }

    public void updateQuantity(int quantity) {
        validateQuantity(quantity);
        this.quantity = quantity;
    }

    public void increaseQuantity(int amount) {
        this.quantity += amount;
    }

    public void decreaseQuantity(int amount) {
        int newQuantity = this.quantity - amount;
        if (newQuantity < 1) {
            throw new IllegalArgumentException("수량은 1개 이상이어야 합니다.");
        }
        this.quantity = newQuantity;
    }
}
