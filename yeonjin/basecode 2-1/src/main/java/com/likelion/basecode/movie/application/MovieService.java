package com.likelion.basecode.movie.application;

import com.likelion.basecode.movie.api.dto.response.MovieListResponseDto;
import com.likelion.basecode.movie.api.dto.response.MovieResponseDto;
import com.likelion.basecode.common.client.MovieSearchClient;
import com.likelion.basecode.common.client.TagRecommendationClient;
import com.likelion.basecode.common.error.ErrorCode;
import com.likelion.basecode.common.exception.BusinessException;
import com.likelion.basecode.post.domain.Post;
import com.likelion.basecode.post.domain.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MovieService {

    private final MovieSearchClient movieSearchClient;
    private final PostRepository postRepository;
    private final TagRecommendationClient tagRecommendationClient;

    // KCISA 영화 목록 조회 (5개 고정)
    public MovieListResponseDto fetchMovies() {
        MovieListResponseDto movieList = movieSearchClient.fetchMovies();

        if (movieList.movies().isEmpty()) {
            throw new BusinessException(ErrorCode.MOVIE_API_NO_RESULT, "검색 결과가 없습니다.");
        }

        return movieList;
    }

    // 게시글 기반 추천 영화 목록 조회 (전체 목록 중 일부 사용)
    public MovieListResponseDto recommendMoviesByPostId(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new BusinessException(ErrorCode.POST_NOT_FOUND_EXCEPTION,
                        ErrorCode.POST_NOT_FOUND_EXCEPTION.getMessage()));

        List<String> recommendedTags = tagRecommendationClient.getRecommendedTags(post.getContents());

        if (recommendedTags.isEmpty()) {
            throw new BusinessException(ErrorCode.TAG_RECOMMENDATION_EMPTY, "추천 태그가 없습니다.");
        }

        // 현재는 전체 영화 목록을 가져와서 랜덤 추출하는 예시
        List<MovieResponseDto> allMovies = movieSearchClient.fetchMovies().movies();

        if (allMovies.isEmpty()) {
            throw new BusinessException(ErrorCode.MOVIE_API_NO_RESULT, "추천 결과가 없습니다.");
        }

        // 최대 5개만 추천 (랜덤 or 앞에서부터)
        List<MovieResponseDto> recommended = allMovies.stream()
                .limit(5)
                .toList();

        return new MovieListResponseDto(recommended);
    }
}
