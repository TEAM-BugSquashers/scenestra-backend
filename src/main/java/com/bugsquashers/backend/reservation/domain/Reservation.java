package com.bugsquashers.backend.reservation.domain;

import com.bugsquashers.backend.movie.domain.Movie;
import com.bugsquashers.backend.theater.domain.Theater;
import com.bugsquashers.backend.user.domain.User;
import jakarta.persistence.*;
import org.hibernate.annotations.CurrentTimestamp;

import java.time.LocalDateTime;

@Entity
public class Reservation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer reservationId;

    private LocalDateTime startDateTime;

    private int timeUnit;

    private int numPeople;

    @CurrentTimestamp
    private LocalDateTime regDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "theater_id")
    private Theater theater;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "movie_id")
    private Movie movie;
}
