package com.bugsquashers.backend.reservation.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class GetAvailableTimesInDayResponse {
    private int timeUnit;
    private String movieName;
    private String theaterName;
    private List<LocalDateTime> availableTimes;
}
