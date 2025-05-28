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

    public Theater getTheaterById(int theaterId) {
        return theaterRepository.findById(theaterId)
                .orElseThrow(() -> new EntityNotFoundException("해당 상영관을 찾을 수 없습니다: " + theaterId));
    }

    public List<Theater> findTheatersByCapacity(int num) {
        List<Theater> theaters = theaterRepository.findTheatersForCapacity(num);
        if (theaters.isEmpty()) {
            throw new EntityNotFoundException("상영관 정보가 없습니다.");
        }
        return theaters;
    }

    //상영관이 해당 인원을 수용 가능한지 확인
    public boolean isCapacityAvailable(int theaterId, int numPeople) {
        Theater theater = getTheaterById(theaterId);
        return theater.getNumPeople() <= numPeople && theater.getNumPeopleMax() >= numPeople;
    }
}
