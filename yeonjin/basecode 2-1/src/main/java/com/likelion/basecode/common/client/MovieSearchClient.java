package com.likelion.basecode.common.client;

import com.likelion.basecode.common.error.ErrorCode;
import com.likelion.basecode.common.exception.BusinessException;
import com.likelion.basecode.movie.api.dto.response.MovieListResponseDto;
import com.likelion.basecode.movie.api.dto.response.MovieResponseDto;
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
public class MovieSearchClient {

    private final RestTemplate restTemplate;

    @Value("${movie-api.base-url}")
    private String baseUrl;

    @Value("${movie-api.service-key}")
    private String serviceKey;

    // 고정값: 5개씩 1페이지 조회
    public MovieListResponseDto fetchMovies() {
        URI uri = UriComponentsBuilder.fromUriString(baseUrl)
                .queryParam("serviceKey", serviceKey)
                .queryParam("numOfRows", 5)
                .queryParam("pageNo", 1)
                .build()
                .encode()
                .toUri();

        ResponseEntity<Map> response = restTemplate.getForEntity(uri, Map.class);

        System.out.println("응답 본문 (KCISA): " + response.getBody());

        Map<String, Object> responseMap = Optional.ofNullable(response.getBody())
                .orElseThrow(() -> new BusinessException(ErrorCode.MOVIE_API_RESPONSE_NULL, "KCISA API 응답이 없습니다."));

        List<Map<String, Object>> items = extractItems(responseMap);

        List<MovieResponseDto> movieDtos = items.stream()
                .map(this::toDto)
                .collect(Collectors.toList());

        return new MovieListResponseDto(movieDtos);
    }

    @SuppressWarnings("unchecked")
    private List<Map<String, Object>> extractItems(Map<String, Object> responseMap) {
        // responseMap 최상위에 body가 있음
        Map<String, Object> body = castToMap(responseMap.get("body"), ErrorCode.MOVIE_API_BODY_MALFORMED);
        Map<String, Object> items = castToMap(body.get("items"), ErrorCode.MOVIE_API_BODY_MALFORMED);

        Object itemObj = items.get("item");
        if (itemObj instanceof Map) return List.of((Map<String, Object>) itemObj);
        if (itemObj instanceof List) return (List<Map<String, Object>>) itemObj;

        throw new BusinessException(ErrorCode.MOVIE_API_BODY_MALFORMED, "KCISA 응답 항목 파싱 실패");
    }


    private MovieResponseDto toDto(Map<String, Object> item) {
        return new MovieResponseDto(
                (String) item.getOrDefault("title", ""),
                (String) item.getOrDefault("extent", ""),
                (String) item.getOrDefault("description", "")
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
