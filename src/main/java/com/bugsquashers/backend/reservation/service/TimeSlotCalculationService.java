package com.bugsquashers.backend.reservation.service;

import com.bugsquashers.backend.movie.domain.Movie;
import com.bugsquashers.backend.movie.repository.MovieRepository;
import com.bugsquashers.backend.reservation.domain.Reservation;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TimeSlotCalculationService {
    private final MovieRepository movieRepository;

    public static final double TIME_UNIT = 30.0; // 30분 단위
    public static final int MAX_TIME_UNITS = (int) (24 * 60 / TIME_UNIT); // 하루 최대 시간 단위 수
    public static final LocalTime OPENING_TIME = LocalTime.of(11, 0); // 영업 시작 시간
    public static final LocalTime CLOSING_TIME = LocalTime.of(23, 0); // 영업 종료 시간

    /**
     * 영화의 상영 시간에 따라 필요한 시간 단위를 계산합니다.
     *
     * @param movie 영화 객체
     * @return 필요한 시간 단위 수
     */
    public int calculateRequiredTimeUnits(Movie movie) {
        int duration = movie.getShowTime(); // 영화의 상영 시간 (분 단위)
        return (int) Math.ceil(duration / TIME_UNIT);
    }

    /**
     * 주어진 날짜에 예약 가능한 모든 경우의 수 시간 슬롯을 생성합니다.
     * 운영 시간 내에서 기본 시간 단위 간격의 모든 시작 가능 리스트를 반환합니다.
     *
     * @param timeUnits 영화 상영에 필요한 시간 단위 수(운영시간 안에 영화 상영이 가능한지 판단하기 위함)
     * @param date      예약 가능한 날짜
     * @return 해당 날짜의 예약 가능한 시간 슬롯 목록
     */
    public List<LocalDateTime> generateAvailableTimeSlots(LocalDate date, int timeUnits) {
        // 해당 날짜의 운영 시간 가져옴
        LocalDateTime startOfDay = date.atTime(OPENING_TIME);
        // 종료 시간은 운영 종료 시간에서 영화 상영에 필요한 시간 단위 수를 고려하여 계산
        LocalDateTime endOfDay = date.atTime(CLOSING_TIME).minusMinutes((long) timeUnits * 30);

        // 시작 시간과 종료 시간 사이의 모든 시간 슬롯 생성 및 반환
        return generateAllTimeSlots(startOfDay, endOfDay);
    }

    // 시작, 종료시간 사이의 모든 슬롯 생성
    public List<LocalDateTime> generateAllTimeSlots(LocalDateTime start, LocalDateTime end) {
        List<LocalDateTime> slots = new ArrayList<>();
        LocalDateTime currentTime = start;

        while (currentTime.isBefore(end.plusMinutes(1))) { // plusMinutes(1)은 end 시간을 포함하기 위함
            slots.add(currentTime);
            currentTime = currentTime.plusMinutes((long) TIME_UNIT);
        }

        return slots;
    }

    public List<LocalDateTime> generateAllTimeSlots(LocalDate date) {
        // 해당 날짜의 운영 시간 가져옴
        LocalDateTime startOfDay = date.atTime(OPENING_TIME);
        // 종료 시간은 운영 종료 시간에서 영화 상영에 필요한 시간 단위 수를 고려하여 계산
        LocalDateTime endOfDay = date.atTime(CLOSING_TIME);

        return generateAllTimeSlots(startOfDay, endOfDay);

    }

    /**
     * 주어진 시간 슬롯과 예약목록을 비교하여 예약 가능한 시간 슬롯을 필터링해 반환합니다.
     *
     * @param availableSlots    예약 가능한 모든 시간 경우의수 슬롯 목록
     * @param reservations      예약된 목록
     * @param requiredTimeUnits 영화 상영에 필요한 시간 단위 수
     * @return 필터링된 예약 가능한 시간 슬롯 목록
     */
    public List<LocalDateTime> filterAvailableSlots(List<LocalDateTime> availableSlots, List<Reservation> reservations, int requiredTimeUnits) {
        List<LocalDateTime> filteredSlots = new ArrayList<>(availableSlots);

        for (Reservation reservation : reservations) {
            LocalDateTime reservedStart = reservation.getStartDateTime();
            // 기존 예약 종료 시간 + 청소시간 30분
            LocalDateTime reservedEndWithCleanup = reservedStart.plusMinutes((long) (reservation.getTimeUnit() * TIME_UNIT) + 30);

            filteredSlots.removeIf(slot -> {
                // 새로운 예약의 종료 시간 + 청소시간 30분 계산
                LocalDateTime newReservationEndWithCleanup = slot.plusMinutes(requiredTimeUnits * (long) TIME_UNIT + 30);

                // 두 시간 구간이 겹치는지 확인 (둘 다 청소시간 포함)
                // 겹치지 않는 조건: 새 예약(청소시간 포함)이 기존 예약보다 완전히 이전에 끝나거나, 완전히 이후에 시작
                boolean noOverlap = newReservationEndWithCleanup.isBefore(reservedStart) ||
                        newReservationEndWithCleanup.equals(reservedStart) ||
                        slot.isAfter(reservedEndWithCleanup) ||
                        slot.equals(reservedEndWithCleanup);

                return !noOverlap;
            });
        }
        return filteredSlots;
    }

    /**
     * 주어진 시간이 기존 예약과 중복되는지 확인합니다.
     *
     * @param checkTime    확인하려는 시간
     * @param reservations 기존 예약 목록
     * @return 중복되면 true, 아니면 false
     */
    public boolean timeSlotOverlapping(LocalDateTime checkTime, List<Reservation> reservations) {
        for (Reservation reservation : reservations) {
            LocalDateTime reservedStart = reservation.getStartDateTime();
            // 기존 예약 종료 시간 + 청소시간 30분
            LocalDateTime reservedEndWithCleanup = reservedStart.plusMinutes((long) (reservation.getTimeUnit() * TIME_UNIT) + 30);

            // checkTime이 기존 예약 시간(청소시간 포함) 내에 있는지 확인
            // (reservedStart <= checkTime < reservedEndWithCleanup)
            if (!checkTime.isBefore(reservedStart) && checkTime.isBefore(reservedEndWithCleanup)) {
                return true; // 중복 발생
            }
        }
        return false; // 중복 없음
    }
}
