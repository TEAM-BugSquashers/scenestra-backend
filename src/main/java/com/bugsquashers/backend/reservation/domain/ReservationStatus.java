package com.bugsquashers.backend.reservation.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ReservationStatus {
    PENDING(1, "대기중"),
    CONFIRMED(2, "확정"),
    CANCELLED(3, "취소됨"),
    COMPLETED(4, "완료");

    private final int code;
    private final String description;
}
