package com.likelion.basecode.movie.api;

import com.likelion.basecode.movie.api.dto.response.MovieListResponseDto;
import com.likelion.basecode.movie.application.MovieService;
import com.likelion.basecode.common.error.SuccessCode;
import com.likelion.basecode.common.template.ApiResTemplate;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/movies")
public class MovieController {

    private final MovieService movieService;

    // 전체 영화 목록 조회 (5개 고정)
    @GetMapping("/search")
    public ApiResTemplate<MovieListResponseDto> searchMovies() {
        MovieListResponseDto movieListResponseDto = movieService.fetchMovies();
        return ApiResTemplate.successResponse(SuccessCode.GET_SUCCESS, movieListResponseDto);
    }

    // 게시글 기반 추천 영화 목록 조회
    @GetMapping("/recommend/{postId}")
    public ApiResTemplate<MovieListResponseDto> recommendMoviesByPost(@PathVariable Long postId) {
        MovieListResponseDto movieListResponseDto = movieService.recommendMoviesByPostId(postId);
        return ApiResTemplate.successResponse(SuccessCode.GET_SUCCESS, movieListResponseDto);
    }
}
