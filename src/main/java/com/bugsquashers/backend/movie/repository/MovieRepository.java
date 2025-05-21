package com.bugsquashers.backend.movie.repository;

import com.bugsquashers.backend.movie.domain.Movie;
import com.bugsquashers.backend.movie.domain.MovieGenre;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MovieRepository extends JpaRepository <Movie, String> {
    //전체 영화 찾기
    List<Movie> findAll();

    //장르 전체 목록 불러오기







}
