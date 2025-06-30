package com.likelion.basecode.korea.api;

import com.likelion.basecode.common.error.SuccessCode;
import com.likelion.basecode.common.template.ApiResTemplate;
import com.likelion.basecode.korea.api.dto.response.KoreaListResponseDto;
import com.likelion.basecode.korea.application.KoreaService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/korea")
public class KoreaController {

    private final KoreaService koreaService;

    @GetMapping("/all")
    public ApiResTemplate<KoreaListResponseDto> getAll() {
        KoreaListResponseDto koreaListResponseDto = koreaService.fetchAllRecommended();
        return ApiResTemplate.successResponse(SuccessCode.GET_SUCCESS, koreaListResponseDto);
    }

}
