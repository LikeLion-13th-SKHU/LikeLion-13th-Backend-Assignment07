package com.likelion.basecode.food.api;

import com.likelion.basecode.common.template.ApiResTemplate;
import com.likelion.basecode.common.error.SuccessCode;
import com.likelion.basecode.food.api.dto.response.FoodListResponseDto;
import com.likelion.basecode.food.application.FoodService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/foods")
public class FoodController {

    private final FoodService foodService;

    @GetMapping("/search")
    public ApiResTemplate<FoodListResponseDto> searchFoods(@RequestParam(required = false) String keyword) {
        FoodListResponseDto response = foodService.fetchFoods(keyword);
        return ApiResTemplate.successResponse(SuccessCode.GET_SUCCESS, response);
    }

}
