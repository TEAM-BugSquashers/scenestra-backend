package com.bugsquashers.backend.user.domain;

import jakarta.persistence.*;

@Entity
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
}
