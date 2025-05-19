package com.bugsquashers.backend.user.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode
public class UserGenreId implements Serializable {

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "genre_id")
    private Integer genreId;

    public UserGenreId(Long userId, Integer genreId) {
        this.userId = userId;
        this.genreId = genreId;
    }
}