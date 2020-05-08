package org.speech4j.contentservice.service.impl;

import com.amazonaws.SdkClientException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.amazonaws.util.IOUtils;
import lombok.extern.slf4j.Slf4j;
import org.speech4j.contentservice.exception.ContentNotFoundException;
import org.speech4j.contentservice.exception.InternalServerException;
import org.speech4j.contentservice.service.S3Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@Service
@Slf4j
public class S3ServiceImpl implements S3Service {
    @Value(value = "${aws.fileExtension}")
    private String extension = ".mp3";

    @Value(value = "${aws.bucketName}")
    private String bucketName;
    @Value(value = "${aws.endpointUrl}")
    private String endpointUrl;

    private AmazonS3 amazonS3;

    @Autowired
    public S3ServiceImpl(AmazonS3 amazonS3) {
        this.amazonS3 = amazonS3;
    }

    @Override
    public String uploadAudioFile(String tenantId, String contentId, MultipartFile multipartFile) {
        String fileUrl = "";
        try {
            InputStream inputStream = multipartFile.getInputStream();
            ObjectMetadata meta = new ObjectMetadata();
            meta.setContentLength(inputStream.available());
            String fileName = getFileName(tenantId,contentId);
            fileUrl = endpointUrl + "/" + bucketName + "/" + fileName;

            amazonS3.putObject(bucketName, fileName, inputStream, meta);
            log.debug("S3-SERVICE: File was successfully uploaded to S3 bucket!");
        } catch (SdkClientException | IOException e) {
            throw new InternalServerException("AWS server error!");
        }
        return fileUrl;
    }

    @Override
    public byte[] downloadAudioFile(String tenantId, String contentId) {
        String fileName = getFileName(tenantId,contentId);
        byte[] content = null;
        try(S3Object s3Object = amazonS3.getObject(bucketName, fileName)){
            S3ObjectInputStream inputStream = s3Object.getObjectContent();
            content = IOUtils.toByteArray(inputStream);
        } catch (IOException e) {
           throw new ContentNotFoundException("Content not found!");
        }
        return content;
    }

    @Override
    public void deleteFolder(String bucketName, String folderName) {
        List fileList = amazonS3.listObjects(bucketName, folderName).getObjectSummaries();
        for (Object object : fileList) {
            S3ObjectSummary file = (S3ObjectSummary) object;
            amazonS3.deleteObject(bucketName, file.getKey());
        }
        amazonS3.deleteObject(bucketName, folderName);
    }

    private String getFileName(String tenantId, String contentId) {
        return tenantId + "/" + contentId + extension;
    }
}
