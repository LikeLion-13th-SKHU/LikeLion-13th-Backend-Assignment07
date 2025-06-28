package com.likelion.basecode.naver.api.dto.response;

public record NaverResponseDto(
        String title,        // 글 제목
        String link,         // 글 링크
        String description   // 요약 (내용 미리보기)
) {
}
