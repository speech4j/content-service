package org.speech4j.contentservice.aws;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import lombok.extern.slf4j.Slf4j;
import org.speech4j.contentservice.dto.response.ApiName;
import org.speech4j.contentservice.dto.response.ConfigDto;
import org.speech4j.contentservice.exception.InternalServerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Map;

import static java.util.Objects.isNull;

@Configuration
@Slf4j
public class S3Config {
    private TenantClient tenantClient;
    private Map<String, Object> credentials;

    @Autowired
    public S3Config(TenantClient tenantClient) {
        this.tenantClient = tenantClient;
    }

    @PostConstruct
    public void init(){
        List<ConfigDto> configs = tenantClient.getAllConfigByTenantId("speech4j");
        configs.forEach(config->{
            if(config.getApiName().equals(ApiName.AWS)) {
              credentials = config.getCredentials();
            }
        });
        log.info("AWS: Config was successfully retrieved!");

        if (isNull(credentials)){
            throw new InternalServerException("AWS: There aren't credentials specified to S3!");
        }
        System.setProperty("aws.bucketName", credentials.get("bucket_name").toString());
        System.setProperty("aws.endpointUrl", credentials.get("endpoint_url").toString());
    }

    @Bean(name = "amazonS3")
    public AmazonS3 getAmazonS3() {
        AWSCredentials awsCredentials = new BasicAWSCredentials(
                credentials.get("access_key").toString(),
                credentials.get("secret_key").toString()
        );
        AmazonS3 s3client = AmazonS3ClientBuilder
                .standard()
                .withCredentials(new AWSStaticCredentialsProvider(awsCredentials))
                .withRegion(Regions.US_EAST_2)
                .build();
        if(s3client.doesBucketExistV2(credentials.get("bucket_name").toString())) {
            log.info("Bucket name is not available."
                    + " Try again with a different Bucket name.");
        }
        return s3client;
    }
}
