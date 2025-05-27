package com.bugsquashers.backend.reservation;

import com.bugsquashers.backend.reservation.domain.Reservation;
import com.bugsquashers.backend.reservation.domain.ReservationStatus;
import com.bugsquashers.backend.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface ReservationRepository extends JpaRepository<Reservation, Integer> {
    List<Reservation> findByTheater_TheaterIdAndStartDateTimeBetweenAndStatusNot(int theaterId, LocalDateTime startDateTime, LocalDateTime endDateTime, ReservationStatus status);

    List<Reservation> findByUser(User user);
}
