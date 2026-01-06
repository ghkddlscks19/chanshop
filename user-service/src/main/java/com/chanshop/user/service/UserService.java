package com.chanshop.user.service;

import com.chanshop.common.exception.DuplicateException;
import com.chanshop.common.exception.EntityNotFoundException;
import com.chanshop.common.exception.ErrorCode;
import com.chanshop.common.exception.InvalidValueException;
import com.chanshop.user.domain.User;
import com.chanshop.user.dto.request.LoginRequest;
import com.chanshop.user.dto.request.RegisterRequest;
import com.chanshop.user.dto.response.LoginResponse;
import com.chanshop.user.dto.response.UserResponse;
import com.chanshop.user.repository.UserRepository;
import com.chanshop.user.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final JwtUtil jwtUtil;

    @Transactional
    public UserResponse register(RegisterRequest registerRequest) {
        if (userRepository.existsByEmail(registerRequest.getEmail())) {
            throw new DuplicateException(ErrorCode.DUPLICATE_EMAIL);
        }
        User user = registerRequest.toEntity(bCryptPasswordEncoder);
        userRepository.save(user);

        return UserResponse.from(user);
    }

    public LoginResponse login(LoginRequest loginRequest) {
        // 1. 이메일로 사용자 조회
        User user = userRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.USER_NOT_FOUND));

        // 2. 비밀번호 검증
        if (!bCryptPasswordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            throw new InvalidValueException(ErrorCode.INVALID_PASSWORD);
        }

        // 3. Access Token & Refresh Token 생성
        String accessToken = jwtUtil.generateAccessToken(user.getEmail(), user.getId());
        String refreshToken = jwtUtil.generateRefreshToken(user.getEmail(), user.getId());

        // 4. LoginResponse 반환
        return LoginResponse.of(accessToken, refreshToken, user);
    }

}
