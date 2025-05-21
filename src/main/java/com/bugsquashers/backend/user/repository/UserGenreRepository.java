package com.bugsquashers.backend.user.repository;

import com.bugsquashers.backend.user.domain.User;
import com.bugsquashers.backend.user.domain.UserGenre;
import com.bugsquashers.backend.user.domain.UserGenreId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserGenreRepository extends JpaRepository<UserGenre, UserGenreId> {
    void deleteByUser(User user);
} 