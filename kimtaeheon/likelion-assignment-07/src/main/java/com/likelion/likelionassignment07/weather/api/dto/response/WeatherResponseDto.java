package com.likelion.likelionassignment07.weather.api.dto.response;

public record WeatherResponseDto(
        String city,            // 도시명
        double temperature,     // 온도 (섭씨)
        double feelsLike,      // 체감온도
        double humidity,       // 습도 (%)
        String description,    // 날씨 설명
        String main,          // 날씨 주요 상태
        double windSpeed,     // 풍속
        String country        // 국가 코드
) {
    public String getTemperatureText() {
        return String.format("%.1f°C", temperature);
    }

    public String getFeelsLikeText() {
        return String.format("%.1f°C", feelsLike);
    }

    public String getHumidityText() {
        return String.format("%.0f%%", humidity);
    }

    public String getWindSpeedText() {
        return String.format("%.1fm/s", windSpeed);
    }
}
