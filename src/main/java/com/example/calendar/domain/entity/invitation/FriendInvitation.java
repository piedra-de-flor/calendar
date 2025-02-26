package com.example.calendar.domain.entity.invitation;

import com.example.calendar.domain.entity.member.Member;
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
    public FriendInvitation(Member sender, Member receiver) {
        super(sender, receiver);
    }
}
