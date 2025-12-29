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
    @Index(name = "idx_status", columnList = "status"),
    @Index(name = "idx_provider_provider_id", columnList = "provider, providerId")
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @Column // 소셜 로그인은 비밀번호가 없으므로 nullable
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

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private AuthProvider provider; // 인증 제공자 (LOCAL, GOOGLE, KAKAO, NAVER)

    @Column(length = 100)
    private String providerId; // 소셜 로그인 제공자의 사용자 ID

    @Builder
    public User(String email, String password, String name, String phone,
                AuthProvider provider, String providerId) {
        this.email = email;
        this.password = password;
        this.name = name;
        this.phone = phone;
        this.role = UserRole.USER;
        this.point = 0;
        this.status = UserStatus.ACTIVE;
        this.provider = provider != null ? provider : AuthProvider.LOCAL;
        this.providerId = providerId;
    }

    public enum UserRole {
        USER, ADMIN
    }

    public enum UserStatus {
        ACTIVE,     // 활성 - 정상 계정
        LOCKED,     // 잠김 - 일시 정지 (복구 가능)
        WITHDRAWN   // 탈퇴 - 회원 탈퇴 (소프트 삭제)
    }

    public enum AuthProvider {
        LOCAL,   // 일반 회원가입
        GOOGLE,  // 구글 소셜 로그인
        KAKAO,   // 카카오 소셜 로그인
        NAVER    // 네이버 소셜 로그인
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
