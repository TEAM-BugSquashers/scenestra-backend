package com.bugsquashers.backend.user.service;

import com.bugsquashers.backend.movie.domain.Genre;
import com.bugsquashers.backend.movie.repository.GenreRepository;
import com.bugsquashers.backend.user.domain.User;
import com.bugsquashers.backend.user.domain.UserGenre;
import com.bugsquashers.backend.user.dto.*;
import com.bugsquashers.backend.user.repository.UserGenreRepository;
import com.bugsquashers.backend.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final GenreRepository genreRepository;
    private final UserGenreRepository userGenreRepository;

    public User getUserById(Long id) {
        return userRepository.findById(id).orElseThrow(() -> new UsernameNotFoundException("유저를 찾을 수 없습니다!"));
    }

    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("유저를 찾을 수 없습니다!"));
    }

    @Transactional
    public UserJoinResponse userJoin(UserJoinRequest reqDto) {
        validateDuplicateUsername(reqDto.getUsername());
        validateDuplicateEmail(reqDto.getEmail());
        validateDuplicateMobile(reqDto.getMobile());

        User user = User.builder()
                .username(reqDto.getUsername())
                .email(reqDto.getEmail())
                .password(passwordEncoder.encode(reqDto.getPassword()))
                .realName(reqDto.getRealName())
                .mobile(reqDto.getMobile())
                .enabled(true)
                .isAdmin(false)
                .build();

        if (reqDto.getGenres() != null && !reqDto.getGenres().isEmpty()) {
            reqDto.getGenres().forEach(genreId -> {
                Genre genre = genreRepository.findByGenreId(genreId)
                        .orElseThrow(() -> new IllegalArgumentException("ID에 해당하는 장르를 찾을 수 없습니다: " + genreId));
                user.addGenre(genre);
            });
        }
        userRepository.save(user);
        return new UserJoinResponse(user.getUserId(), user.getUsername(), user.getEmail());
    }

    public void validateDuplicateUsername(String username) {
        if (userRepository.existsByUsername(username)) {
            throw new IllegalArgumentException("이미 가입된 아이디 입니다.");
        }
    }

    public void validateDuplicateEmail(String email) {
        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("이미 가입된 이메일 입니다.");
        }
    }

    public void validateDuplicateMobile(String mobile) {
        if (userRepository.existsByMobile(mobile)) {
            throw new IllegalArgumentException("이미 가입된 휴대폰 번호 입니다.");
        }
    }

    @Transactional(readOnly = true)
    public List<UserPreferredGenreResponse> userPreferredGenres(Long userId) {
        return userRepository.findPreferredGenresByUserId(userId);
    }

    @Transactional
    public void updateUserPreferredGenres(Long userId, Set<Integer> genreIds) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("유저를 찾을 수 없습니다!"));

        userGenreRepository.deleteByUser(user);

        if (genreIds != null && !genreIds.isEmpty()) {
            List<UserGenre> newUserGenresList = new ArrayList<>();
            for (Integer genreId : genreIds) {
                Genre genre = genreRepository.findByGenreId(genreId)
                        .orElseThrow(() -> new IllegalArgumentException("ID에 해당하는 장르를 찾을 수 없습니다: " + genreId));
                newUserGenresList.add(new UserGenre(user, genre));
            }
            userGenreRepository.saveAll(newUserGenresList);
        }
    }

    public UserInfoResponse getUserInfo(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("유저를 찾을 수 없습니다!"));

        List<UserInfoResponse.UserGenreInfo> userGenreInfos = user.getUserGenres().stream()
                .map(userGenre -> new UserInfoResponse.UserGenreInfo(
                        userGenre.getGenre().getGenreId(),
                        userGenre.getGenre().getName()
                ))
                .collect(Collectors.toList());

        return new UserInfoResponse(
                user.getUserId(),
                user.getUsername(),
                user.getRealName(),
                user.getEmail(),
                user.getMobile(),
                user.getEnabled(),
                user.getIsAdmin(),
                user.getRegDate(),
                userGenreInfos
        );
    }

    @Transactional
    public void updateUserInfo(Long userId, UserInfoUpdateRequest reqDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("유저를 찾을 수 없습니다!"));

        if (!user.getEmail().equals(reqDto.getEmail())) {
            validateDuplicateEmail(reqDto.getEmail());
        }

        if (!user.getMobile().equals(reqDto.getMobile())) {
            validateDuplicateMobile(reqDto.getMobile());
        }

        user.setEmail(reqDto.getEmail());
        user.setMobile(reqDto.getMobile());

        userRepository.save(user);
    }

    @Transactional
    public void updatePassword(User user, String newPassword) {
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    @Transactional
    public void updatePassword(Long userId, UserPasswordUpdateRequest reqDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("유저를 찾을 수 없습니다!"));

        if (!passwordEncoder.matches(reqDto.getOldPassword(), user.getPassword())) {
            throw new IllegalArgumentException("현재 비밀번호가 틀렸습니다.");
        }

        updatePassword(user, reqDto.getNewPassword());
    }

    //
    @Transactional(readOnly = true)
    public Optional<String> getUsernameByEmail(String email, String name) {
        boolean byEmail = userRepository.existsByEmail(email);
        boolean byName = userRepository.existsByRealName(name);

        if (!byEmail && !byName) {
            throw new UsernameNotFoundException("유저를 찾을 수 없습니다.");
        }

        Optional<User> exactMatch = userRepository.findByEmailAndRealName(email, name);
        if (exactMatch.isEmpty()) {
            throw new UsernameNotFoundException ("입력한 정보가 일치하지 않습니다.");
        }

        return Optional.of(exactMatch.get().getUsername());
    }

    @Transactional(readOnly = true)
    public String sendPassword(String username, String name) {
        boolean byUsername = userRepository.existsByUsername(username);
        boolean byName = userRepository.existsByRealName(name);

        if (!byUsername && !byName) {
            throw new UsernameNotFoundException("유저를 찾을 수 없습니다.");
        }

        Optional<User> exactMatch = userRepository.findByUsernameAndRealName(username, name);
        if (exactMatch.isEmpty()) {
            throw new UsernameNotFoundException ("입력한 정보가 일치하지 않습니다.");
        }

        return "회원님의 이메일로 임시 비밀번호를 전송했습니다.";
    }
}
