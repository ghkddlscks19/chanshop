package com.chanshop.user.controller;

import com.chanshop.common.dto.ApiResponse;
import com.chanshop.user.dto.request.RegisterRequest;
import com.chanshop.user.dto.response.UserResponse;
import com.chanshop.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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
}
