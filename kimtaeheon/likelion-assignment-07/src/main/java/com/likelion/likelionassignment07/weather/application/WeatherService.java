package com.likelion.likelionassignment07.weather.application;

import com.likelion.likelionassignment07.common.error.ErrorCode;
import com.likelion.likelionassignment07.common.exception.BusinessException;
import com.likelion.likelionassignment07.weather.api.dto.response.WeatherResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class WeatherService {

    private final RestTemplate restTemplate;

    @Value("${weather.api.key}")
    private String apiKey;

    @Value("${weather.api.url}")
    private String baseUrl;

    public WeatherResponseDto getCurrentWeather(String city) {
        try {
            String url = String.format("%s?q=%s&appid=%s&units=metric&lang=kr",
                    baseUrl, city, apiKey);

            log.info("날씨 API 호출: {}", url);

            ResponseEntity<Map> response = restTemplate.getForEntity(url, Map.class);
            Map<String, Object> data = response.getBody();

            if (data == null) {
                throw new BusinessException(ErrorCode.WEATHER_API_ERROR, ErrorCode.WEATHER_API_ERROR.getMessage());
            }

            log.info("날씨 API 응답 성공");
            return parseWeatherData(data);

        } catch (HttpClientErrorException.Unauthorized e) {
            log.error("날씨 API 키 인증 실패: {}", e.getMessage());
            throw new BusinessException(ErrorCode.WEATHER_API_KEY_INVALID, ErrorCode.WEATHER_API_KEY_INVALID.getMessage());

        } catch (HttpClientErrorException.NotFound e) {
            log.error("도시를 찾을 수 없음: {}", city);
            throw new BusinessException(ErrorCode.WEATHER_CITY_NOT_FOUND, ErrorCode.WEATHER_CITY_NOT_FOUND.getMessage());

        } catch (RestClientException e) {
            log.error("날씨 API 호출 중 네트워크 오류", e);
            throw new BusinessException(ErrorCode.WEATHER_API_ERROR, ErrorCode.WEATHER_API_ERROR.getMessage());

        } catch (BusinessException e) {
            // 이미 BusinessException인 경우 그대로 던짐
            throw e;

        } catch (Exception e) {
            log.error("날씨 정보 조회 중 예상치 못한 오류", e);
            throw new BusinessException(ErrorCode.WEATHER_API_ERROR, ErrorCode.WEATHER_API_ERROR.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    private WeatherResponseDto parseWeatherData(Map<String, Object> data) {
        try {
            // main 객체에서 온도, 습도 정보 추출
            Map<String, Object> main = (Map<String, Object>) data.get("main");
            if (main == null) {
                throw new BusinessException(ErrorCode.WEATHER_DATA_PARSING_ERROR, ErrorCode.WEATHER_DATA_PARSING_ERROR.getMessage());
            }

            double temp = getDoubleValue(main, "temp");
            double feelsLike = getDoubleValue(main, "feels_like");
            double humidity = getDoubleValue(main, "humidity");

            // weather 배열에서 날씨 설명 추출
            List<Map<String, Object>> weather = (List<Map<String, Object>>) data.get("weather");
            if (weather == null || weather.isEmpty()) {
                throw new BusinessException(ErrorCode.WEATHER_DATA_PARSING_ERROR, ErrorCode.WEATHER_DATA_PARSING_ERROR.getMessage());
            }

            String description = (String) weather.get(0).get("description");
            String mainWeather = (String) weather.get(0).get("main");

            // wind 객체에서 풍속 정보 추출
            Map<String, Object> wind = (Map<String, Object>) data.get("wind");
            double windSpeed = wind != null ? getDoubleValue(wind, "speed") : 0.0;

            // 도시명과 국가 정보
            String cityName = (String) data.get("name");
            Map<String, Object> sys = (Map<String, Object>) data.get("sys");
            String country = sys != null ? (String) sys.get("country") : "";

            return new WeatherResponseDto(
                    cityName, temp, feelsLike, humidity,
                    description, mainWeather, windSpeed, country
            );

        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("날씨 데이터 파싱 실패", e);
            throw new BusinessException(ErrorCode.WEATHER_DATA_PARSING_ERROR, ErrorCode.WEATHER_DATA_PARSING_ERROR.getMessage());
        }
    }

    public List<WeatherResponseDto> getKoreaCitiesWeather() {
        String[] cities = {"Seoul", "Busan", "Incheon", "Daegu", "Daejeon", "Gwangju"};

        return List.of(cities).stream()
                .map(this::tryGetWeather)         // Optional<WeatherResponseDto>
                .filter(Optional::isPresent)      // 값이 있는 경우만 필터링
                .map(Optional::get)               // Optional에서 실제 값 꺼내기
                .toList();
    }

    private Optional<WeatherResponseDto> tryGetWeather(String city) {
        try {
            return Optional.of(getCurrentWeather(city));
        } catch (BusinessException e) {
            log.warn("도시 {} 날씨 조회 실패: {}", city, e.getMessage());
            return Optional.empty();
        }
    }

    /**
     * Map에서 double 값 안전하게 가져오기
     */
    private double getDoubleValue(Map<String, Object> map, String key) {
        Object value = map.get(key);
        if (value instanceof Number) {
            return ((Number) value).doubleValue();
        }
        return 0.0;
    }
}
