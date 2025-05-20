package com.bugsquashers.backend.user.repository;

import com.bugsquashers.backend.user.domain.RefreshToken;
import com.bugsquashers.backend.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, String> {
    void deleteByUser(User user);

    Optional<RefreshToken> findTopByUserOrderByIdDesc(User user);

    void deleteByToken(String refreshToken);

}
