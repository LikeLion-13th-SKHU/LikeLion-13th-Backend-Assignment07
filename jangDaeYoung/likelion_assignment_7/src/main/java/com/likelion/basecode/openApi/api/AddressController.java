package com.likelion.basecode.openApi.api;


import com.likelion.basecode.common.error.SuccessCode;
import com.likelion.basecode.common.template.ApiResTemplate;
import com.likelion.basecode.openApi.api.request.AddressInfoRequestDto;
import com.likelion.basecode.openApi.api.response.AddressInfoResponseDto;
import com.likelion.basecode.openApi.application.AddressService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/address")
@RequiredArgsConstructor
public class AddressController {

    private final AddressService addressService;

    @PostMapping("/find") //도로명 주속 받으면 경도, 위도값 반환
    public ApiResTemplate<AddressInfoResponseDto> findPosition(@RequestBody AddressInfoRequestDto addressInfoRequestDto)
    {
        AddressInfoResponseDto addressInfoResponseDto = addressService.getPosition(addressInfoRequestDto.address());

        return ApiResTemplate.successResponse(SuccessCode.GET_POSITION_SUCCESS, addressInfoResponseDto);
    }

}
