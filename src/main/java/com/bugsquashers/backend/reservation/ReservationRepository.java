package com.bugsquashers.backend.reservation;

import com.bugsquashers.backend.reservation.domain.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface ReservationRepository extends JpaRepository<Reservation, Integer> {
    /**
     * 특정 상영관에서 주어진 시간 범위 내에 시작되는 예약 목록을 조회합니다.
     *
     * @param theaterId     상영관 ID
     * @param startDateTime 조회 시작 시간
     * @param endDateTime   조회 종료 시간
     * @return 해당 조건에 맞는 예약 목록
     */
    List<Reservation> findByTheater_TheaterIdAndStartDateTimeBetween(int theaterId, LocalDateTime startDateTime, LocalDateTime endDateTime);
}
