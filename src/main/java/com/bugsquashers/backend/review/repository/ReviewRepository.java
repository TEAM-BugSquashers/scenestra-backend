package com.bugsquashers.backend.review.repository;

import com.bugsquashers.backend.review.domain.Review;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Integer> {

    // 상영관 별 리뷰 목록
    List<Review> findByReservation_Theater_TheaterId(Integer theaterId);
}
