package com.votingsystem.dto;

import lombok.Data;

import java.util.Map;

@Data
public class ElectionResultResponse {
    private Long electionId;
    private String electionTitle;
    private Map<String, Long> results;
    private Long totalVotes;
}
