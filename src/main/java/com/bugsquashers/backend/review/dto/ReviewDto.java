package com.bugsquashers.backend.review.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ReviewDto {
    private Integer reviewId;
    private String content;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss") // 또는
    private LocalDateTime reg_date;
    private Integer star;
    private String title;
    private Integer viewCount;
    private String userName;
    private Long userId;
    private Integer reservationId;
}
