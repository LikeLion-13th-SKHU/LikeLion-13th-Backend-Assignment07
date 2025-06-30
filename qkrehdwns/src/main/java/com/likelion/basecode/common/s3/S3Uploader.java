package com.likelion.basecode.common.s3;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.likelion.basecode.common.error.ErrorCode;
import com.likelion.basecode.common.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class S3Uploader {

    private final AmazonS3 amazonS3;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    // MultipartFile을 S3에 업로드 한 후, 해당 파일의 접근 URL을 반환
    public String upload(MultipartFile file, String dirName) {
        // S3에 저장될 파일 경로 (디렉토리/랜덤UUID_원본파일명)
        String fileName = dirName + "/" + UUID.randomUUID() + "_" + file.getOriginalFilename();
        ObjectMetadata metadata = new ObjectMetadata();

        try {
            // 메타데이터 설정
            metadata.setContentLength(file.getSize()); // 파일 크기 설정
            metadata.setContentType(file.getContentType()); // MIME 타입

            // S3에 파일 업로드
            amazonS3.putObject(bucket, fileName, file.getInputStream(), metadata);
        } catch (IOException e) {
            throw new BusinessException(ErrorCode.S3_UPLOAD_FAIL, ErrorCode.S3_UPLOAD_FAIL.getMessage());
        }

        // 업로드 완료된 파일의 URL 반환
        return amazonS3.getUrl(bucket, fileName).toString();
    }

    // S3에서 파일 삭제
    public void delete(String imageUrl) {
        try {
            URL url = new URL(imageUrl);
            String path = url.getPath().substring(1);

            // URL 디코딩 - 한글 파일명 처리
            String key = URLDecoder.decode(path, StandardCharsets.UTF_8);

            amazonS3.deleteObject(bucket, key);
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.S3_DELETE_FAIL, "S3 이미지 삭제 중 오류가 발생했습니다.");
        }
    }

}
