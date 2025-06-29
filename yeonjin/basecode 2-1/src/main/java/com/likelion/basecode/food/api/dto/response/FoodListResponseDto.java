package com.likelion.basecode.food.api.dto.response;

import java.util.List;

public record FoodListResponseDto(
        List<FoodResponseDto> foods
) {}