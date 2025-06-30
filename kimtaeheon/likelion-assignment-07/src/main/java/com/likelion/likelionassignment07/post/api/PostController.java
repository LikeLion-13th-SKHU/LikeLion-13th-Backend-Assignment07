package com.likelion.likelionassignment07.post.api;

import com.likelion.likelionassignment07.common.error.SuccessCode;
import com.likelion.likelionassignment07.common.template.ApiResTemplate;
import com.likelion.likelionassignment07.post.api.dto.request.PostSaveRequestDto;
import com.likelion.likelionassignment07.post.api.dto.request.PostUpdateRequestDto;
import com.likelion.likelionassignment07.post.api.dto.response.PostInfoResponseDto;
import com.likelion.likelionassignment07.post.api.dto.response.PostListResponseDto;
import com.likelion.likelionassignment07.post.application.PostService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/post")
public class PostController {

    private final PostService postService;

    // 게시물 저장
    @PostMapping("/save")
    public ApiResTemplate<PostInfoResponseDto> postSave(@RequestPart("post") @Valid PostSaveRequestDto postSaveRequestDto,
                                                        @RequestPart(value = "image", required = false) MultipartFile imageFile) {
        PostInfoResponseDto postInfoResponseDto = postService.postSave(postSaveRequestDto, imageFile);
        return ApiResTemplate.successResponse(SuccessCode.POST_SAVE_SUCCESS, postInfoResponseDto);
    }

    // 사용자 id를 기준으로 해당 사용자가 작성한 게시글 목록 조회
    @GetMapping("/{memberId}")
    public ApiResTemplate<PostListResponseDto> myPostFindAll(@PathVariable("memberId") Long memberId) {
        PostListResponseDto postListResponseDto = postService.postFindMember(memberId);
        return ApiResTemplate.successResponse(SuccessCode.GET_SUCCESS, postListResponseDto);
    }

    // 게시물 id를 기준으로 사용자가 작성한 게시물 수정
    @PatchMapping("/{postId}")
    public ApiResTemplate<PostInfoResponseDto> postUpdate(@PathVariable("postId") Long postId,
                                                          @RequestPart("post") PostUpdateRequestDto postUpdateRequestDto,
                                                          @RequestPart(value = "image", required = false) MultipartFile imageFile) {
        PostInfoResponseDto postInfoResponseDto = postService.postUpdate(postId, postUpdateRequestDto, imageFile);
        return ApiResTemplate.successResponse(SuccessCode.POST_UPDATE_SUCCESS, postInfoResponseDto);
    }

    // 게시물 id를 기준으로 사용자가 작성한 게시물 삭제
    @DeleteMapping("/{postId}")
    public ApiResTemplate<String> postDelete(
            @PathVariable("postId") Long postId) {
        postService.postDelete(postId);
        return ApiResTemplate.successResponse(SuccessCode.POST_DELETE_SUCCESS, null);
    }

    // 게시물의 이미지 삭제 API
    @DeleteMapping("/{postId}/image")
    public ApiResTemplate<PostInfoResponseDto> deletePostImage(@PathVariable Long postId) {
        PostInfoResponseDto response = postService.deletePostImage(postId);
        return ApiResTemplate.successResponse(SuccessCode.POST_IMAGE_DELETE_SUCCESS, response);
    }
}
