package com.chanshop.user.service;

import com.chanshop.common.exception.DuplicateException;
import com.chanshop.common.exception.ErrorCode;
import com.chanshop.user.domain.User;
import com.chanshop.user.dto.request.RegisterRequest;
import com.chanshop.user.dto.response.UserResponse;
import com.chanshop.user.repository.UserRepository;
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

    @Transactional
    public UserResponse register(RegisterRequest registerRequest) {
        if (userRepository.existsByEmail(registerRequest.getEmail())) {
            throw new DuplicateException(ErrorCode.DUPLICATE_EMAIL);
        }
        User user = registerRequest.toEntity(bCryptPasswordEncoder);
        userRepository.save(user);

        return UserResponse.from(user);
    }

    public void login(String email, String password) {

    }

}
