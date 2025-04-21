package com.votingsystem.service;

import com.votingsystem.dto.ElectionCreateRequest;
import com.votingsystem.dto.ElectionResponse;
import com.votingsystem.dto.ElectionResultResponse;
import com.votingsystem.model.Candidate;
import com.votingsystem.model.Election;
import com.votingsystem.repository.CandidateRepository;
import com.votingsystem.repository.ElectionRepository;
import com.votingsystem.repository.VoteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ElectionService {

    private final ElectionRepository electionRepository;
    private final CandidateRepository candidateRepository;
    private final VoteRepository voteRepository;

    public List<ElectionResponse> getAllElections() {
        return electionRepository.findAll().stream()
                .map(this::mapToElectionResponse)
                .collect(Collectors.toList());
    }

    public List<ElectionResponse> getActiveElections() {
        return electionRepository.findByActiveTrue().stream()
                .map(this::mapToElectionResponse)
                .collect(Collectors.toList());
    }

    public List<ElectionResponse> getCurrentElections() {
        return electionRepository.findCurrentlyActiveElections(LocalDateTime.now()).stream()
                .map(this::mapToElectionResponse)
                .collect(Collectors.toList());
    }

    public ElectionResponse getElectionById(Long id) {
        Election election = electionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Election not found with id: " + id));
        return mapToElectionResponse(election);
    }

    @Transactional
    public ElectionResponse createElection(ElectionCreateRequest request) {
        Election election = new Election();
        election.setTitle(request.getTitle());
        election.setDescription(request.getDescription());
        election.setStartDate(request.getStartDate());
        election.setEndDate(request.getEndDate());
        election.setActive(request.isActive());

        Election savedElection = electionRepository.save(election);

        if (request.getCandidates() != null && !request.getCandidates().isEmpty()) {
            request.getCandidates().forEach(candidateName -> {
                Candidate candidate = new Candidate();
                candidate.setName(candidateName);
                candidate.setElection(savedElection);
                candidateRepository.save(candidate);
            });
        }

        return mapToElectionResponse(electionRepository.findById(savedElection.getId()).get());
    }

    @Transactional
    public ElectionResponse updateElection(Long id, ElectionCreateRequest request) {
        Election election = electionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Election not found with id: " + id));

        election.setTitle(request.getTitle());
        election.setDescription(request.getDescription());
        election.setStartDate(request.getStartDate());
        election.setEndDate(request.getEndDate());
        election.setActive(request.isActive());

        Election savedElection = electionRepository.save(election);

        List<Candidate> existingCandidates = candidateRepository.findByElectionId(id);
        candidateRepository.deleteAll(existingCandidates);

        if (request.getCandidates() != null && !request.getCandidates().isEmpty()) {
            request.getCandidates().forEach(candidateName -> {
                Candidate candidate = new Candidate();
                candidate.setName(candidateName);
                candidate.setElection(savedElection);
                candidateRepository.save(candidate);
            });
        }

        return mapToElectionResponse(electionRepository.findById(savedElection.getId()).get());
    }

    @Transactional
    public void deleteElection(Long id) {
        if (!electionRepository.existsById(id)) {
            throw new RuntimeException("Election not found with id: " + id);
        }
        electionRepository.deleteById(id);
    }

    public ElectionResultResponse getElectionResults(Long electionId) {
        Election election = electionRepository.findById(electionId)
                .orElseThrow(() -> new RuntimeException("Election not found with id: " + electionId));

        List<Object[]> voteCounts = voteRepository.countVotesByCandidateForElection(electionId);
        Map<Long, Long> candidateVotes = new HashMap<>();

        for (Object[] result : voteCounts) {
            Long candidateId = (Long) result[0];
            Long voteCount = (Long) result[1];
            candidateVotes.put(candidateId, voteCount);
        }

        List<Candidate> candidates = candidateRepository.findByElectionId(electionId);

        ElectionResultResponse response = new ElectionResultResponse();
        response.setElectionId(electionId);
        response.setElectionTitle(election.getTitle());

        Map<String, Long> results = new HashMap<>();
        candidates.forEach(candidate -> {
            results.put(candidate.getName(), candidateVotes.getOrDefault(candidate.getId(), 0L));
        });

        response.setResults(results);
        response.setTotalVotes(results.values().stream().mapToLong(Long::longValue).sum());

        return response;
    }

    private ElectionResponse mapToElectionResponse(Election election) {
        ElectionResponse response = new ElectionResponse();
        response.setId(election.getId());
        response.setTitle(election.getTitle());
        response.setDescription(election.getDescription());
        response.setStartDate(election.getStartDate());
        response.setEndDate(election.getEndDate());
        response.setActive(election.isActive());

        List<Candidate> candidates = candidateRepository.findByElectionId(election.getId());
        response.setCandidates(candidates.stream()
                .collect(Collectors.toMap(Candidate::getId, Candidate::getName)));

        return response;
    }
}