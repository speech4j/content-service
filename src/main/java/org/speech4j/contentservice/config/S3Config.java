package org.speech4j.contentservice.config;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class S3Config {

    private static final Logger LOGGER = LoggerFactory.getLogger(S3Config.class);
    @Value(value = "${aws.accessKey}")
    private String accessKey;
    @Value(value = "${aws.secretKey}")
    private String secretKey;
    @Value(value = "${aws.bucketName}")
    private String bucketName;

    @Bean
    public AmazonS3 getAmazonS3() {
        AWSCredentials credentials = new BasicAWSCredentials(
                accessKey,
                secretKey
        );
        AmazonS3 s3client = AmazonS3ClientBuilder
                .standard()
                .withCredentials(new AWSStaticCredentialsProvider(credentials))
                .withRegion(Regions.US_EAST_2)
                .build();

        if(s3client.doesBucketExist(bucketName)) {
            LOGGER.info("Bucket name is not available."
                    + " Try again with a different Bucket name.");
        }
        //Creating a bucket with a particular name (before start work with S3)
        //s3client.createBucket(bucketName);

        return s3client;
    }
}
