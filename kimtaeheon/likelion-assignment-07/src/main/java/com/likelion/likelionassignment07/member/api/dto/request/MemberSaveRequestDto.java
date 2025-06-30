package com.likelion.likelionassignment07.member.api.dto.request;


import com.likelion.likelionassignment07.member.domain.Part;

public record MemberSaveRequestDto(
        String name,
        int age,
        Part part
) {
}
