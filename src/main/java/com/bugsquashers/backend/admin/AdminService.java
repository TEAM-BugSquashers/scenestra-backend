package com.bugsquashers.backend.admin;

import com.bugsquashers.backend.reservation.ReservationRepository;
import com.bugsquashers.backend.reservation.domain.Reservation;
import com.bugsquashers.backend.reservation.dto.ReservationDetailsResponse;
import com.bugsquashers.backend.review.repository.ReviewRepository;
import com.bugsquashers.backend.user.domain.User;
import com.bugsquashers.backend.user.dto.UserInfoResponse;
import com.bugsquashers.backend.user.repository.UserRepository;
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
    private final UserRepository userRepository;

    /**
     * 전체 예약 목록 조회
     */
//    @Transactional(readOnly = true)
//    public List<AdminDto> getAllReservations() {
//        List<Reservation> reservations = reservationRepository.findAll();
//        return reservations.stream()
//                .map(reservation -> {
//                    boolean isReviewed = reviewRepository.existsByReservation(reservation);
//                    return AdminDto.from(reservation, isReviewed);
//                })
//                .toList();
//    }
//
//    @Transactional(readOnly = true)
//    public AdminDto getReservationDetailsById(Integer reservationId) {
//        Reservation reservation = reservationRepository.findById(reservationId)
//                .orElseThrow(() -> new EntityNotFoundException("해당 예약의 상세 정보를 조회할 수 없습니다."));
//
//        boolean isReviewed = reviewRepository.existsByReservation(reservation);
//        return AdminDto.from(reservation, isReviewed);
//    }
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

    @Transactional(readOnly = true)
    public ReservationDetailsResponse getReservationDetailsById(Integer reservationId) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new EntityNotFoundException("해당 예약의 상세 정보를 조회할 수 없습니다."));

        boolean isReviewed = reviewRepository.existsByReservation(reservation);
        return new ReservationDetailsResponse(reservation, isReviewed);
    }

    @Transactional(readOnly = true)
    public List<UserInfoResponse> getAllUsers() {
        return userRepository.findAll().stream()
                .map(user -> {
                    UserInfoResponse response = new UserInfoResponse();
                    response.setUserId(user.getUserId());
                    response.setUsername(user.getUsername());
                    response.setRealName(user.getRealName());
                    response.setEmail(user.getEmail());

                    return response;
                })
                .toList();
    }
}
