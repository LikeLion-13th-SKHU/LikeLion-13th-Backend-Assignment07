package com.likelion.basecode.openApi.api.request;

import jakarta.validation.constraints.NotNull;

public record AddressInfoRequestDto(

        @NotNull
        String address  //도로명 주소 입력 대략적인 위치만 적어도 위도, 경도 정보 확인가능 ex)월드컵경기장

) {

}
