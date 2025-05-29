package com.bugsquashers.backend.review.service;

import com.bugsquashers.backend.image.ImageService;
import com.bugsquashers.backend.reservation.domain.Reservation;
import com.bugsquashers.backend.review.domain.Review;
import com.bugsquashers.backend.review.domain.ReviewImage;
import com.bugsquashers.backend.review.dto.ReviewRequest;
import com.bugsquashers.backend.review.dto.ReviewResponse;
import com.bugsquashers.backend.review.repository.ReviewImageRepository;
import com.bugsquashers.backend.review.repository.ReviewRepository;
import com.bugsquashers.backend.user.domain.User;
import com.bugsquashers.backend.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReviewService {
    private final ReviewRepository reviewRepository;
    private final ReviewImageRepository reviewImageRepository;
    private final UserRepository userRepository;
    private final ImageService imageService;

    private ReviewResponse toDto(Review review) {
        ReviewResponse reviewResponse = new ReviewResponse();
        reviewResponse.setContent(review.getContent());
        reviewResponse.setStar(review.getStar());
        reviewResponse.setTitle(review.getTitle());
        reviewResponse.setReservationId(review.getReservation() != null ? review.getReservation().getReservationId() : null);
        List<ReviewImage> images = reviewImageRepository.findByReview(review);
        List<String> imageUrls = images.stream()
                .map(ReviewImage::getImageUrl)
                .collect(Collectors.toList());
        reviewResponse.setImageUrls(imageUrls);
        return reviewResponse;
    }

    // 글 쓰기
    public ReviewResponse createReview(ReviewRequest reviewRequest) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        // 유저의 최근 예약 한 건 조회
        Reservation reservation = reviewRepository
                .findTop1ByUserOrderByRegDateDesc(user)
                .orElseThrow(() -> new RuntimeException("최근 예약 내역이 없습니다."));

        Review review = new Review();
        review.setContent(reviewRequest.getContent());
        review.setStar(reviewRequest.getStar());
        review.setTitle(reviewRequest.getTitle());
        review.setRegDate(LocalDateTime.now());
        review.setViewCount(0);
        review.setReservation(reservation);

        reviewRepository.save(review);

        if (reviewRequest.getImages() != null && !reviewRequest.getImages().isEmpty()) {
            for (MultipartFile file : reviewRequest.getImages()) {
                String imageUrl = imageService.saveImage(file);

                ReviewImage reviewImage = new ReviewImage();
                reviewImage.setReview(review);
                reviewImage.setImageUrl(imageUrl);
                reviewImage.setRegDate(LocalDateTime.now());

                reviewImageRepository.save(reviewImage);
            }
        }
        return toDto(review);
    }

    // 전체 리뷰 목록
    @Transactional
    public List<ReviewResponse> getAllReview() {
        List<Review> reviews = reviewRepository.findAll();
        return reviews.stream().map(this::toDto).toList();
    }

    // 상세 조회
    @Transactional
    public ReviewResponse getReviewById(Integer reviewId) {
        Review review = reviewRepository.findById(reviewId).orElseThrow(() -> new IllegalArgumentException("리뷰 없음"));
        if (review.getReservation() == null || review.getReservation().getTheater() == null) {
            throw new IllegalStateException("예약 또는 상영관 정보 없음");
        }
        // 조회수 증가
        review.setViewCount(review.getViewCount() + 1);
        return toDto(review);
    }


    // 상영관 별 리뷰 목록
    @Transactional
    public List<ReviewResponse> getReviewByTheaterId(Integer theaterId) {
        List<Review> reviews = reviewRepository.findByReservation_Theater_TheaterId(theaterId);
        return reviews.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

}
