package com.likelion.likelionassignment07.common.error;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum ErrorCode {

    // 404
    MEMBER_NOT_FOUND_EXCEPTION(HttpStatus.NOT_FOUND, "해당 사용자가 없습니다. memberId = ", "NOT_FOUND_404"),
    POST_NOT_FOUND_EXCEPTION(HttpStatus.NOT_FOUND, "해당 게시글이 없습니다. postId = ", "NOT_FOUND_404"),
    TAG_RECOMMENDATION_EMPTY(HttpStatus.BAD_REQUEST, "추천 가능한 태그가 없습니다.", "TAG_RECOMMENDATION_EMPTY_400"),
    BOOK_API_NO_RESULT(HttpStatus.NOT_FOUND, "해당 키워드로 검색된 도서가 없습니다.", "BOOK_API_NO_RESULT_400"),
    WEATHER_CITY_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 도시의 날씨 정보를 찾을 수 없습니다.", "WEATHER_CITY_NOT_FOUND_404"),

    // 500
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "알 수 없는 서버 에러가 발생했습니다", "INTERNAL_SERVER_ERROR_500"),
    BOOK_API_RESPONSE_NULL(HttpStatus.INTERNAL_SERVER_ERROR, "도서 API 응답이 null입니다.", "BOOK_API_500"),
    BOOK_API_BODY_MALFORMED(HttpStatus.INTERNAL_SERVER_ERROR, "도서 API의 body 항목이 잘못되었습니다.", "BOOK_API_500"),
    BOOK_API_ITEMS_MALFORMED(HttpStatus.INTERNAL_SERVER_ERROR, "도서 API의 items 항목이 잘못되었습니다.", "BOOK_API_500"),
    BOOK_API_ITEM_MALFORMED(HttpStatus.INTERNAL_SERVER_ERROR, "도서 API의 item 항목이 잘못되었습니다.", "BOOK_API_500"),
    S3_UPLOAD_FAIL(HttpStatus.INTERNAL_SERVER_ERROR, "S3 파일 업로드에 실패했습니다.", "S3_UPLOAD_FAIL_500"),
    S3_DELETE_FAIL(HttpStatus.INTERNAL_SERVER_ERROR, "S3 파일 삭제에 실패했습니다.", "S3_DELETE_FAIL_500"),
    S3_URL_PARSING_EXCEPTION(HttpStatus.INTERNAL_SERVER_ERROR, "S3 이미지 파싱에 실패했습니다.", "S3_URL_PARSING_EXCEPTION_500"),
    IMAGE_NOT_FOUND_EXCEPTION(HttpStatus.INTERNAL_SERVER_ERROR, "삭제할 이미지가 존재하지 않습니다.", "IMAGE_NOT_FOUND_EXCEPTION_500"),
    WEATHER_API_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "날씨 정보 API 호출에 실패했습니다.", "WEATHER_API_ERROR_500"),
    WEATHER_DATA_PARSING_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "날씨 데이터 파싱에 실패했습니다.", "WEATHER_DATA_PARSING_ERROR_500"),

    // 400
    VALIDATION_EXCEPTION(HttpStatus.BAD_REQUEST, "유효성 검사에 실패하였습니다.", "BAD_REQUEST_400"),

    // 401
    WEATHER_API_KEY_INVALID(HttpStatus.UNAUTHORIZED, "날씨 API 키가 유효하지 않습니다.", "WEATHER_API_KEY_INVALID_401");


    private final HttpStatus httpStatus;
    private final String message;
    private final String code;

    public int getHttpStatusCode() {
        return httpStatus.value();
    }
}
