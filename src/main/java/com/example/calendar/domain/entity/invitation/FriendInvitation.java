package com.example.calendar.domain.entity.invitation;

import com.example.calendar.domain.entity.member.Member;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
@DiscriminatorValue("FRIEND")
public class FriendInvitation extends Invitation {
    @Builder
    public FriendInvitation(Member receiver, Member sender) {
        super(receiver, sender);
    }

    @Override
    protected void acceptHandle() {
        super.getReceiver().addFriends(super.getSender().getId());
        super.getSender().addFriends(super.getReceiver().getId());
    }
}
