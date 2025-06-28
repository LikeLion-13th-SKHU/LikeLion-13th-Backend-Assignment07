package com.likelion.basecode.openApi.api.response;

import lombok.Builder;

@Builder
public record AddressInfoResponseDto(

        Double longitude,  //경도(x)값 반환
        Double latitude  //위도(y)값 반환

) {

    public static AddressInfoResponseDto from(Double longitude, Double latitude){
        return AddressInfoResponseDto.builder()
                .latitude(latitude)
                .longitude(longitude)
                .build();

    }
}
