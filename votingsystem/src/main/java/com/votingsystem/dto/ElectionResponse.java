package com.votingsystem.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.Map;

@Data
public class ElectionResponse {
    private Long id;
    private String title;
    private String description;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private boolean active;
    private Map<Long, String> candidates;
}
