package com.bugsquashers.backend.reservation.dto;

import com.bugsquashers.backend.reservation.domain.Reservation;
import com.bugsquashers.backend.reservation.domain.ReservationStatus;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
public class ReservationDetailsResponse {
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

    public ReservationDetailsResponse(Reservation reservation) {

        this.reservationId = reservation.getReservationId();
        this.date = reservation.getStartDateTime().toLocalDate();
        this.startTime = reservation.getStartDateTime().toLocalTime();
        // endTime = startTime + (timeUnit * 30분)
        this.endTime = reservation.getStartDateTime().toLocalTime().plusMinutes(reservation.getTimeUnit() * 30L);
        this.timeUnit = reservation.getTimeUnit();
        this.numPeople = reservation.getNumPeople();
        this.regDate = reservation.getRegDate();
        this.status = reservation.getStatus();
        this.statusString = reservation.getStatus().getDescription();

        this.movieId = reservation.getMovie().getMovieId();
        this.movieTitle = reservation.getMovie().getTitle();

        this.theaterId = reservation.getTheater().getTheaterId();
        this.theaterName = reservation.getTheater().getName();

        this.userId = reservation.getUser().getUserId();
        this.username = reservation.getUser().getUsername();
        this.mobile = reservation.getUser().getMobile();
    }
} 