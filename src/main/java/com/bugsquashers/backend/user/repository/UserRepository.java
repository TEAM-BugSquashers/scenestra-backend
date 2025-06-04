package com.bugsquashers.backend.user.repository;

import com.bugsquashers.backend.user.domain.User;
import com.bugsquashers.backend.user.dto.UserPreferredGenreResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);

    boolean existsByEmail(String email);

    boolean existsByUsername(String username);

    @Query("select new com.bugsquashers.backend.user.dto.UserPreferredGenreResponse(g.genreId, g.name)" +
            "from User u join u.userGenres ug join ug.genre g " +
            "where u.userId = :userId"
    )
    List<UserPreferredGenreResponse> findPreferredGenresByUserId(Long userId);

    boolean existsByMobile(String mobile);

    //
    Optional<User> findByEmailAndRealName(String email, String realName);

    Optional<User> findByUsernameAndRealName(String username, String realName);

    Optional<User> findByEmail(String email);

    Optional<User> findByRealName(String name);
}
