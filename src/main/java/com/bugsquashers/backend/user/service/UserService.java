package com.bugsquashers.backend.user.service;

import com.bugsquashers.backend.movie.domain.Genre;
import com.bugsquashers.backend.movie.repository.GenreRepository;
import com.bugsquashers.backend.user.domain.User;
import com.bugsquashers.backend.user.domain.UserGenre;
import com.bugsquashers.backend.user.dto.UserJoinRequest;
import com.bugsquashers.backend.user.dto.UserJoinResponse;
import com.bugsquashers.backend.user.dto.UserPreferredGenreResponse;
import com.bugsquashers.backend.user.repository.UserRepository;
import com.bugsquashers.backend.user.repository.UserGenreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

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
            throw new IllegalArgumentException("이미 가입된 계정명 입니다.");
        }
    }

    public void validateDuplicateEmail(String email) {
        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("이미 가입된 이메일 입니다.");
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
}
