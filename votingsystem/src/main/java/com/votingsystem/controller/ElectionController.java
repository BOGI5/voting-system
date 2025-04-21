package com.votingsystem.controller;

import com.votingsystem.dto.ElectionResponse;
import com.votingsystem.dto.VoteRequest;
import com.votingsystem.service.ElectionService;
import com.votingsystem.service.VoteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/elections")
@RequiredArgsConstructor
public class ElectionController {

    private final ElectionService electionService;
    private final VoteService voteService;

    @GetMapping
    public ResponseEntity<List<ElectionResponse>> getAllActiveElections() {
        return ResponseEntity.ok(electionService.getActiveElections());
    }

    @GetMapping("/current")
    public ResponseEntity<List<ElectionResponse>> getCurrentElections() {
        return ResponseEntity.ok(electionService.getCurrentElections());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ElectionResponse> getElectionById(@PathVariable Long id) {
        return ResponseEntity.ok(electionService.getElectionById(id));
    }

    @PostMapping("/{id}/vote")
    @PreAuthorize("hasRole('VOTER')")
    public ResponseEntity<?> castVote(@PathVariable Long id, @Valid @RequestBody VoteRequest voteRequest) {
        if (!id.equals(voteRequest.getElectionId())) {
            return ResponseEntity.badRequest().body("Election ID mismatch");
        }

        voteService.castVote(voteRequest);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}/has-voted")
    @PreAuthorize("hasRole('VOTER')")
    public ResponseEntity<Boolean> hasVoted(@PathVariable Long id) {
        return ResponseEntity.ok(voteService.hasVoted(id));
    }
}