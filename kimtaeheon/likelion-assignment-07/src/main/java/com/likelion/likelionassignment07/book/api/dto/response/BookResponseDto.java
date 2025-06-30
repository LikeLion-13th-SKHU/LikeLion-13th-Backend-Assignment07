package com.likelion.likelionassignment07.book.api.dto.response;

public record BookResponseDto(
        String title,
        String alternativeTitle,
        String author,
        String url
) {}
