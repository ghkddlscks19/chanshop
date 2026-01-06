package com.chanshop.user.domain;

import com.chanshop.common.domain.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "users", indexes = {
    @Index(name = "idx_email", columnList = "email"),
    @Index(name = "idx_status", columnList = "status")
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false, length = 50)
    private String name;

    @Column(length = 20)
    private String phone;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private UserRole role;

    @Column(nullable = false)
    private Integer point;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private UserStatus status;

    @Builder
    public User(Long id, String email, String password, String name, String phone) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.name = name;
        this.phone = phone;
        this.role = UserRole.USER;
        this.point = 0;
        this.status = UserStatus.ACTIVE;
    }

    public enum UserRole {
        USER, ADMIN
    }

    public enum UserStatus {
        ACTIVE,     // 활성 - 정상 계정
        LOCKED,     // 잠김 - 일시 정지 (복구 가능)
        WITHDRAWN   // 탈퇴 - 회원 탈퇴 (소프트 삭제)
    }

    // 비즈니스 메서드
    public void addPoint(int amount) {
        this.point += amount;
    }

    public void deductPoint(int amount) {
        if (this.point < amount) {
            throw new IllegalArgumentException("포인트가 부족합니다.");
        }
        this.point -= amount;
    }

    public void lock() {
        this.status = UserStatus.LOCKED;
    }

    public void withdraw() {
        this.status = UserStatus.WITHDRAWN;
    }

    public void updateProfile(String name, String phone) {
        if (name != null && !name.isBlank()) {
            this.name = name;
        }
        if (phone != null && !phone.isBlank()) {
            this.phone = phone;
        }
    }
}
