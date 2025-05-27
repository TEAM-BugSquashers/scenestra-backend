package com.bugsquashers.backend.reservation.domain;

import com.bugsquashers.backend.movie.domain.Movie;
import com.bugsquashers.backend.theater.domain.Theater;
import com.bugsquashers.backend.user.domain.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.CurrentTimestamp;

import java.time.LocalDateTime;

@Entity
@NoArgsConstructor
@Getter
@ToString
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

    private ReservationStatus status;

    public Reservation(LocalDateTime startDateTime, int timeUnit, int numPeople, Movie movie, Theater theater, User user) {
        this.startDateTime = startDateTime;
        this.timeUnit = timeUnit;
        this.numPeople = numPeople;
        this.movie = movie;
        this.theater = theater;
        this.user = user;
        this.status = ReservationStatus.CONFIRMED;
    }
}
