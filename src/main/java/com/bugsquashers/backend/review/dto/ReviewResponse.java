package com.bugsquashers.backend.review.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ReviewResponse {
    private String content;
    private Integer star;
    private String title;
    private Integer reservationId;
    // 응답
    private List<String> imageUrls;
}
