package com.bugsquashers.backend.admin;

import com.bugsquashers.backend.reservation.ReservationRepository;
import com.bugsquashers.backend.reservation.domain.Reservation;
import com.bugsquashers.backend.reservation.dto.ReservationDetailsResponse;
import com.bugsquashers.backend.review.repository.ReviewRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminService {
    private final ReservationRepository reservationRepository;
    private final ReviewRepository reviewRepository;

    /**
     * 전체 예약 목록 조회
     */
    @Transactional(readOnly = true)
    public List<ReservationDetailsResponse> getAllReservations() {
        List<Reservation> reservations = reservationRepository.findAll();
        return reservations.stream()
                .map(reservation -> {
                    boolean isReviewed = reviewRepository.existsByReservation(reservation);
                    return new ReservationDetailsResponse(reservation, isReviewed);
                })
                .toList();
    }

    /**
     * 예약 상세 조회
     */
    @Transactional(readOnly = true)
    public ReservationDetailsResponse getReservationDetailsById(Integer reservationId) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new EntityNotFoundException("해당 예약의 상세 정보를 조회할 수 없습니다."));

        boolean isReviewed = reviewRepository.existsByReservation(reservation);
        return new ReservationDetailsResponse(reservation, isReviewed);
    }
}
