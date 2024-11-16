package com.example.calendar.domain.entity.invitation;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
@DiscriminatorValue("FRIEND")
public class FriendInvitation extends Invitation {
    @Override
    protected void acceptHandle() {
        super.getReceiver().addFriends(super.getSender().getId());
        super.getSender().addFriends(super.getReceiver().getId());
    }
}
