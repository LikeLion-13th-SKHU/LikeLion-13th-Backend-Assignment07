package com.likelion.likelionassignment07.member.api.dto.response;

import com.likelion.likelionassignment07.member.domain.Member;
import com.likelion.likelionassignment07.member.domain.Part;
import lombok.Builder;

@Builder
public record MemberInfoResponseDto(
        String name,
        int age,
        Part part
) {
    public static MemberInfoResponseDto from(Member member) {
        return MemberInfoResponseDto.builder()
                .name(member.getName())
                .age(member.getAge())
                .part(member.getPart())
                .build();
    }
}
