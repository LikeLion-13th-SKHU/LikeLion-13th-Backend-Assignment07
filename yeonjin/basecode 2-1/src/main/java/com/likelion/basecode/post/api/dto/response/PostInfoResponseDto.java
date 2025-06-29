package com.likelion.basecode.post.api.dto.response;

import com.likelion.basecode.post.domain.Post;
import lombok.Builder;
import java.util.List;

@Builder
public record PostInfoResponseDto(
        Long postId,      // 추가된 postId 필드
        String title,
        String contents,
        String writer,
        List<String> tags,
        String imageUrl
) {
    public static PostInfoResponseDto from(Post post) {
        return PostInfoResponseDto.builder()
                .postId(post.getPostId())     // postId 세팅
                .title(post.getTitle())
                .contents(post.getContents())
                .writer(post.getMember().getName())
                .tags(
                        post.getPostTags().stream()
                                .map(postTag -> postTag.getTag().getName())
                                .toList()
                )
                .imageUrl(post.getImageUrl())
                .build();
    }
}
