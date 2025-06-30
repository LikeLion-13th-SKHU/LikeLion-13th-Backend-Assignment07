package com.likelion.basecode.travel.application;

import com.likelion.basecode.travel.api.dto.response.TravelResponseDto;
import com.likelion.basecode.travel.api.dto.response.TravelListResponseDto;
import com.likelion.basecode.common.client.TravelSearchClient;
import com.likelion.basecode.common.client.TagRecommendationClient;
import com.likelion.basecode.common.error.ErrorCode;
import com.likelion.basecode.common.exception.BusinessException;
import com.likelion.basecode.post.domain.Post;
import com.likelion.basecode.post.domain.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TravelService {

    private final PostRepository postRepository;
    private final TagRecommendationClient tagClient;
    private final TravelSearchClient travelSearchClient;

    // 전체 여행지 목록 조회
    public TravelListResponseDto fetchAllRecommendedTravels() {
        List<TravelResponseDto> travels = travelSearchClient.fetchAllTravels();
        return new TravelListResponseDto(travels);
    }

    // 특정 게시글의 추천 태그를 기반으로 여행지 추천
    public TravelListResponseDto recommendTravelsByPostId(Long postId) {
        // 1. 게시글 조회
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new BusinessException(ErrorCode.POST_NOT_FOUND_EXCEPTION,
                        ErrorCode.POST_NOT_FOUND_EXCEPTION.getMessage()));

        // 2. AI 기반 태그 추천
        List<String> tags = tagClient.getRecommendedTags(post.getContents());

        // 3. 태그 추천 결과가 비어있는 경우 예외 처리
        if (tags.isEmpty()) {
            throw new BusinessException(ErrorCode.TAG_RECOMMENDATION_EMPTY,
                    ErrorCode.TAG_RECOMMENDATION_EMPTY.getMessage());
        }

        // 4. 전체 여행지 목록 조회
        List<TravelResponseDto> allTravels = travelSearchClient.fetchAllTravels();

        // 5. description에 태그가 포함된 여행지만 필터링
        List<TravelResponseDto> filteredTravels = filterTravelsByDescription(allTravels, tags);

        // 6. 필터링 결과가 비어있으면 예외 처리
        if (filteredTravels.isEmpty()) {
            throw new BusinessException(ErrorCode.TRAVEL_API_NO_RESULT, ErrorCode.TRAVEL_API_NO_RESULT.getMessage());
        }

        // 7. 최종 결과 반환
        return new TravelListResponseDto(filteredTravels);
    }

    // 여행지 목록에서 description에 태그가 포함된 여행지만 필터링
    private List<TravelResponseDto> filterTravelsByDescription(
            List<TravelResponseDto> travels, List<String> tags
    ) {
        return travels.stream()
                .filter(travel ->
                        tags.stream().anyMatch(tag -> {
                            String description = Optional.ofNullable(travel.description()).orElse("");
                            return description.contains(tag);
                        })
                )
                .limit(3)
                .toList();
    }
}
