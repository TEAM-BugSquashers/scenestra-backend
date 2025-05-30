package com.bugsquashers.backend.review.service;

import com.bugsquashers.backend.image.ImageService;
import com.bugsquashers.backend.reservation.domain.Reservation;
import com.bugsquashers.backend.reservation.domain.ReservationStatus;
import com.bugsquashers.backend.reservation.service.ReservationService;
import com.bugsquashers.backend.review.domain.Review;
import com.bugsquashers.backend.review.domain.ReviewImage;
import com.bugsquashers.backend.review.dto.ReviewListResponse;
import com.bugsquashers.backend.review.dto.ReviewRequest;
import com.bugsquashers.backend.review.dto.ReviewResponse;
import com.bugsquashers.backend.review.repository.ReviewImageRepository;
import com.bugsquashers.backend.review.repository.ReviewRepository;
import com.bugsquashers.backend.user.domain.User;
import com.bugsquashers.backend.user.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
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
    private final ReservationService reservationService;

    // 리뷰를 클릭 했을 때 해당 화면에 들어갈 정보
    private ReviewResponse toDto(Review review) {
        ReviewResponse reviewResponse = new ReviewResponse();
        reviewResponse.setContent(review.getContent());
        reviewResponse.setStar(review.getStar());
        reviewResponse.setTitle(review.getTitle());
        reviewResponse.setRegDate(review.getRegDate());
        reviewResponse.setViewCount(review.getViewCount());
        reviewResponse.setReservationId(review.getReservation() != null ? review.getReservation().getReservationId() : null);

        List<ReviewImage> images = reviewImageRepository.findByReview(review);
        List<String> imageUrls = images.stream()
                .map(ReviewImage::getImageUrl)
                .collect(Collectors.toList());
        reviewResponse.setImageUrls(imageUrls);

        String username = null;
        if (review.getUser() != null) {
            username = review.getUser().getUsername();
        } else if (
                review.getReservation() != null &&
                review.getReservation().getUser() != null
        ) {
            username = review.getReservation().getUser().getUsername();
        }
        reviewResponse.setUsername(username);

        return reviewResponse;
    }

    // 상영관 클릭 시 해당 화면에 나타날 리뷰 목록 정보
    private ReviewListResponse toTDto(Review review) {
        ReviewListResponse reviewListResponse = new ReviewListResponse();
        reviewListResponse.setReviewId(review.getReviewId());
        reviewListResponse.setReservationId(review.getReservation() != null ? review.getReservation().getReservationId() : null);
        reviewListResponse.setStar(review.getStar());
        reviewListResponse.setTitle(review.getTitle());
        reviewListResponse.setRegDate(review.getRegDate());
        reviewListResponse.setViewCount(review.getViewCount());

        String username = null;
        if (review.getUser() != null) {
            username = review.getUser().getUsername();
        } else if (
                review.getReservation() != null &&
                review.getReservation().getUser() != null
        ) {
            username = review.getReservation().getUser().getUsername();
        }
        reviewListResponse.setUsername(username);

        return reviewListResponse;
    }

    // 글 쓰기
    public ReviewResponse createReview(ReviewRequest reviewRequest) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("사용자를 찾을 수 없습니다."));

        // 내가 예약한 예약 id 가져오기
        Reservation reservation = reservationService.getReservationById(reviewRequest.getReservationId());
        if (!reservation.getUser().getUserId().equals(user.getUserId())) {
            throw new AccessDeniedException("본인 예약이 아닙니다.");
        }

        // 이미 해당 예약에 리뷰가 있는지 확인
        if (reviewRepository.existsByReservation(reservation)) {
            throw new IllegalArgumentException("리뷰는 예약 당 하나만 작성 가능합니다.");
        }

        //  이용 완료한 예약에 대해서만 리뷰 남기도록
        if(!reservation.getStatus().equals(ReservationStatus.COMPLETED)) {
            throw new IllegalArgumentException("이용 완료한 예약에 대해서만 리뷰를 남길 수 있습니다.");
        }

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
    @Transactional(readOnly=true)
    public ReviewResponse getReviewById(Integer reviewId) {
        Review review = reviewRepository.findById(reviewId).orElseThrow(() -> new IllegalArgumentException("리뷰 없음"));
        if (review.getReservation() == null || review.getReservation().getTheater() == null) {
            throw new IllegalArgumentException("예약 또는 상영관 정보 없음");
        }
        // 조회수 증가
        review.setViewCount(review.getViewCount() + 1);
        return toDto(review);
    }


    // 상영관 별 리뷰 목록
    @Transactional(readOnly=true)
    public List<ReviewListResponse> getReviewByTheaterId(Integer theaterId) {
        List<Review> reviews = reviewRepository.findByReservation_Theater_TheaterId(theaterId);
        return reviews.stream()
                .map(this::toTDto)
                .collect(Collectors.toList());
    }

}
