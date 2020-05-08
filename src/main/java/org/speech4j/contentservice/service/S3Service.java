package org.speech4j.contentservice.service;

import org.springframework.web.multipart.MultipartFile;

public interface S3Service {
    String uploadAudioFile(String tenantId, String contentId, MultipartFile file);

    byte[] downloadAudioFile(String tenantId, String contentId);

    void deleteFolder(String bucketName, String folderName);
}
