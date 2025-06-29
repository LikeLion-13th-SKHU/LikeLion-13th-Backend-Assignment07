package com.likelion.basecode.common.client;

import com.likelion.basecode.common.error.ErrorCode;
import com.likelion.basecode.common.exception.BusinessException;
import com.likelion.basecode.food.api.dto.response.FoodListResponseDto;
import com.likelion.basecode.food.api.dto.response.FoodResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.*;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class FoodSearchClient {

    private final RestTemplate restTemplate;

    @Value("${food-api.base-url}")
    private String baseUrl;

    @Value("${food-api.service-key}")
    private String serviceKey;

    // keyword로 음식점 검색 (키워드 없으면 전체 조회)
    public FoodListResponseDto fetchFoods(String keyword) {
        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUriString(baseUrl)
                .queryParam("serviceKey", serviceKey)
                .queryParam("numOfRows", 5)
                .queryParam("pageNo", 1);

        if (keyword != null && !keyword.isBlank()) {
            uriBuilder.queryParam("keyword", keyword);
        }

        URI uri = uriBuilder.build().encode().toUri();

        ResponseEntity<Map> response = restTemplate.getForEntity(uri, Map.class);

        Map<String, Object> responseBody = Optional.ofNullable(response.getBody())
                .orElseThrow(() -> new BusinessException(ErrorCode.FOOD_API_RESPONSE_NULL, "음식점 API 응답이 없습니다."));

        Map<String, Object> body = castToMap(responseBody.get("body"), ErrorCode.FOOD_API_BODY_MALFORMED);

        List<Map<String, Object>> items = extractItems(body);

        List<FoodResponseDto> foodDtos = items.stream()
                .map(this::toDto)
                .collect(Collectors.toList());

        return new FoodListResponseDto(foodDtos);
    }

    @SuppressWarnings("unchecked")
    private List<Map<String, Object>> extractItems(Map<String, Object> body) {
        Object itemsObj = body.get("items");
        if (!(itemsObj instanceof Map)) {
            throw new BusinessException(ErrorCode.FOOD_API_BODY_MALFORMED, "음식점 응답 body.items 형식 오류");
        }
        Map<String, Object> itemsMap = (Map<String, Object>) itemsObj;

        Object itemObj = itemsMap.get("item");
        if (itemObj instanceof Map) return List.of((Map<String, Object>) itemObj);
        if (itemObj instanceof List) return (List<Map<String, Object>>) itemObj;

        throw new BusinessException(ErrorCode.FOOD_API_BODY_MALFORMED, "음식점 응답 item 파싱 실패");
    }

    private FoodResponseDto toDto(Map<String, Object> item) {
        return new FoodResponseDto(
                (String) item.getOrDefault("title", ""),
                (String) item.getOrDefault("issuedDate", ""),
                (String) item.getOrDefault("category1", ""),
                (String) item.getOrDefault("category2", ""),
                (String) item.getOrDefault("category3", ""),
                (String) item.getOrDefault("information", ""),
                (String) item.getOrDefault("tel", ""),
                (String) item.getOrDefault("operatingTime", ""),
                (String) item.getOrDefault("address", ""),
                (String) item.getOrDefault("coordinates", "")
        );
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> castToMap(Object obj, ErrorCode errorCode) {
        if (!(obj instanceof Map)) {
            throw new BusinessException(errorCode, errorCode.getMessage());
        }
        return (Map<String, Object>) obj;
    }
}
