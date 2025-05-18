package com.bugsquashers.backend.user.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "refresh_tokens")
public class RefreshToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, foreignKey = @ForeignKey(name = "fk_refresh_token_user"))
    private User user;

    @Column(nullable = false, unique = true)
    private String token;

    @Column(nullable = false)
    private LocalDateTime expiryDate;

    @CreationTimestamp
    @Column(nullable = false)
    private LocalDateTime createdAt;

    public RefreshToken(User user, String token, LocalDateTime expiryDate) {
        this.user = user;
        this.token = token;
        this.expiryDate = expiryDate;
    }
}
