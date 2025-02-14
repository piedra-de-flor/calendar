package com.example.calendar.service.member;

import com.example.calendar.domain.entity.member.CustomMemberDetails;
import com.example.calendar.domain.entity.member.Member;
import com.example.calendar.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomOauth2MemberDetailsService extends DefaultOAuth2UserService {
    private final MemberRepository memberRepository;

    @Transactional
    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);
        log.info("getAttributes : {}", oAuth2User.getAttributes());

        String provider = userRequest.getClientRegistration().getRegistrationId();
        String providerId = oAuth2User.getAttribute("sub");
        String loginEmail = oAuth2User.getAttribute("email");

        Optional<Member> optionalMember = memberRepository.findByEmail(loginEmail);
        Member member;

        if (optionalMember.isEmpty()) {
            member = Member.builder()
                    .email(loginEmail)
                    .name(oAuth2User.getAttribute("name"))
                    .provider(provider)
                    .providerId(providerId)
                    .password("oauth2user")
                    .build();
            memberRepository.save(member);
        } else {
            member = optionalMember.get();
        }

        return new CustomMemberDetails(member, oAuth2User.getAttributes());
    }
}

