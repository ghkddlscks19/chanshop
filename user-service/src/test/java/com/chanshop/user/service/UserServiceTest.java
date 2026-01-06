package com.chanshop.user.service;

import com.chanshop.common.exception.DuplicateException;
import com.chanshop.user.domain.User;
import com.chanshop.user.dto.request.RegisterRequest;
import com.chanshop.user.dto.response.UserResponse;
import com.chanshop.user.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
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
}