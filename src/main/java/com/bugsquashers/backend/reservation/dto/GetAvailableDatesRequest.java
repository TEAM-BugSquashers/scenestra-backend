package com.bugsquashers.backend.reservation.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.YearMonth;

@Data
public class GetAvailableDatesRequest {
    @NotNull(message = "상영관 ID는 필수입니다.")
    @Schema(description = "상영관 ID", example = "1")
    private Integer theaterId;

    @NotBlank(message = "영화 ID는 필수입니다.")
    @Schema(description = "영화 ID", example = "19798147")
    private String movieId;

    @NotNull(message = "년월은 필수입니다.")
    @Schema(description = "조회할 년월", example = "2025-01")
    private YearMonth yearMonth;
}
