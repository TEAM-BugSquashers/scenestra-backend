package com.bugsquashers.backend.theater;

import com.bugsquashers.backend.theater.domain.Theater;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TheaterService {
    private  final TheaterRepository theaterRepository;

    public List<Theater> getAllTheaters() {
        List<Theater> theaters = theaterRepository.findAll();
        if (theaters.isEmpty()) {
            throw new EntityNotFoundException("상영관 정보가 없습니다.");
        }
        return theaterRepository.findAll();
    }

    public List<Theater> findTheatersByCapacity(int num) {
        List<Theater> theaters = theaterRepository.findTheatersForCapacity(num);
        if (theaters.isEmpty()) {
            throw new EntityNotFoundException("상영관 정보가 없습니다.");
        }
        return theaters;
    }
}
