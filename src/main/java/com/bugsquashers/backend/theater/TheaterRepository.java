package com.bugsquashers.backend.theater;

import com.bugsquashers.backend.theater.domain.Theater;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TheaterRepository extends JpaRepository<Theater, Integer> {
    List<Theater> findAll();

    @Query("SELECT t FROM Theater t WHERE t.numPeople <= :num AND t.numPeopleMax >= :num")
    List<Theater> findTheatersForCapacity(@Param("num") int num);
}
