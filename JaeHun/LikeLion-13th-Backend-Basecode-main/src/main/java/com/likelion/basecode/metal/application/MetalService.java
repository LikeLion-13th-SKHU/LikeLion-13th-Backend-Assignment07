package com.likelion.basecode.metal.application;

import com.likelion.basecode.metal.api.dto.response.MetalListResponseDto;
import com.likelion.basecode.metal.api.dto.response.MetalResponseDto;
import com.likelion.basecode.common.client.MetalSearchClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MetalService {

    private final MetalSearchClient metalSearchClient;

    public MetalListResponseDto fetchAllMetals() {
        List<MetalResponseDto> metals = metalSearchClient.fetchAllMetals();
        return new MetalListResponseDto(metals);
    }
}
