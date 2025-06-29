package com.likelion.basecode.metal.api;

import com.likelion.basecode.metal.api.dto.response.MetalListResponseDto;
import com.likelion.basecode.metal.application.MetalService;
import com.likelion.basecode.common.template.ApiResTemplate;
import com.likelion.basecode.common.error.SuccessCode;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/metals")
public class MetalController {

    private final MetalService metalService;

    @GetMapping("/all")
    public ApiResTemplate<MetalListResponseDto> getAllMetals() {
        MetalListResponseDto response = metalService.fetchAllMetals();
        return ApiResTemplate.successResponse(SuccessCode.GET_SUCCESS, response);
    }
}

