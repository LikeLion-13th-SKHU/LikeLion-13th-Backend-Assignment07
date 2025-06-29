package com.likelion.basecode.food.application;

import com.likelion.basecode.common.client.FoodSearchClient;
import com.likelion.basecode.food.api.dto.response.FoodListResponseDto;
import com.likelion.basecode.common.error.ErrorCode;
import com.likelion.basecode.common.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FoodService {

    private final FoodSearchClient foodSearchClient;

    public FoodListResponseDto fetchFoods(String keyword) {
        FoodListResponseDto foodList = foodSearchClient.fetchFoods(keyword);

        if (foodList.foods().isEmpty()) {
            throw new BusinessException(ErrorCode.FOOD_API_NO_RESULT, "검색 결과가 없습니다.");
        }

        return foodList;
    }
}
