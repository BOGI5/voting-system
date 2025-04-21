package com.votingsystem.repository;

import com.votingsystem.model.Vote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VoteRepository extends JpaRepository<Vote, Long> {

    boolean existsByVoterIdAndElectionId(String voterId, Long electionId);

    @Query("SELECT v.candidate.id, COUNT(v) FROM Vote v WHERE v.election.id = ?1 GROUP BY v.candidate.id")
    List<Object[]> countVotesByCandidateForElection(Long electionId);

    List<Vote> findByElectionId(Long electionId);

    Optional<Vote> findByVoterIdAndElectionId(String voterId, Long electionId);
}