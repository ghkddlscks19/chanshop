package com.chanshop.user.service;

import com.chanshop.common.exception.DuplicateException;
import com.chanshop.common.exception.EntityNotFoundException;
import com.chanshop.common.exception.InvalidValueException;
import com.chanshop.user.domain.User;
import com.chanshop.user.dto.request.LoginRequest;
import com.chanshop.user.dto.request.RegisterRequest;
import com.chanshop.user.dto.response.LoginResponse;
import com.chanshop.user.dto.response.UserResponse;
import com.chanshop.user.repository.UserRepository;
import com.chanshop.user.util.JwtUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@SpringBootTest
class UserServiceTest {

    @Autowired
    private UserService userService;

    @MockitoBean
    private UserRepository userRepository;

    @MockitoBean
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @MockitoBean
    private JwtUtil jwtUtil;

    @Test
    @DisplayName("회원가입 성공")
    void register_success() {
        // given
        RegisterRequest registerRequest = RegisterRequest.builder()
                .email("test@example.com")
                .password("password123")
                .name("홍길동")
                .phone("01012345678")
                .build();

        User savedUser = User.builder()
                .id(1L)
                .email("test@example.com")
                .password("encodedPassword")
                .name("홍길동")
                .phone("01012345678")
                .build();

        given(userRepository.existsByEmail("test@example.com")).willReturn(false);
        given(bCryptPasswordEncoder.encode("password123")).willReturn("encodedPassword");
        given(userRepository.save(any(User.class))).willReturn(savedUser);

        // when
        UserResponse response = userService.register(registerRequest);

        // then
        assertThat(response).isNotNull();
        assertThat(response.getEmail()).isEqualTo("test@example.com");
        assertThat(response.getName()).isEqualTo("홍길동");
        assertThat(response.getPhone()).isEqualTo("01012345678");
        assertThat(response.getRole()).isEqualTo(User.UserRole.USER);
        assertThat(response.getPoint()).isEqualTo(0);
        assertThat(response.getStatus()).isEqualTo(User.UserStatus.ACTIVE);

        verify(userRepository).existsByEmail("test@example.com");
        verify(bCryptPasswordEncoder).encode("password123");
        verify(userRepository).save(any(User.class));
    }

    @Test
    @DisplayName("회원가입 실패 - 이메일 중복")
    void register_fail_duplicate_email() {
        // given
        RegisterRequest registerRequest = RegisterRequest.builder()
                .email("duplicate@example.com")
                .password("password123")
                .name("홍길동")
                .phone("01012345678")
                .build();

        given(userRepository.existsByEmail("duplicate@example.com")).willReturn(true);

        // when & then
        assertThatThrownBy(() -> userService.register(registerRequest))
                .isInstanceOf(DuplicateException.class);

        verify(userRepository).existsByEmail("duplicate@example.com");
        verify(bCryptPasswordEncoder, never()).encode(any());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("로그인 성공")
    void login_success() {
        // given
        LoginRequest loginRequest = LoginRequest.builder()
                .email("test@example.com")
                .password("password123")
                .build();

        User user = User.builder()
                .id(1L)
                .email("test@example.com")
                .password("encodedPassword")
                .name("홍길동")
                .phone("01012345678")
                .build();

        given(userRepository.findByEmail("test@example.com")).willReturn(Optional.of(user));
        given(bCryptPasswordEncoder.matches("password123", "encodedPassword")).willReturn(true);
        given(jwtUtil.generateAccessToken(anyString(), anyLong())).willReturn("accessToken");
        given(jwtUtil.generateRefreshToken(anyString(), anyLong())).willReturn("refreshToken");

        // when
        LoginResponse response = userService.login(loginRequest);

        // then
        assertThat(response).isNotNull();
        assertThat(response.getAccessToken()).isEqualTo("accessToken");
        assertThat(response.getRefreshToken()).isEqualTo("refreshToken");
        assertThat(response.getTokenType()).isEqualTo("Bearer");
        assertThat(response.getEmail()).isEqualTo("test@example.com");
        assertThat(response.getName()).isEqualTo("홍길동");
        assertThat(response.getRole()).isEqualTo(User.UserRole.USER);

        verify(userRepository).findByEmail("test@example.com");
        verify(bCryptPasswordEncoder).matches("password123", "encodedPassword");
        verify(jwtUtil).generateAccessToken(anyString(), anyLong());
        verify(jwtUtil).generateRefreshToken(anyString(), anyLong());
    }

    @Test
    @DisplayName("로그인 실패 - 존재하지 않는 이메일")
    void login_fail_user_not_found() {
        // given
        LoginRequest loginRequest = LoginRequest.builder()
                .email("notfound@example.com")
                .password("password123")
                .build();

        given(userRepository.findByEmail("notfound@example.com")).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> userService.login(loginRequest))
                .isInstanceOf(EntityNotFoundException.class);

        verify(userRepository).findByEmail("notfound@example.com");
        verify(bCryptPasswordEncoder, never()).matches(anyString(), anyString());
        verify(jwtUtil, never()).generateAccessToken(anyString(), anyLong());
        verify(jwtUtil, never()).generateRefreshToken(anyString(), anyLong());
    }

    @Test
    @DisplayName("로그인 실패 - 비밀번호 불일치")
    void login_fail_invalid_password() {
        // given
        LoginRequest loginRequest = LoginRequest.builder()
                .email("test@example.com")
                .password("wrongPassword")
                .build();

        User user = User.builder()
                .id(1L)
                .email("test@example.com")
                .password("encodedPassword")
                .name("홍길동")
                .phone("01012345678")
                .build();

        given(userRepository.findByEmail("test@example.com")).willReturn(Optional.of(user));
        given(bCryptPasswordEncoder.matches("wrongPassword", "encodedPassword")).willReturn(false);

        // when & then
        assertThatThrownBy(() -> userService.login(loginRequest))
                .isInstanceOf(InvalidValueException.class);

        verify(userRepository).findByEmail("test@example.com");
        verify(bCryptPasswordEncoder).matches("wrongPassword", "encodedPassword");
        verify(jwtUtil, never()).generateAccessToken(anyString(), anyLong());
        verify(jwtUtil, never()).generateRefreshToken(anyString(), anyLong());
    }
}