package com.likelion.likelionassignment07.book.api.dto.response;

import java.util.List;

public record BookListResponseDto(
        List<BookResponseDto> books
) {}
