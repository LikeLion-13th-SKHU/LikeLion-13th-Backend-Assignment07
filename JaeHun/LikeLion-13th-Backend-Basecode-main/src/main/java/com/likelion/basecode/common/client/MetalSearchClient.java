package com.likelion.basecode.common.client;

import com.likelion.basecode.metal.api.dto.response.MetalResponseDto;
import com.likelion.basecode.common.error.ErrorCode;
import com.likelion.basecode.common.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class MetalSearchClient {

    private final RestTemplate restTemplate;

    @Value("${book-api.base-url}")
    private String baseUrl;

    @Value("${book-api.service-key}")
    private String serviceKey;

    public List<MetalResponseDto> fetchAllMetals() {
        URI uri = UriComponentsBuilder.fromUriString(baseUrl)
                .path("/MetalService")
                .queryParam("serviceKey", serviceKey)
                .queryParam("numOfRows", 100)
                .queryParam("pageNo", 1)
                .queryParam("resultType", "xml")
                .queryParam("stationcode", 1)
                .queryParam("itemcode", 90303)
                .queryParam("date", "20250630")
                .queryParam("timecode", "RH02")
                .build()
                .toUri();

        ResponseEntity<Map> responseEntity = restTemplate.getForEntity(uri, Map.class);

        Map<String, Object> fullResponseMap = Optional.ofNullable(responseEntity.getBody())
                .orElseThrow(() -> new BusinessException(
                        ErrorCode.BOOK_API_RESPONSE_NULL,
                        ErrorCode.BOOK_API_RESPONSE_NULL.getMessage()
                ));

        System.out.println("--- Full API Response Map Start ---");
        System.out.println(fullResponseMap);
        System.out.println("--- Full API Response Map End ---");

        return extractItemList(fullResponseMap).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @SuppressWarnings("unchecked")
    private List<Map<String, Object>> extractItemList(Map<String, Object> fullResponseMap) {
        Map<String, Object> innerResponseMap;

        if (fullResponseMap.containsKey("response") && fullResponseMap.get("response") instanceof Map) {
            innerResponseMap = (Map<String, Object>) fullResponseMap.get("response");
        } else {
            innerResponseMap = fullResponseMap;
        }

        Map<String, Object> headerNode = castToMap(innerResponseMap.get("header"), ErrorCode.BOOK_API_BODY_MALFORMED);
        String resultCode = (String) headerNode.get("resultCode");
        String resultMsg = (String) headerNode.get("resultMsg");

        if (!"00".equals(resultCode)) {
            throw new BusinessException(ErrorCode.BOOK_API_RESPONSE_ERROR, "공공데이터 API 오류: " + resultMsg + " (코드: " + resultCode + ")");
        }

        Map<String, Object> bodyNode = castToMap(innerResponseMap.get("body"), ErrorCode.BOOK_API_BODY_MALFORMED);
        Map<String, Object> itemsNode = castToMap(bodyNode.get("items"), ErrorCode.BOOK_API_ITEMS_MALFORMED);
        Object itemObj = itemsNode.get("item");

        if (itemObj instanceof List<?> itemList) {
            return (List<Map<String, Object>>) itemList;
        } else if (itemObj instanceof Map) {
            return Collections.singletonList((Map<String, Object>) itemObj);
        }

        throw new BusinessException(ErrorCode.BOOK_API_ITEM_MALFORMED, ErrorCode.BOOK_API_ITEM_MALFORMED.getMessage());
    }

    private MetalResponseDto toDto(Map<String, Object> item) {
        return new MetalResponseDto(
                (String) item.getOrDefault("sdate", ""),
                (String) item.getOrDefault("stationcode", ""),
                (String) item.getOrDefault("itemcode", ""),
                (String) item.getOrDefault("timecode", ""),
                String.valueOf(item.getOrDefault("value", ""))
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