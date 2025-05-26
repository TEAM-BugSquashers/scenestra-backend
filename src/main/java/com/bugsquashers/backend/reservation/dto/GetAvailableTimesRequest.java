package com.bugsquashers.backend.reservation.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class GetAvailableTimesRequest {
    @NotNull(message = "상영관 ID는 필수입니다.")
    @Schema(description = "상영관 ID", example = "1")
    private Integer theaterId;

    @NotBlank(message = "영화 ID는 필수입니다.")
    @Schema(description = "영화 ID", example = "19798147")
    private String movieId;

    @NotNull(message = "날짜는 필수입니다.")
    @Schema(description = "조회할 날짜", example = "2025-01-01")
    private LocalDate day;
}
