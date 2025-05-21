package com.bugsquashers.backend.movie.repository;

import com.bugsquashers.backend.movie.domain.Genre;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GenreRepository extends JpaRepository<Genre, Integer> {

}
