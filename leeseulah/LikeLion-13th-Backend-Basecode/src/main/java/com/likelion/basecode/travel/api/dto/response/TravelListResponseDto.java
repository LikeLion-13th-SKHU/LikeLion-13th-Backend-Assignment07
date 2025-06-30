package com.likelion.basecode.travel.api.dto.response;

import java.util.List;

public record TravelListResponseDto(
        List<TravelResponseDto> travels
) {}
