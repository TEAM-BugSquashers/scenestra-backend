package com.bugsquashers.backend.review.domain;

import com.bugsquashers.backend.reservation.domain.Reservation;
import com.bugsquashers.backend.user.domain.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CurrentTimestamp;

import java.time.LocalDateTime;

@Entity
@Setter
@Getter
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer reviewId;

    private String title;

    @Column(columnDefinition = "TEXT")
    private String content;

    @CurrentTimestamp
    @Column(name = "reg_date")
    private LocalDateTime regDate;

    private Integer star;

    private Integer viewCount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reservation_id")
    private Reservation reservation;
}
