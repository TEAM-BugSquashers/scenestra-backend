package com.bugsquashers.backend.movie.repository;

import com.bugsquashers.backend.movie.domain.Genre;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface GenreRepository extends JpaRepository<Genre, Integer> {
//    @Query("SELECT g.name FROM Genre g")
//    List<String> findAllNames();
}
