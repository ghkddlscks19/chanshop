package com.chanshop.order.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "order_items", indexes = {
    @Index(name = "idx_order_id", columnList = "order_id"),
    @Index(name = "idx_product_id", columnList = "product_id")
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "order_id", nullable = false)
    private Long orderId;

    @Column(name = "product_id", nullable = false)
    private Long productId;

    // 스냅샷 (주문 당시 정보 저장)
    @Column(name = "product_name", nullable = false, length = 200)
    private String productName;

    @Column(name = "product_price", nullable = false)
    private Integer productPrice;

    @Column(name = "product_discount_price")
    private Integer productDiscountPrice;

    // 수량 및 금액
    @Column(nullable = false)
    private Integer quantity;

    @Column(name = "item_amount", nullable = false)
    private Integer itemAmount;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Builder
    public OrderItem(Long orderId, Long productId, String productName,
                     Integer productPrice, Integer productDiscountPrice, Integer quantity) {
        this.orderId = orderId;
        this.productId = productId;
        this.productName = productName;
        this.productPrice = productPrice;
        this.productDiscountPrice = productDiscountPrice;
        this.quantity = quantity;
        this.itemAmount = calculateItemAmount(productPrice, productDiscountPrice, quantity);
    }

    private Integer calculateItemAmount(Integer price, Integer discountPrice, Integer quantity) {
        int unitPrice = discountPrice != null ? discountPrice : price;
        return unitPrice * quantity;
    }
}
