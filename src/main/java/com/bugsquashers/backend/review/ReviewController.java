package com.bugsquashers.backend.review;

import com.bugsquashers.backend.review.dto.ReviewListResponse;
import com.bugsquashers.backend.review.dto.ReviewRequest;
import com.bugsquashers.backend.review.dto.ReviewResponse;
import com.bugsquashers.backend.review.service.ReviewService;
import com.bugsquashers.backend.util.response.ApiResponse;
import com.bugsquashers.backend.util.response.SuccessStatus;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/review")
@RequiredArgsConstructor
public class ReviewController {
    private final ReviewService reviewService;

    // 글 쓰기
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<ReviewResponse> createReview(@ModelAttribute ReviewRequest reviewRequest) {
        return ApiResponse.onSuccess(SuccessStatus.OK, reviewService.createReview(reviewRequest)).getBody();
    }

    // 전체 리뷰 목록
    @GetMapping
    @Operation(summary = "전체 리뷰 목록(상영관 구별X)", description = "전체 리뷰 목록을 조회합니다.")
    public ResponseEntity<ApiResponse<Object>> getAllReviews() {
        return ApiResponse.onSuccess(SuccessStatus.OK, reviewService.getAllReview());
    }

    // 상세 조회
    @GetMapping("/{id}")
    @Operation(summary = "리뷰 하나 조회(reviewId 이용)", description = "리뷰 하나를 조회합니다.")
    public ResponseEntity<ApiResponse<Object>> getReviewById(@PathVariable Integer id) {
        return ApiResponse.onSuccess(SuccessStatus.OK, reviewService.getReviewById(id));
    }

    // 상영관 별 리뷰 목록
    @GetMapping("/theater/{theaterId}")
    @Operation(summary = "상영관 별 리뷰 목록(theaterId 이용)", description = "상영관 별 리뷰 목록을 조회합니다.")
    public List<ReviewListResponse> getReviewsByTheater(@PathVariable Integer theaterId) {
        return reviewService.getReviewByTheaterId(theaterId);
    }


}
