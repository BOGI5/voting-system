package com.votingsystem.controller;

import com.votingsystem.dto.ElectionCreateRequest;
import com.votingsystem.dto.ElectionResponse;
import com.votingsystem.dto.ElectionResultResponse;
import com.votingsystem.service.ElectionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/elections")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class AdminController {

    private final ElectionService electionService;

    @GetMapping
    public ResponseEntity<List<ElectionResponse>> getAllElections() {
        return ResponseEntity.ok(electionService.getAllElections());
    }

    @PostMapping
    public ResponseEntity<ElectionResponse> createElection(@Valid @RequestBody ElectionCreateRequest request) {
        return ResponseEntity.ok(electionService.createElection(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ElectionResponse> updateElection(
            @PathVariable Long id,
            @Valid @RequestBody ElectionCreateRequest request) {
        return ResponseEntity.ok(electionService.updateElection(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteElection(@PathVariable Long id) {
        electionService.deleteElection(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}/results")
    public ResponseEntity<ElectionResultResponse> getElectionResults(@PathVariable Long id) {
        return ResponseEntity.ok(electionService.getElectionResults(id));
    }
}