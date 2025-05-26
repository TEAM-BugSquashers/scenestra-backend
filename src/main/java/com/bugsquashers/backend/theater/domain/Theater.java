package com.bugsquashers.backend.theater.domain;

import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Getter
public class Theater {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer theaterId;

    @Column(nullable = false)
    private String name;

    private String image;

    private int numPeople;

    private int numPeopleMax;

    private String info;

    private int price;
}
