package com.likelion.likelionassignment07.weather.api;

import com.likelion.likelionassignment07.common.error.SuccessCode;
import com.likelion.likelionassignment07.common.template.ApiResTemplate;
import com.likelion.likelionassignment07.weather.api.dto.response.WeatherResponseDto;
import com.likelion.likelionassignment07.weather.application.WeatherService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/weather")
public class WeatherController {

    private final WeatherService weatherService;

    /**
     * 특정 도시의 현재 날씨 조회
     * @param city 도시명 (기본값: Seoul)
     * @return 날씨 정보
     */
    @GetMapping("/current")
    public ApiResTemplate<WeatherResponseDto> getCurrentWeather(
            @RequestParam(defaultValue = "Seoul") String city) {

        WeatherResponseDto response = weatherService.getCurrentWeather(city);
        return ApiResTemplate.successResponse(SuccessCode.GET_SUCCESS, response);
    }

    // 서울 날씨 조회
    @GetMapping("/seoul")
    public ApiResTemplate<WeatherResponseDto> getSeoulWeather() {
        WeatherResponseDto response = weatherService.getCurrentWeather("Seoul");
        return ApiResTemplate.successResponse(SuccessCode.GET_SUCCESS, response);
    }

    // 한국 주요 도시들의 날씨 정보 조회
    @GetMapping("/korea-cities")
    public ApiResTemplate<List<WeatherResponseDto>> getKoreaCitiesWeather() {
        List<WeatherResponseDto> response = weatherService.getKoreaCitiesWeather();
        return ApiResTemplate.successResponse(SuccessCode.GET_SUCCESS, response);
    }
}
