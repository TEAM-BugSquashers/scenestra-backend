package com.bugsquashers.backend.review.repository;

import com.bugsquashers.backend.reservation.domain.Reservation;
import com.bugsquashers.backend.review.domain.Review;
import com.bugsquashers.backend.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ReviewRepository extends JpaRepository<Review, Integer> {

    // 상영관 별 리뷰 목록
    List<Review> findByReservation_Theater_TheaterId(Integer theaterId);

    // 유저 기준 가장 최근 예약 조회
    @Query("SELECT res FROM Reservation res WHERE res.user = :user ORDER BY res.regDate DESC")
    Optional<Reservation> findTop1ByUserOrderByRegDateDesc(User user);

    // 이미 해당 예약에 리뷰가 있는지 확인
    boolean existsByReservation(Reservation reservation);

    List<Review> findByUser(User user);
}
