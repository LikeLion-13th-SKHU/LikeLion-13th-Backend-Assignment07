package com.likelion.basecode.movie.api.dto.response;

public record MovieResponseDto(
        String title,       // 자원의 명칭
        String extent,      // 자원의 크기나 재생시간
        String description  // 내용
) {}
