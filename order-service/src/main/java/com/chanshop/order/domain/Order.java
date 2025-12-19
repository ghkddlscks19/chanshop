package com.chanshop.order.domain;

import com.chanshop.common.domain.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "orders", indexes = {
    @Index(name = "idx_user_id", columnList = "user_id"),
    @Index(name = "idx_status", columnList = "status"),
    @Index(name = "idx_created_at", columnList = "created_at"),
    @Index(name = "idx_order_number", columnList = "order_number")
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Order extends BaseTimeEntity {

    @Id
    private Long id; // Snowflake ID

    @Column(name = "order_number", nullable = false, unique = true, length = 30)
    private String orderNumber;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    // 금액
    @Column(name = "total_amount", nullable = false)
    private Integer totalAmount;

    @Column(name = "discount_amount", nullable = false)
    private Integer discountAmount;

    @Column(name = "delivery_fee", nullable = false)
    private Integer deliveryFee;

    @Column(name = "final_amount", nullable = false)
    private Integer finalAmount;

    // 포인트
    @Column(name = "used_point", nullable = false)
    private Integer usedPoint;

    @Column(name = "earned_point", nullable = false)
    private Integer earnedPoint;

    // 배송 정보
    @Column(name = "recipient_name", nullable = false, length = 50)
    private String recipientName;

    @Column(name = "recipient_phone", nullable = false, length = 20)
    private String recipientPhone;

    @Column(name = "delivery_address", nullable = false)
    private String deliveryAddress;

    @Column(name = "delivery_address_detail")
    private String deliveryAddressDetail;

    @Column(name = "delivery_memo")
    private String deliveryMemo;

    // 상태
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private OrderStatus status;

    @Builder
    public Order(Long id, String orderNumber, Long userId,
                 Integer totalAmount, Integer discountAmount, Integer usedPoint,
                 String recipientName, String recipientPhone,
                 String deliveryAddress, String deliveryAddressDetail, String deliveryMemo) {
        this.id = id;
        this.orderNumber = orderNumber;
        this.userId = userId;
        this.totalAmount = totalAmount;
        this.discountAmount = discountAmount != null ? discountAmount : 0;
        this.deliveryFee = calculateDeliveryFee(totalAmount, discountAmount);
        this.usedPoint = usedPoint != null ? usedPoint : 0;
        this.finalAmount = totalAmount - this.discountAmount - this.usedPoint + this.deliveryFee;
        this.earnedPoint = calculateEarnedPoint(this.finalAmount);
        this.recipientName = recipientName;
        this.recipientPhone = recipientPhone;
        this.deliveryAddress = deliveryAddress;
        this.deliveryAddressDetail = deliveryAddressDetail;
        this.deliveryMemo = deliveryMemo;
        this.status = OrderStatus.PENDING;
    }

    public enum OrderStatus {
        PENDING,    // 결제 대기
        PAID,       // 결제 완료
        PREPARING,  // 상품 준비중
        SHIPPING,   // 배송중
        DELIVERED,  // 배송 완료
        CANCELLED,  // 취소
        REFUNDED    // 환불
    }

    private Integer calculateDeliveryFee(Integer total, Integer discount) {
        int finalPrice = total - (discount != null ? discount : 0);
        return finalPrice >= 50000 ? 0 : 3000; // 5만원 이상 무료배송
    }

    private Integer calculateEarnedPoint(Integer finalAmount) {
        return (int) (finalAmount * 0.01); // 1% 적립
    }

    public void updateStatus(OrderStatus status) {
        this.status = status;
    }

    public void cancel() {
        if (this.status == OrderStatus.DELIVERED) {
            throw new IllegalStateException("배송 완료된 주문은 취소할 수 없습니다.");
        }
        this.status = OrderStatus.CANCELLED;
    }

    public void refund() {
        if (this.status != OrderStatus.DELIVERED) {
            throw new IllegalStateException("배송 완료된 주문만 환불 가능합니다.");
        }
        this.status = OrderStatus.REFUNDED;
    }
}
