package com.votingsystem.service;

import com.votingsystem.dto.VoteRequest;
import com.votingsystem.model.Candidate;
import com.votingsystem.model.Election;
import com.votingsystem.model.Vote;
import com.votingsystem.repository.CandidateRepository;
import com.votingsystem.repository.ElectionRepository;
import com.votingsystem.repository.VoteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class VoteService {

    private final VoteRepository voteRepository;
    private final ElectionRepository electionRepository;
    private final CandidateRepository candidateRepository;

    @Transactional
    public void castVote(VoteRequest voteRequest) {
        String voterId = getCurrentUserId();

        Election election = electionRepository.findById(voteRequest.getElectionId())
                .orElseThrow(() -> new RuntimeException("Election not found"));

        LocalDateTime now = LocalDateTime.now();
        if (!election.isActive() || now.isBefore(election.getStartDate()) || now.isAfter(election.getEndDate())) {
            throw new RuntimeException("Election is not currently active");
        }

        if (voteRepository.existsByVoterIdAndElectionId(voterId, election.getId())) {
            throw new RuntimeException("You have already voted in this election");
        }

        Candidate candidate = candidateRepository.findById(voteRequest.getCandidateId())
                .orElseThrow(() -> new RuntimeException("Candidate not found"));

        if (!candidate.getElection().getId().equals(election.getId())) {
            throw new RuntimeException("Candidate does not belong to this election");
        }

        Vote vote = new Vote();
        vote.setVoterId(voterId);
        vote.setElection(election);
        vote.setCandidate(candidate);

        voteRepository.save(vote);
    }

    public boolean hasVoted(Long electionId) {
        String voterId = getCurrentUserId();
        return voteRepository.existsByVoterIdAndElectionId(voterId, electionId);
    }

    private String getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication instanceof JwtAuthenticationToken) {
            JwtAuthenticationToken jwtAuth = (JwtAuthenticationToken) authentication;
            return jwtAuth.getToken().getSubject();
        }
        throw new RuntimeException("User not authenticated");
    }
}