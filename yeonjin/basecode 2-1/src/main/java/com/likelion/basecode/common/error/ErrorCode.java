package com.likelion.basecode.common.error;

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

    // 500
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "내부 서버 에러가 발생했습니다", "INTERNAL_SERVER_ERROR_500"),

    // 영화 API 관련 에러코드 추가
    MOVIE_API_RESPONSE_NULL(HttpStatus.INTERNAL_SERVER_ERROR, "영화 API 응답이 없습니다.", "MOVIE_API_RESPONSE_NULL_500"),
    MOVIE_API_BODY_MALFORMED(HttpStatus.INTERNAL_SERVER_ERROR, "영화 API 응답 형식이 올바르지 않습니다.", "MOVIE_API_BODY_MALFORMED_500"),
    MOVIE_API_NO_RESULT(HttpStatus.NOT_FOUND, "영화 API 조회 결과가 없습니다.", "MOVIE_API_NO_RESULT_404"),

    //태그 관련 오류
    TAG_RECOMMENDATION_EMPTY(HttpStatus.INTERNAL_SERVER_ERROR, "추천 태그가 존재하지 않습니다.", "TAG_RECOMMENDATION_EMPTY_500"),

    //s3
    S3_UPLOAD_FAIL(HttpStatus.INTERNAL_SERVER_ERROR, "S3 파일 업로드에 실패했습니다.", "S3_UPLOAD_FAIL_500"),
    S3_DELETE_FAIL(HttpStatus.INTERNAL_SERVER_ERROR, "S3 이미지 삭제에 실패했습니다.", "S3_002"),

    //food
    FOOD_API_RESPONSE_NULL(HttpStatus.INTERNAL_SERVER_ERROR, "푸드 API 응답이 없습니다.", "FOOD_API_RESPONSE_NULL_500"),
    FOOD_API_BODY_MALFORMED(HttpStatus.INTERNAL_SERVER_ERROR, "푸드 API 응답 형식이 올바르지 않습니다.", "FOOD_API_BODY_MALFORMED_500"),
    FOOD_API_NO_RESULT(HttpStatus.NOT_FOUND, "푸드 API 검색 결과가 없습니다.", "FOOD_API_NO_RESULT_404");





    private final HttpStatus httpStatus;
    private final String message;
    private final String code;

    public int getHttpStatusCode() {
        return httpStatus.value();
    }
}
