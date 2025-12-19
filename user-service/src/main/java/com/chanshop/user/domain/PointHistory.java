package com.chanshop.user.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "point_histories", indexes = {
    @Index(name = "idx_user_id", columnList = "user_id"),
    @Index(name = "idx_created_at", columnList = "created_at")
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PointHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(nullable = false)
    private Integer amount; // 양수: 적립/충전, 음수: 사용

    @Column(nullable = false)
    private Integer balance; // 변경 후 잔액

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private PointType type;

    @Column(length = 255)
    private String description;

    @Column(name = "order_id")
    private Long orderId; // 관련 주문 (있으면)

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Builder
    public PointHistory(Long userId, Integer amount, Integer balance,
                        PointType type, String description, Long orderId) {
        this.userId = userId;
        this.amount = amount;
        this.balance = balance;
        this.type = type;
        this.description = description;
        this.orderId = orderId;
    }

    public enum PointType {
        SIGNUP,   // 회원가입 축하 포인트
        CHARGE,   // 충전 (관리자 지급 등)
        EARN,     // 적립 (구매로 획득)
        USE,      // 사용 (주문 결제)
        REFUND,   // 환불 (주문 취소)
        EVENT     // 이벤트 지급
    }
}
