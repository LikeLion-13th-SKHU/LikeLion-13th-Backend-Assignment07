package com.likelion.basecode.food.api.dto.response;

public record FoodResponseDto(
        String title,
        String issuedDate,
        String category1,
        String category2,
        String category3,
        String information,
        String tel,
        String operatingTime,
        String address,
        String coordinates
) {}
