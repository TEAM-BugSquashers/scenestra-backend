package com.bugsquashers.backend.reservation.service;

import com.bugsquashers.backend.movie.domain.Movie;
import com.bugsquashers.backend.movie.service.MovieService;
import com.bugsquashers.backend.reservation.ReservationRepository;
import com.bugsquashers.backend.reservation.domain.Reservation;
import com.bugsquashers.backend.reservation.domain.ReservationStatus;
import com.bugsquashers.backend.reservation.dto.GetAvailableTimesInDayResponse;
import com.bugsquashers.backend.reservation.dto.ReservationDetailsResponse;
import com.bugsquashers.backend.theater.TheaterService;
import com.bugsquashers.backend.theater.domain.Theater;
import com.bugsquashers.backend.user.domain.User;
import com.bugsquashers.backend.user.service.UserService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.YearMonth;
import java.util.*;

@Service
@RequiredArgsConstructor
public class ReservationService {
    private final TheaterService theaterService;
    private final MovieService movieService;
    private final TimeSlotCalculationService timeSlotCalculationService;
    private final ReservationRepository reservationRepository;
    private final UserService userService;

    /*
      컨트롤러 대응 메서드
     */

    /**
     * 월별 상영관 예약가능일 목록 조회
     * 특정 상영관에서 특정 영화의 해당 월에 예약 가능한 날짜 목록을 조회합니다.
     */
    @Transactional(readOnly = true)
    public Map<LocalDate, Boolean> getAvailableDatesInMonth(String movieId, int theaterId, YearMonth yearMonth) {
        // 필요한 객체들을 미리 조회
        Movie movie = movieService.getMovieByMovieId(movieId);
        Theater theater = theaterService.getTheaterById(theaterId);

        // 공통 로직 호출
        return getAvailableDatesInMonthInternal(movie, theater, yearMonth);
    }

    /**
     * 특정 날짜에 예약 가능한 시간 목록 조회 (List<LocalDateTime> 반환)
     */
    @Transactional(readOnly = true)
    public List<LocalDateTime> getAvailableTimesInDay(int theaterId, String movieId, LocalDate date) {
        // 필요한 객체들을 미리 조회
        Movie movie = movieService.getMovieByMovieId(movieId);
        Theater theater = theaterService.getTheaterById(theaterId);

        // 공통 로직 호출
        return getAvailableTimesInDayInternal(theater, movie, date);
    }

    /**
     * 특정 날짜에 예약 가능한 시간 목록 조회 (DTO 반환)
     */
    @Transactional(readOnly = true)
    public GetAvailableTimesInDayResponse getAvailableTimesInDayWithDetails(int theaterId, String movieId, LocalDate date) {
        // 필요한 객체들을 미리 조회
        Movie movie = movieService.getMovieByMovieId(movieId);
        Theater theater = theaterService.getTheaterById(theaterId);

        // 공통 로직 호출
        List<LocalDateTime> availableTimes = getAvailableTimesInDayInternal(theater, movie, date);
        int timeUnit = timeSlotCalculationService.calculateRequiredTimeUnits(movie);

        // DTO 생성 및 반환
        GetAvailableTimesInDayResponse response = new GetAvailableTimesInDayResponse();
        response.setTimeUnit(timeUnit);
        response.setMovieName(movie.getTitle());
        response.setTheaterName(theater.getName());
        response.setAvailableTimes(availableTimes);

        return response;
    }

    /**
     * 예약 가능 여부 확인
     * 특정 영화, 상영관, 날짜, 시간에 대해 예약 가능 여부를 확인합니다.
     */
    @Transactional(readOnly = true)
    public void checkReservationAvailability(String movieId, Integer theaterId, LocalDate date, LocalTime time, Integer numPeople) {
        // 필요한 객체들을 미리 조회
        Movie movie = movieService.getMovieByMovieId(movieId);
        Theater theater = theaterService.getTheaterById(theaterId);

        // 공통 로직 호출
        checkReservationAvailabilityInternal(movie, theater, date, time, numPeople);
    }

    /**
     * 실제 예약을 진행합니다.
     */
    @Transactional
    public Reservation createReservation(String movieId, Integer theaterId, LocalDate date, LocalTime time, Integer numPeople, long userId) {
        // 필요한 객체들을 미리 조회
        Movie movie = movieService.getMovieByMovieId(movieId);
        Theater theater = theaterService.getTheaterById(theaterId);
        User user = userService.getUserById(userId);

        // 공통 로직 호출
        return createReservationInternal(movie, theater, date, time, numPeople, user);
    }

