package com.bugsquashers.backend.reservation;

import com.bugsquashers.backend.reservation.domain.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface ReservationRepository extends JpaRepository<Reservation, Integer> {
    List<Reservation> findByTheater_TheaterIdAndStartDateTimeBetween(int theaterId, LocalDateTime startDateTime, LocalDateTime endDateTime);
}
