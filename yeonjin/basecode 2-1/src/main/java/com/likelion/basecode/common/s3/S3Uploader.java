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
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class S3Uploader {

    private final AmazonS3 amazonS3;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    // 파일 업로드
    public String upload(MultipartFile file, String dirName) {
        String fileName = dirName + "/" + UUID.randomUUID() + "_" + file.getOriginalFilename();
        ObjectMetadata metadata = new ObjectMetadata();

        try {
            metadata.setContentLength(file.getSize());
            metadata.setContentType(file.getContentType());
            amazonS3.putObject(bucket, fileName, file.getInputStream(), metadata);
        } catch (IOException e) {
            throw new BusinessException(ErrorCode.S3_UPLOAD_FAIL, ErrorCode.S3_UPLOAD_FAIL.getMessage());
        }

        return amazonS3.getUrl(bucket, fileName).toString();
    }

    // ✅ S3 이미지 삭제 메서드 추가
    public void delete(String imageUrl) {
        try {
            // URL에서 파일 경로 추출 (버킷 이름 제외한 Key)
            String key = extractKeyFromUrl(imageUrl);
            amazonS3.deleteObject(bucket, key);
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.S3_DELETE_FAIL, "S3 이미지 삭제에 실패했습니다.");
        }
    }

    // ✅ URL에서 S3 키 추출
    private String extractKeyFromUrl(String imageUrl) {
        // https://bucket-name.s3.amazonaws.com/dir/filename.png
        String urlPrefix = "https://" + bucket + ".s3." + amazonS3.getRegionName() + ".amazonaws.com/";
        String key = imageUrl.replace(urlPrefix, "");
        return URLDecoder.decode(key, StandardCharsets.UTF_8); // 혹시 인코딩된 한글 파일명 대응
    }
}
