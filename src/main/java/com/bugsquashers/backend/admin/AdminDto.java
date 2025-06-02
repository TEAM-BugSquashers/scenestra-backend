package com.bugsquashers.backend.admin;

import com.bugsquashers.backend.reservation.domain.Reservation;
import com.bugsquashers.backend.reservation.domain.ReservationStatus;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminDto {
    // 예약 정보
    private Integer reservationId;
    private LocalDate date;
    private LocalTime startTime;
    private LocalTime endTime;
    private int timeUnit;
    private int numPeople;
    private LocalDateTime regDate;
    private ReservationStatus status;
    private String statusString;
    private int totalPrice;

    // 영화 정보
    private String movieId;
    private String movieTitle;

    // 상영관 정보
    private Integer theaterId;
    private String theaterName;

    // 사용자 정보
    private Long userId;
    private String username;
    private String mobile;

    //리뷰 작성 여부
    private Boolean isReviewed;

    public static AdminDto from(Reservation reservation) {
        return from(reservation, false);
    }

    public static AdminDto from(Reservation reservation, boolean isReviewed) {
        return AdminDto.builder()
                .reservationId(reservation.getReservationId())
                .date(reservation.getStartDateTime().toLocalDate())
                .startTime(reservation.getStartDateTime().toLocalTime())
                .endTime(reservation.getStartDateTime().toLocalTime().plusMinutes((reservation.getTimeUnit() * 30L)))
                .timeUnit(reservation.getTimeUnit())
                .numPeople(reservation.getNumPeople())
                .regDate(reservation.getRegDate())
                .status(reservation.getStatus())
                .statusString(reservation.getStatus().getDescription())
                .totalPrice(reservation.getTotalPrice())
                //
                .movieId(reservation.getMovie().getMovieId())
                .movieTitle(reservation.getMovie().getTitle())
                .theaterId(reservation.getTheater().getTheaterId())
                .theaterName(reservation.getTheater().getName())
                //
                .userId(reservation.getUser().getUserId())
                .username(reservation.getUser().getRealName())
                .mobile(reservation.getUser().getMobile())
                //
                .isReviewed(isReviewed)
                //
                .build();
    }
}
