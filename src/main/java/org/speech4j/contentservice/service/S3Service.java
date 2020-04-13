package org.speech4j.contentservice.service;

import org.springframework.web.multipart.MultipartFile;

public interface S3Service {
    String uploadAudioFile(String tenantId, MultipartFile file);
}
