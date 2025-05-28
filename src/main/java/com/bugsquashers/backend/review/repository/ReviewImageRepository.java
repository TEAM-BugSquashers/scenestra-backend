package com.bugsquashers.backend.review.repository;

import com.bugsquashers.backend.review.domain.Review;
import com.bugsquashers.backend.review.domain.ReviewImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReviewImageRepository extends JpaRepository<ReviewImage, Integer> {
    List<ReviewImage> findByReview(Review review);
}
