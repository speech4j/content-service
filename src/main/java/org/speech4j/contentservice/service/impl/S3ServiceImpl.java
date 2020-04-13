package org.speech4j.contentservice.service.impl;

import com.amazonaws.SdkClientException;
import com.amazonaws.services.mediaconvert.model.S3ServerSideEncryptionType;
import com.amazonaws.services.s3.AmazonS3;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.speech4j.contentservice.exception.InternalServerException;
import org.speech4j.contentservice.service.S3Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

@Service
public class S3ServiceImpl implements S3Service {
    @Value(value = "${aws.bucketName}")
    private String bucketName;
    @Value(value = "${aws.endpointUrl}")
    private String endpointUrl;

    private AmazonS3 amazonS3;
    private static final Logger LOGGER = LoggerFactory.getLogger(S3ServiceImpl.class);

    @Autowired
    public S3ServiceImpl(AmazonS3 amazonS3) {
        this.amazonS3 = amazonS3;
    }

    @Override
    public String uploadAudioFile(String tenantId, MultipartFile multipartFile) {
        String fileUrl = "";
        try {
            File file = convertMultiPartToFile(multipartFile);
            String fileName = generateFileName(tenantId, multipartFile);
            fileUrl = endpointUrl + "/" + bucketName + "/" + fileName;
            amazonS3.putObject(
                    bucketName,
                    fileName,
                    file
            );
        } catch (SdkClientException | IOException e) {
            throw new InternalServerException("AWS server error!");
        }
        return fileUrl;
    }

    private File convertMultiPartToFile(MultipartFile file) throws IOException {
        File convertFile = new File(file.getOriginalFilename());
        try(FileOutputStream fos = new FileOutputStream(convertFile)) {
            fos.write(file.getBytes());
        }

        return convertFile;
    }

    private String generateFileName(String tenantId, MultipartFile multiPart) {
        return  tenantId + "/" + multiPart.getOriginalFilename().replace(" ", "_");
    }
}
