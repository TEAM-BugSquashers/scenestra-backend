package com.bugsquashers.backend.reservation.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ReservationStatus {
    PENDING("대기중"),
    CONFIRMED("확정"),
    CANCELLED("취소"),
    COMPLETED("이용 완료");

    private final String description;
}
