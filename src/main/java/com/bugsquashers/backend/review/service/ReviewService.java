package com.bugsquashers.backend.review.service;

import com.bugsquashers.backend.reservation.ReservationRepository;
import com.bugsquashers.backend.reservation.domain.Reservation;
import com.bugsquashers.backend.review.domain.Review;
import com.bugsquashers.backend.review.dto.ReviewDto;
import com.bugsquashers.backend.review.repository.ReviewRepository;
import com.bugsquashers.backend.user.domain.User;
import com.bugsquashers.backend.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReviewService {
    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;
    private final ReservationRepository reservationRepository;

    private ReviewDto toDto(Review review) {
        ReviewDto dto = new ReviewDto();
        dto.setReviewId(review.getReviewId());
        dto.setContent(review.getContent());
        dto.setReg_date(review.getReg_date());
        dto.setStar(review.getStar());
        dto.setTitle(review.getTitle());
        dto.setViewCount(review.getViewCount());
        dto.setReservationId(review.getReservation() != null ? review.getReservation().getReservationId() : null);
        dto.setUserName(review.getUser() != null ? review.getUser().getUsername() : null);
        dto.setUserId(review.getUser() != null ? review.getUser().getUserId() : null);
        return dto;
    }

    // 글 쓰기
    public ReviewDto createReview(ReviewDto dto) {
        User user = userRepository.findById(dto.getUserId()).orElseThrow();
        Reservation reservation = reservationRepository.findById(dto.getReservationId()).orElseThrow();;
        Review review = new Review();
        review.setContent(dto.getContent());
        review.setStar(dto.getStar());
        review.setTitle(dto.getTitle());
        review.setReg_date(LocalDateTime.now());
        review.setViewCount(0);
        review.setReservation(reservation);
        review.setUser(user);

        Review saved = reviewRepository.save(review);
        return toDto(saved);
    }

    // 전체 리뷰 목록
    @Transactional
    public List<ReviewDto> getAllReview() {
        List<Review> reviews = reviewRepository.findAll();
        return reviews.stream().map(this::toDto).toList();
    }

    // 상세 조회
    @Transactional
    public ReviewDto getReviewById(Integer reviewId) {
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
    public List<ReviewDto> getReviewByTheaterId(Integer theaterId) {
        List<Review> reviews = reviewRepository.findByReservation_Theater_TheaterId(theaterId);
        return reviews.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

}
