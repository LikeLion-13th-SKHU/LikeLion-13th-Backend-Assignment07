package com.likelion.likelionassignment07.tag.api.dto.response;
import com.likelion.likelionassignment07.tag.domain.Tag;
import lombok.Builder;

@Builder
public record TagInfoResponseDto(
        Long id,
        String name
) {
    public static TagInfoResponseDto from(Tag tag) {
        return TagInfoResponseDto.builder()
                .id(tag.getId())
                .name(tag.getName())
                .build();
    }
}
