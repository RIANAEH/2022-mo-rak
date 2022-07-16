package com.morak.back.auth.ui.dto;

import com.morak.back.auth.domain.Member;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class MemberResponse {

    private Long id;
    private String name;
    private String profileUrl;
    private String description;

    public static MemberResponse of(Member member, String description) {
        return new MemberResponse(
                member.getId(),
                member.getName(),
                member.getProfileUrl(),
                description
        );
    }
}
