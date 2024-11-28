package com.example.calendar.service.group;

import com.example.calendar.domain.entity.group.Team;
import com.example.calendar.domain.entity.group.Teaming;
import com.example.calendar.domain.entity.member.Member;
import com.example.calendar.dto.group.TeamAddFriendDto;
import com.example.calendar.dto.group.TeamCreateDto;
import com.example.calendar.dto.group.TeamDto;
import com.example.calendar.dto.invitation.TeamInvitationDto;
import com.example.calendar.dto.member.FriendDto;
import com.example.calendar.repository.MemberRepository;
import com.example.calendar.repository.TeamRepository;
import com.example.calendar.repository.TeamingRepository;
import com.example.calendar.service.invitation.InvitationFacadeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

@RequiredArgsConstructor
@Slf4j
@Service
public class TeamService {
    private final TeamRepository teamRepository;
    private final TeamingRepository teamingRepository;
    private final MemberRepository memberRepository;
    private final InvitationFacadeService invitationFacadeService;

    @Transactional
    public boolean createTeam(String memberEmail, TeamCreateDto createDto) {
        Member member = memberRepository.findByEmail(memberEmail)
                .orElseThrow(NoSuchElementException::new);

        if (member.getTeamsNames().contains(createDto.name())) {
            throw new IllegalArgumentException("group name is duplicated");
        }

        Team team = Team.builder()
                .name(createDto.name())
                .build();

        Teaming teaming = Teaming.builder()
                .member(member)
                .team(team)
                .build();

        member.addTeam(teaming);
        teamingRepository.save(teaming);

        for (long friendId : createDto.friends()) {
            Member friend = memberRepository.findById(friendId)
                    .orElseThrow(NoSuchElementException::new);

            invitationFacadeService.sendTeamInvitation(memberEmail, new TeamInvitationDto(friend.getEmail(), team.getId()));
        }

        teamRepository.save(team);

        return true;
    }

    @Transactional
    public FriendDto addFriend(String memberEmail, TeamAddFriendDto addFriendDto) {
        Member sender = memberRepository.findByEmail(memberEmail)
                .orElseThrow(NoSuchElementException::new);

        Team team = teamRepository.findById(addFriendDto.teamId())
                .orElseThrow(NoSuchElementException::new);

        Teaming teaming = teamingRepository.findByMemberAndTeam(sender, team)
                .orElseThrow(() -> new NoSuchElementException("find teaming error, there is no teaming"));

        Member friend = memberRepository.findByEmail(addFriendDto.friendEmail())
                .orElseThrow(NoSuchElementException::new);

        invitationFacadeService.sendTeamInvitation(memberEmail, new TeamInvitationDto(friend.getEmail(), team.getId()));

        return new FriendDto(friend.getId(), friend.getEmail(), friend.getName());
    }

    public List<FriendDto> readAllMemberInTeam(long groupId) {
        List<FriendDto> response = new ArrayList<>();

        Team team = teamRepository.findById(groupId)
                .orElseThrow(NoSuchElementException::new);

        for (Teaming teaming : team.getTeamings()) {
            Member teamMember = teaming.getMember();
            response.add(new FriendDto(teamMember.getId(), teamMember.getEmail(), teamMember.getName()));
        }

        return response;
    }

    public List<TeamDto> readAllMyTeams(String memberEmail) {
        List<TeamDto> response = new ArrayList<>();

        Member member = memberRepository.findByEmail(memberEmail)
                .orElseThrow(NoSuchElementException::new);

        for (Teaming teaming : member.getTeamings()) {
            Team myTeam = teaming.getTeam();
            response.add(new TeamDto(myTeam.getId(), myTeam.getName()));
        }

        return response;
    }

    @Transactional
    public boolean exitTeam(String memberEmail, long groupId) {
        Member member = memberRepository.findByEmail(memberEmail)
                .orElseThrow(NoSuchElementException::new);

        Team team = teamRepository.findById(groupId)
                .orElseThrow(NoSuchElementException::new);

        Teaming teaming = teamingRepository.findByMemberAndTeam(member, team)
                .orElseThrow(NoSuchElementException::new);

        member.exitTeam(teaming);
        teamingRepository.delete(teaming);

        return true;
    }

    @Scheduled(cron = "0 59 23 * * *")
    public void deleteTeam() {
        List<Team> emptyTeams = teamRepository.findAllByTeamingsEmpty();
        teamRepository.deleteAll(emptyTeams);

        log.info("Deleted {} empty groups", emptyTeams.size());

        for (Team team : emptyTeams) {
            List<Teaming> deletedTeaming = teamingRepository.deleteAllByTeam(team);
            log.info("Deleted {} invitations about empty groups", deletedTeaming.size());
        }
    }
}
