package com.chanshop.user.controller;

import com.chanshop.common.dto.ApiResponse;
import com.chanshop.user.dto.request.LoginRequest;
import com.chanshop.user.dto.request.RegisterRequest;
import com.chanshop.user.dto.response.LoginResponse;
import com.chanshop.user.dto.response.UserResponse;
import com.chanshop.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "User", description = "사용자 API")
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @Operation(summary = "회원가입", description = "새로운 사용자를 등록합니다.")
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<UserResponse>> register(@RequestBody @Valid RegisterRequest registerRequest) {
        UserResponse userResponse = userService.register(registerRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(userResponse));
    }

    @Operation(summary = "로그인", description = "이메일과 비밀번호로 로그인합니다.")
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(
            @RequestBody @Valid LoginRequest loginRequest,
            HttpServletResponse response
    ) {
        LoginResponse loginResponse = userService.login(loginRequest);

        // RefreshToken을 HttpOnly 쿠키로 설정
        Cookie refreshTokenCookie = new Cookie("refreshToken", loginResponse.getRefreshToken());
        refreshTokenCookie.setHttpOnly(true);  // JavaScript 접근 차단
        refreshTokenCookie.setSecure(false);   // HTTPS only (개발환경에서는 false)
        refreshTokenCookie.setPath("/");       // 모든 경로에서 유효
        refreshTokenCookie.setMaxAge(14 * 24 * 60 * 60);  // 2주 (초 단위)
        // refreshTokenCookie.setSameSite("Strict");  // CSRF 방지 (Spring Boot 3.1+)

        response.addCookie(refreshTokenCookie);

        return ResponseEntity.ok(ApiResponse.success(loginResponse));
    }
}
