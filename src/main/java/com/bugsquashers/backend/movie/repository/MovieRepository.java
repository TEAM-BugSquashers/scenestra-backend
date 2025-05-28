package com.bugsquashers.backend.movie.repository;

import com.bugsquashers.backend.movie.domain.Movie;
import lombok.NonNull;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface MovieRepository extends JpaRepository<Movie, String> {
    //전체 영화 찾기
    @NonNull
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
                        WHERE g.genre_id = :genreId
                    """,
            nativeQuery = true
    )
    List<Movie> findAllByGenreId(@Param("genreId") int genreId);

    // 장르별 영화 찾기(20개씩)
    @Query(
            value = """
                    SELECT m.*
                        FROM movie m
                        JOIN movie_genre mg
                            ON m.movie_id = mg.movie_id
                        WHERE mg.genre_id = :genreId
                        ORDER BY m.num_audience DESC
                        LIMIT 20
                    """,
            nativeQuery = true
    )
    List<Movie> findTop20ByGenreId(@Param("genreId") Integer genreId);

    //장르별 영화 찾기(관객수 높은 순)
    @Query(
            value = """
                    SELECT m.*
                        FROM movie AS m
                        JOIN movie_genre AS mg
                            ON m.movie_id = mg.movie_id
                        JOIN genre AS g
                            ON mg.genre_id = g.genre_id
                        WHERE g.genre_id = :genreId
                            ORDER BY m.num_audience DESC
                    """,
            nativeQuery = true
    )
    List<Movie> findTopNByGenreId(@Param("genreId") int genreId, Pageable pageable);

    // 최신 영화(NEW)
    List<Movie> findAllByOrderByOpenDateDesc(Pageable pageable);

    // 관객수 많은 순서(Best)
    List<Movie> findAllByOrderByNumAudienceDesc(Pageable pageable);

    // 검색
    List<Movie> findByTitleContainingIgnoreCase(String keyword);

    // 영화 ID로 영화 찾기
    Optional<Movie> findByMovieId(String movieID);
}
