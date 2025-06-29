package com.likelion.basecode.metal.api.dto.response;

import java.util.List;


public record MetalListResponseDto(
        List<MetalResponseDto> metals
) {}

