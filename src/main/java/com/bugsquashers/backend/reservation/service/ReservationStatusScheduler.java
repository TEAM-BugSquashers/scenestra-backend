package com.bugsquashers.backend.reservation.service;

import com.bugsquashers.backend.reservation.ReservationRepository;
import com.bugsquashers.backend.reservation.domain.Reservation;
import com.bugsquashers.backend.reservation.domain.ReservationStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReservationStatusScheduler {

    private final ReservationRepository reservationRepository;

    /**
     * 매 10분마다 예약 상태를 확인하여 상영이 완료된 예약들을 COMPLETED 상태로 변경합니다.
     * startDateTime + (timeUnit * 30분)이 현재 시간보다 이전인 CONFIRMED 예약들을 대상으로 합니다.
     */
    @Scheduled(fixedRate = 600000) // 10분마다 실행
    @Transactional
    public void updateExpiredReservations() {
        try {
            LocalDateTime currentTime = LocalDateTime.now();
            log.info("예약 상태 업데이트 스케줄러 실행 시작 - 현재 시간: {}", currentTime);

            // CONFIRMED 상태인 모든 예약 조회
            List<Reservation> confirmedReservations = reservationRepository.findByStatus(ReservationStatus.CONFIRMED);

            int updatedCount = 0;
            for (Reservation reservation : confirmedReservations) {
                LocalDateTime endTime = reservation.getStartDateTime().plusMinutes(reservation.getTimeUnit() * 30L);

                // 현재 시간이 종료 시간을 지났으면 상태 변경
                if (currentTime.isAfter(endTime) || currentTime.isEqual(endTime)) {
                    reservation.setStatus(ReservationStatus.COMPLETED);
                    reservationRepository.save(reservation);
                    updatedCount++;

                    log.info("예약 ID {} 상태를 COMPLETED로 변경 - 시작: {}, 종료: {}",
                            reservation.getReservationId(),
                            reservation.getStartDateTime(),
                            endTime);
                }
            }

            if (updatedCount == 0) {
                log.info("상태 업데이트가 필요한 예약이 없습니다.");
            } else {
                log.info("총 {}개의 예약 상태를 CONFIRMED에서 COMPLETED로 업데이트 완료", updatedCount);
            }

        } catch (Exception e) {
            log.error("예약 상태 업데이트 중 오류 발생", e);
        }
    }
} 