package com.bugsquashers.backend.user.domain;

import com.bugsquashers.backend.movie.domain.Genre;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;


@Entity
@Getter
@Setter
@NoArgsConstructor
public class UserGenre {

    @EmbeddedId
    private UserGenreId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("userId")
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("genreId")
    @JoinColumn(name = "genre_id")
    private Genre genre;

    @CreationTimestamp
    private LocalDateTime addedDate;

    public UserGenre(User user, Genre genre) {
        this.user = user;
        this.genre = genre;
        this.id = new UserGenreId(user.getUserId(), genre.getGenreId());
    }
}