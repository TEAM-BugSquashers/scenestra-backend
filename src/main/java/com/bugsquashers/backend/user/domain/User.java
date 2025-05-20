package com.bugsquashers.backend.user.domain;

import com.bugsquashers.backend.movie.domain.Genre;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String password;

    private String realName;

    @Column(unique = true)
    private String email;

    @Column(unique = true)
    private String mobile;

    private Boolean enabled;

    private Boolean isAdmin;

    @CreationTimestamp
    private LocalDateTime regDate;

    @OneToMany(
            mappedBy = "user",
            cascade = {CascadeType.PERSIST, CascadeType.MERGE},
            fetch = FetchType.LAZY,
            orphanRemoval = true
    )
    private List<UserGenre> userGenres = new ArrayList<>();

    public void addGenre(Genre genre) {
        UserGenre userGenre = new UserGenre(this, genre);
        userGenres.add(userGenre);
    }

    public void removeGenre(Genre genre) {
        userGenres.removeIf(userGenre -> userGenre.getGenre().equals(genre));
    }
}
