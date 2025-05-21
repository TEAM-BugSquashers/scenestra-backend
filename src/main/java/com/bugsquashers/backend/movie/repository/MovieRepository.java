package com.bugsquashers.backend.movie.repository;

import com.bugsquashers.backend.movie.domain.Movie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MovieRepository extends JpaRepository <Movie, String> {
    //전체 영화 찾기
    List<Movie> findAll();

    //장르별 영화 찾기
    @Query("SELECT mg.movie FROM MovieGenre mg WHERE mg.genre.genreId = :genreId")
    List<Movie> findMoviesByGenreId(@Param("genreId") Integer genreId);





}