    /**
     * 예약 상세 조회 서비스(컨트롤러)
     * 예약 ID를 통해 예약의 상세 정보를 조회합니다.
     */
    @Transactional(readOnly = true)
    public ReservationDetailsResponse getReservationDetails(Integer reservationId, Long userId) {
        // 예약 ID로 예약 정보 조회
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new EntityNotFoundException("해당하는 예약을 찾을 수 없습니다."));

        // 예약한 사용자의 ID와 요청한 사용자의 ID가 일치하는지 확인
        if (!reservation.getUser().getUserId().equals(userId)) {
            throw new AccessDeniedException("해당 예약은 요청한 사용자의 예약이 아닙니다.");
        }

        return new ReservationDetailsResponse(reservation);
    }



    /*
     공통사용 서비스 메서드(core business logic)
    */

    /**
     * 월별 상영관 예약가능일 목록 조회 (내부 공통 로직)
     */
    private Map<LocalDate, Boolean> getAvailableDatesInMonthInternal(Movie movie, Theater theater, YearMonth yearMonth) {
        LocalDate today = LocalDate.now();
        LocalDate tomorrow = today.plusDays(1);
        LocalDate maxBookingDate = today.plusMonths(1);

        // 해당 월에 시작일과 종료일 계산
        LocalDate firstDay = yearMonth.atDay(1);
        LocalDate lastDay = yearMonth.atEndOfMonth();

        // 예약 불가한 월인지 확인 (예약 가능한 날이 하나도 없는 경우)
        if (lastDay.isBefore(tomorrow) || firstDay.isAfter(maxBookingDate)) {
            throw new IllegalArgumentException("해당 월은 예약 가능한 날짜가 없습니다. 예약은 내일부터 한 달 이내만 가능합니다.");
        }

        // 영화의 상영 시간 단위 계산
        int requiredTimeUnits = timeSlotCalculationService.calculateRequiredTimeUnits(movie);

        // 상영관의 해당 월 예약 목록을 한 번에 조회
        List<Reservation> monthlyReservations = getReservationsForMonth(theater, firstDay, lastDay);

        // 예약 목록을 날짜별로 미리 분류
        Map<LocalDate, List<Reservation>> reservationsByDate = groupReservationsByDate(monthlyReservations);

        // 결과를 저장할 LinkedHashMap (순서 보장)
        Map<LocalDate, Boolean> availableDates = new LinkedHashMap<>();

        // 1일부터 마지막일까지 순서대로 처리
        LocalDate currentDate = firstDay;
        while (!currentDate.isAfter(lastDay)) { // 마지막날 포함
            boolean isAvailable = false;

            // 날짜 제한 검사: 내일 이후 && 한 달 이내만 예약 가능
            if (isDateInBookingRange(currentDate, tomorrow, maxBookingDate)) {
                // 현재 날짜의 예약 가능한 모든 시간 슬롯 생성
                List<LocalDateTime> oneDayUnitTimes = timeSlotCalculationService.generateAvailableTimeSlots(currentDate, requiredTimeUnits);

                // 현재 날짜의 예약 목록 가져오기 (없으면 빈 리스트)
                List<Reservation> todayReservations = reservationsByDate.getOrDefault(currentDate, new ArrayList<>());

                // 예약 가능한 시간이 있는지 확인
                isAvailable = !timeSlotCalculationService.filterAvailableSlots(oneDayUnitTimes, todayReservations, requiredTimeUnits).isEmpty();
            }
            // 날짜 제한에 걸리는 경우 isAvailable = false

            availableDates.put(currentDate, isAvailable);
            currentDate = currentDate.plusDays(1);
        }

        return availableDates;
    }

    /**
     * 특정 날짜에 예약 가능한 시간 목록 조회 (내부 공통 로직)
     */
    private List<LocalDateTime> getAvailableTimesInDayInternal(Theater theater, Movie movie, LocalDate date) {
        // 내일 이후 날짜만 && 한달까지만 예약 가능
        validateBookingDate(date);

        // 영화의 상영 시간 단위 계산
        int requiredTimeUnits = timeSlotCalculationService.calculateRequiredTimeUnits(movie);

        // 예약 가능한 모든 경우의 수 시간 슬롯 생성
        List<LocalDateTime> availableSlots = timeSlotCalculationService.generateAvailableTimeSlots(date, requiredTimeUnits);

        // 상영관의 해당일 예약 목록을 조회함
        List<Reservation> reservations = getReservationsForDate(theater, date);

        // 예약된 시간 슬롯을 제외한 예약 가능한 시간 슬롯을 필터링
        return timeSlotCalculationService.filterAvailableSlots(availableSlots, reservations, requiredTimeUnits);
    }

    /**
     * 예약 가능 여부 확인 (내부 공통 로직)
     */
    private void checkReservationAvailabilityInternal(Movie movie, Theater theater, LocalDate date, LocalTime time, Integer numPeople) {
        // 예약 가능 인원수 확인
        if (!isCapacityAvailable(theater, numPeople)) {
            throw new IllegalArgumentException("예약 인원 수가 상영관의 수용 인원을 초과합니다.");
        }

        // 날짜 유효성 검사 및 예약 가능 시간 조회
        List<LocalDateTime> availableTimes = getAvailableTimesInDayInternal(theater, movie, date);
        LocalDateTime requestedTime = date.atTime(time);

        // 요청한 시간대가 예약 가능한 시간대에 포함되는지 확인
        if (!availableTimes.contains(requestedTime)) {
            throw new IllegalArgumentException("요청한 날짜의 시간대는 예약 가능한 시간이 아닙니다.");
        }
    }

    /**
     * 예약 생성 (내부 공통 로직)
     */
    private Reservation createReservationInternal(Movie movie, Theater theater, LocalDate date, LocalTime time, Integer numPeople, User user) {
        // 예약 가능 여부 검증
        checkReservationAvailabilityInternal(movie, theater, date, time, numPeople);

        // 예약 객체 생성
        LocalDateTime startDateTime = date.atTime(time);
        int timeUnit = timeSlotCalculationService.calculateRequiredTimeUnits(movie);

        Reservation reservation = new Reservation(
                startDateTime,
                timeUnit,
                numPeople,
                movie,
                theater,
                user
        );

        // 예약 저장 및 반환
        return reservationRepository.save(reservation);
    }

    /**
     * 예약 목록을 날짜별로 그룹화
     */
    private Map<LocalDate, List<Reservation>> groupReservationsByDate(List<Reservation> reservations) {
        Map<LocalDate, List<Reservation>> reservationsByDate = new HashMap<>();
        for (Reservation reservation : reservations) {
            LocalDate reservationDate = reservation.getStartDateTime().toLocalDate();
            reservationsByDate.computeIfAbsent(reservationDate, k -> new ArrayList<>()).add(reservation);
        }
        return reservationsByDate;
    }

    /**
     * 월별 예약 목록 조회
     */
    private List<Reservation> getReservationsForMonth(Theater theater, LocalDate firstDay, LocalDate lastDay) {
        return reservationRepository.findByTheater_TheaterIdAndStartDateTimeBetweenAndStatusNot(
                theater.getTheaterId(),
                firstDay.atStartOfDay(),
                lastDay.atTime(23, 59, 59),
                ReservationStatus.CANCELLED
        );
    }

    /**
     * 특정 날짜의 예약 목록 조회
     */
    private List<Reservation> getReservationsForDate(Theater theater, LocalDate date) {
        return reservationRepository.findByTheater_TheaterIdAndStartDateTimeBetweenAndStatusNot(
                theater.getTheaterId(),
                date.atStartOfDay(),
                date.atTime(23, 59, 59),
                ReservationStatus.CANCELLED
        );
    }

    /**
     * 날짜가 예약 가능 범위 내에 있는지 확인
     */
    private boolean isDateInBookingRange(LocalDate date, LocalDate tomorrow, LocalDate maxBookingDate) {
        return !date.isBefore(tomorrow) && !date.isAfter(maxBookingDate);
    }

    /**
     * 예약 날짜 유효성 검사
     */
    private void validateBookingDate(LocalDate date) {
        LocalDate today = LocalDate.now();
        LocalDate tomorrow = today.plusDays(1);
        LocalDate maxBookingDate = today.plusMonths(1);

        if (date.isBefore(tomorrow) || date.isAfter(maxBookingDate)) {
            throw new IllegalArgumentException("예약은 내일부터 한 달 이내만 가능합니다.");
        }
    }

    /**
     * 상영관 수용 인원 확인
     */
    private boolean isCapacityAvailable(Theater theater, Integer numPeople) {
        return theaterService.isCapacityAvailable(theater.getTheaterId(), numPeople);
    }

}

