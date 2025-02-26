package com.example.calendar.domain.entity.invitation;

import com.example.calendar.domain.entity.member.Member;
import com.example.calendar.domain.vo.invitation.InvitationState;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "type")
@Getter
@Entity
public class Invitation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    @JoinColumn(name = "receiver_id")
    private Member receiver;

    @ManyToOne
    @JoinColumn(name = "sender_id")
    private Member sender;
    private LocalDate date;

    @Enumerated(EnumType.STRING)
    private InvitationState state = InvitationState.NOT_DECIDE;

    @Builder
    public Invitation(Member sender, Member receiver) {
        this.sender = sender;
        this.receiver = receiver;
        this.date = LocalDate.now();
    }

    public void denied() {
        this.state = InvitationState.DENIED;
    }

    public boolean isSentBy(Member member) {
        return this.sender.getId() == member.getId();
    }
}
