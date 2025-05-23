package com.bugsquashers.backend.reservation;

import com.bugsquashers.backend.theater.TheaterService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReservationService {
    private final TheaterService theaterService;


}
