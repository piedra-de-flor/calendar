package com.example.calendar.domain.entity.vote;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
public class VoteOption {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String optionText;

    @ElementCollection(fetch = FetchType.LAZY)
    private Set<String> voters = new HashSet<>();

    @ManyToOne
    @JoinColumn(name = "vote_id")
    private Vote vote;

    @Builder
    public VoteOption(Vote vote, String optionText) {
        this.vote = vote;
        this.optionText = optionText;
    }

    public void castVote(String email) {
        this.voters.add(email);
    }

    public int getVoterNumber() {
        return voters.size();
    }

    public void setVote(Vote vote) {
        this.vote = vote;
    }
}
