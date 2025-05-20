package com.bugsquashers.backend.user.service;

import com.bugsquashers.backend.user.domain.User;
import com.bugsquashers.backend.user.dto.UserJoinRequest;
import com.bugsquashers.backend.user.dto.UserJoinResponse;
import com.bugsquashers.backend.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public User getUserById(Long id) {
        return userRepository.findById(id).orElseThrow(() -> new UsernameNotFoundException("유저를 찾을 수 없습니다!"));
    }

    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("유저를 찾을 수 없습니다!"));
    }

    public UserJoinResponse userJoin(UserJoinRequest reqDto) {
        validateDuplicateUsername(reqDto.getUsername());
        validateDuplicateEmail(reqDto.getEmail());

        User user = User.builder()
                .username(reqDto.getUsername())
                .email(reqDto.getEmail())
                .password(passwordEncoder.encode(reqDto.getPassword()))
                .realName(reqDto.getRealName())
                .mobile(reqDto.getMobile())
                .enabled(true)
                .isAdmin(false)
                .build();

        userRepository.save(user);

        return new UserJoinResponse(user.getUserId(), user.getUsername(), user.getEmail());
    }

    public void validateDuplicateUsername(String username) {
        if (userRepository.existsByUsername(username)) {
            throw new IllegalArgumentException("이미 가입된 계정명 입니다.");
        }
    }

    public void validateDuplicateEmail(String email) {
        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("이미 가입된 이메일 입니다.");
        }
    }
}
