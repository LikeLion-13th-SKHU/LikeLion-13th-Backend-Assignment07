package com.likelion.basecode.travel.api;

import com.likelion.basecode.travel.api.dto.response.TravelListResponseDto;
import com.likelion.basecode.travel.application.TravelService;
import com.likelion.basecode.common.error.SuccessCode;
import com.likelion.basecode.common.template.ApiResTemplate;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/travels")
public class TravelController {

    private final TravelService travelService;

    @GetMapping("/all")
    public ApiResTemplate<TravelListResponseDto> getAllTravels() {
        TravelListResponseDto travelListResponseDto = travelService.fetchAllRecommendedTravels();
        return ApiResTemplate.successResponse(SuccessCode.GET_SUCCESS, travelListResponseDto);
    }

    @GetMapping("/recommendations")
    public ApiResTemplate<TravelListResponseDto> recommendTravels(@RequestParam Long postId) {
        TravelListResponseDto travelListResponseDto = travelService.recommendTravelsByPostId(postId);
        return ApiResTemplate.successResponse(SuccessCode.GET_SUCCESS, travelListResponseDto);
    }
}
