package com.bugsquashers.backend.review.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ReviewListResponse {
    private Integer star;
    private String title;
    private Integer viewCount;
    private LocalDateTime regDate;
    private String username;
}
