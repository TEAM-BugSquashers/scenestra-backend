package com.bugsquashers.backend.movie.repository;

import com.bugsquashers.backend.movie.domain.Movie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MovieRepository extends JpaRepository<Movie, String> {
    //전체 영화 찾기
    List<Movie> findAll();

    //장르별 영화 찾기
    @Query(
            value = """
        SELECT m.*
          FROM movie AS m
          JOIN movie_genre AS mg
            ON m.movie_id = mg.movie_id
          JOIN genre AS g
            ON mg.genre_id = g.genre_id
         WHERE g.name = :genreName
      """,
            nativeQuery = true
    )
    List<Movie> findAllByGenreName(@Param("genreName") String genreName);

    // NEW
    List<Movie> findAllByOrderByOpenDateDesc();



}
