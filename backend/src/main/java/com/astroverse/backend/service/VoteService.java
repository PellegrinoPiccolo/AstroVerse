package com.astroverse.backend.service;

import com.astroverse.backend.model.Vote;
import com.astroverse.backend.repository.VoteRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class VoteService {
    private final VoteRepository voteRepository;

    public VoteService(VoteRepository voteRepository) {
        this.voteRepository = voteRepository;
    }

    public Vote saveVote(Vote vote) {
        return voteRepository.save(vote);
    }

    public Optional<Vote> existVote(long userId, long spaceId) {
        return voteRepository.findByUser_IdAndPost_Id(userId, spaceId);
    }

    public void updateVote(long voteId, boolean newVote) {
        Optional<Vote> optionalVote = voteRepository.findById(voteId);
        if (optionalVote.isPresent()) {
            Vote vote = optionalVote.get();
            vote.setVote(newVote);
            voteRepository.save(vote);
        }
    }

    public void deleteVote(long voteId) {
        Optional<Vote> optionalVote = voteRepository.findById(voteId);
        if (optionalVote.isPresent()) {
            voteRepository.deleteById(voteId);
        }
    }
}