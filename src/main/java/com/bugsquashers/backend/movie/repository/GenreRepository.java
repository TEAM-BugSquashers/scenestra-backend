package com.bugsquashers.backend.movie.repository;

import com.bugsquashers.backend.movie.domain.Genre;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface GenreRepository extends JpaRepository<Genre, Integer> {

    Optional<Genre> findByGenreId(Integer id);
}
