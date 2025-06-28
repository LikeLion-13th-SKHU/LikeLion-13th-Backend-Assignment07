package com.likelion.basecode.openApi.application;


import com.likelion.basecode.common.error.ErrorCode;
import com.likelion.basecode.common.exception.BusinessException;
import com.likelion.basecode.openApi.api.response.AddressInfoResponseDto;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;



@Service
public class AddressService {

    @Value("${openapi.access-key}")
    private String API_KEY;

    public AddressInfoResponseDto getPosition(String address){
        try {

            String url = "https://api.vworld.kr/req/address?service=address&request=getCoord" //공공데이터 요청 url형식
                    + "&key=" + API_KEY
                    + "&type=ROAD"
                    + "&address=" + address;

            //Spring에서 제공하는 HTTP 요청 도구 클래스인 RestTemplate의 인스턴스를 생성. 이 객체는 외부 API 서버와의 HTTP 통신을 수행할 수 있다. GET, POST, PUT, DELETE 등 다양한 HTTP 메서드 지원
            RestTemplate restTemplate = new RestTemplate();

            //response JSON이지만 문자열 상태 문자열로 취급됨
            String response = restTemplate.getForObject(url, String.class);

            // JSON 객체로 파싱:
            JSONObject jsonResponse = new JSONObject(response);
            String x = jsonResponse.getJSONObject("response")
                    .getJSONObject("result")
                    .getJSONObject("point")
                    .getString("x");

            String y = jsonResponse.getJSONObject("response")
                    .getJSONObject("result")
                    .getJSONObject("point")
                    .getString("y");

            double longitude = Double.parseDouble(x);
            double latitude = Double.parseDouble(y);

            System.out.println(url);
            System.out.println(longitude);
            System.out.println(latitude);

            return AddressInfoResponseDto.from(latitude, longitude);    //경도 위도 반환

        } catch (Exception e) {
            throw new BusinessException(ErrorCode.POSITION_NOT_FOUND, ErrorCode.POSITION_NOT_FOUND.getMessage());
        }
    }
}







