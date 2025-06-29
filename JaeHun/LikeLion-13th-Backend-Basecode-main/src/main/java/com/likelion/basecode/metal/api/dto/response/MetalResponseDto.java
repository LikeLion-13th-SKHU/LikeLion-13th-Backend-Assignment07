package com.likelion.basecode.metal.api.dto.response;

public record MetalResponseDto(
        String sdate,
        String stationcode,
        String itemcode,
        String timecode,
        String value
) {}
