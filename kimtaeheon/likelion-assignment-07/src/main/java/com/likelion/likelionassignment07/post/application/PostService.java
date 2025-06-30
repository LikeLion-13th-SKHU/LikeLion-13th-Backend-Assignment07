package com.likelion.likelionassignment07.post.application;

import com.likelion.likelionassignment07.common.client.TagRecommendationClient;
import com.likelion.likelionassignment07.common.error.ErrorCode;
import com.likelion.likelionassignment07.common.exception.BusinessException;
import com.likelion.likelionassignment07.common.s3.S3Uploader;
import com.likelion.likelionassignment07.member.domain.Member;
import com.likelion.likelionassignment07.member.domain.repository.MemberRepository;
import com.likelion.likelionassignment07.post.api.dto.request.PostSaveRequestDto;
import com.likelion.likelionassignment07.post.api.dto.request.PostUpdateRequestDto;
import com.likelion.likelionassignment07.post.api.dto.response.PostInfoResponseDto;
import com.likelion.likelionassignment07.post.api.dto.response.PostListResponseDto;
import com.likelion.likelionassignment07.post.domain.Post;
import com.likelion.likelionassignment07.post.domain.repository.PostRepository;
import com.likelion.likelionassignment07.posttag.domain.PostTag;
import com.likelion.likelionassignment07.posttag.domain.repository.PostTagRepository;
import com.likelion.likelionassignment07.tag.domain.Tag;
import com.likelion.likelionassignment07.tag.domain.repository.TagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PostService {

    private final MemberRepository memberRepository;
    private final PostRepository postRepository;
    private final TagRepository tagRepository;
    private final PostTagRepository postTagRepository;
    private final TagRecommendationClient tagClient;
    private final S3Uploader s3Uploader;

    // 게시물 저장
    @Transactional
    public PostInfoResponseDto postSave(PostSaveRequestDto postSaveRequestDto, MultipartFile imageFile) {
        // 회원 조회
        Member member = memberRepository.findById(postSaveRequestDto.memberId())
                .orElseThrow(() -> new BusinessException(ErrorCode.MEMBER_NOT_FOUND_EXCEPTION,
                        ErrorCode.MEMBER_NOT_FOUND_EXCEPTION.getMessage() + postSaveRequestDto.memberId()));

        String imageUrl = null;
        if (imageFile != null && !imageFile.isEmpty()) {
            imageUrl = s3Uploader.upload(imageFile, "post-images");
        }

        // 게시물 생성
        Post post = Post.builder()
                .title(postSaveRequestDto.title())
                .contents(postSaveRequestDto.contents())
                .imageUrl(imageUrl)
                .member(member)
                .build();

        postRepository.save(post);

        // AI 기반 추천 태그 추출 및 등록
        List<String> tagNames = tagClient.getRecommendedTags(post.getContents());
        registerTagsToPost(post, tagNames);

        // Fetch Join으로 태그 포함된 post 다시 조회
        // postSave() 이후 바로 반환하면, post.getPostTags()는 LAZY 로딩이라 tag 정보가 초기화되지 않아 tags가 누락된 채 응답될 수 있기 때문
        Post postWithTags = getPostWithTags(post.getPostId());

        return PostInfoResponseDto.from(post);
    }


    // 특정 작성자가 작성한 게시글 목록을 조회
    public PostListResponseDto postFindMember(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new BusinessException(ErrorCode.MEMBER_NOT_FOUND_EXCEPTION,
                        ErrorCode.MEMBER_NOT_FOUND_EXCEPTION.getMessage() + memberId));

        List<Post> posts = postRepository.findByMember(member);
        List<PostInfoResponseDto> postInfoResponseDtos = posts.stream()
                .map(PostInfoResponseDto::from)
                .toList();

        return PostListResponseDto.from(postInfoResponseDtos);
    }

    // 게시물 수정
    @Transactional
    public PostInfoResponseDto postUpdate(Long postId,
                                          PostUpdateRequestDto postUpdateRequestDto, MultipartFile imageFile) {
        Post postWithTags = getPostWithTags(postId);

        // 기존 이미지 URL
        String oldImageUrl = postWithTags.getImageUrl();
        if (imageFile != null && !imageFile.isEmpty()) {
            String newImageUrl = s3Uploader.upload(imageFile, "post-images");
            postWithTags.updateImage(newImageUrl);
            // 새 이미지 업로드 성공 후 기존 이미지 삭제
            if (oldImageUrl != null && !oldImageUrl.isEmpty()) {
                s3Uploader.delete(oldImageUrl);
            }

        }

        postWithTags.update(postUpdateRequestDto);

        postTagRepository.deleteAllByPost(postWithTags);
        postWithTags.getPostTags().clear(); // 양방향 관계 유지

        // 수정된 내용으로 추천 태그 재생성 및 등록
        List<String> tagNames = tagClient.getRecommendedTags(postWithTags.getContents());
        registerTagsToPost(postWithTags, tagNames);

        return PostInfoResponseDto.from(postWithTags);
    }

    // 게시물 삭제
    @Transactional
    public void postDelete(Long postId) {
        Post post = getPost(postId);

        // 게시물 삭제 전 이미지 삭제
        if (post.getImageUrl() != null && !post.getImageUrl().isEmpty()) {
            s3Uploader.delete(post.getImageUrl());
        }
        postRepository.delete(post);
    }

    // 게시글의 이미지만 삭제
    @Transactional
    public PostInfoResponseDto deletePostImage(Long postId) {
        Post post = getPostWithTags(postId);

        // 현재 이미지 URL 확인
        String currentImageUrl = post.getImageUrl();


        if (currentImageUrl == null || currentImageUrl.isEmpty()) {
            throw new BusinessException(ErrorCode.IMAGE_NOT_FOUND_EXCEPTION, ErrorCode.IMAGE_NOT_FOUND_EXCEPTION.getMessage());
        }

        // S3에서 이미지만 삭제
        s3Uploader.delete(currentImageUrl);
        post.updateImage(null);

        Post postWithTags = getPostWithTags(postId);
        return PostInfoResponseDto.from(postWithTags);
    }

    // 게시물 추천 태그 목록 등록 및 PostTag 연관 엔티티 저장
    private void registerTagsToPost(Post post, List<String> tagNames) {
        for (String tagName : tagNames) {
            // 기존 태그가 있다면 사용, 없으면 새로 생성
            Tag tag = tagRepository.findByName(tagName)
                    .orElseGet(() -> tagRepository.save(new Tag(tagName)));

            // PostTag 생성 및 연관 관계 추가
            PostTag postTag = new PostTag(post, tag);
            post.getPostTags().add(postTag);   // 양방향 매핑 유지
            postTagRepository.save(postTag);
        }
    }

    private Post getPostWithTags(Long postId) {
        return postRepository.findByIdWithTags(postId)
                .orElseThrow(() -> new BusinessException(ErrorCode.POST_NOT_FOUND_EXCEPTION,
                        ErrorCode.POST_NOT_FOUND_EXCEPTION.getMessage() + postId));
    }

    private Post getPost(Long postId) {
        return postRepository.findById(postId)
                .orElseThrow(() -> new BusinessException(ErrorCode.POST_NOT_FOUND_EXCEPTION,
                        ErrorCode.POST_NOT_FOUND_EXCEPTION.getMessage() + postId));
    }
}
