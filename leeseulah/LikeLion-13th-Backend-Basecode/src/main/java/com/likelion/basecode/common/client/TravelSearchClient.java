package com.likelion.basecode.common.client;

import com.likelion.basecode.common.error.ErrorCode;
import com.likelion.basecode.common.exception.BusinessException;
import com.likelion.basecode.travel.api.dto.response.TravelResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class TravelSearchClient {

    private final RestTemplate restTemplate;

    @Value("${travel-api.base-url}")
    private String baseUrl;

    @Value("${travel-api.service-key}")
    private String serviceKey;

    // 외부 여행 API로부터 전체 여행지 목록을 조회
    public List<TravelResponseDto> fetchAllTravels() {
        URI uri = UriComponentsBuilder.fromUriString(baseUrl)
                .queryParam("serviceKey", serviceKey)
                .queryParam("numOfRows", 10)
                .queryParam("pageNo", 1)
                .build()
                .toUri();

        ResponseEntity<Map> response = restTemplate.getForEntity(uri, Map.class);

        Map<String, Object> body = Optional.ofNullable(response.getBody())
                .orElseThrow(() -> new BusinessException(ErrorCode.TRAVEL_API_RESPONSE_NULL, ErrorCode.TRAVEL_API_RESPONSE_NULL.getMessage()));

        return extractItemList(body).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @SuppressWarnings("unchecked")
    private List<Map<String, Object>> extractItemList(Map<String, Object> responseMap) {
        Map<String, Object> response = castToMap(responseMap.get("response"), ErrorCode.TRAVEL_API_BODY_MALFORMED);
        Map<String, Object> body = castToMap(response.get("body"), ErrorCode.TRAVEL_API_BODY_MALFORMED);
        Map<String, Object> items = castToMap(body.get("items"), ErrorCode.TRAVEL_API_ITEMS_MALFORMED);
        Object itemObj = items.get("item");

        if (itemObj instanceof List<?> itemList) {
            return (List<Map<String, Object>>) itemList;
        }

        throw new BusinessException(ErrorCode.TRAVEL_API_ITEM_MALFORMED, ErrorCode.TRAVEL_API_ITEM_MALFORMED.getMessage());
    }

    private TravelResponseDto toDto(Map<String, Object> item) {
        return new TravelResponseDto(
                (String) item.getOrDefault("title", ""),
                (String) item.getOrDefault("description", ""),
                (String) item.getOrDefault("reference", ""),
                (String) item.getOrDefault("url", "")
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
