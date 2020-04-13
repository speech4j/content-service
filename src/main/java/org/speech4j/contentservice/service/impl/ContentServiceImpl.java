package org.speech4j.contentservice.service.impl;

import com.amazonaws.services.s3.AmazonS3;
import org.speech4j.contentservice.dto.response.ConfigDto;
import org.speech4j.contentservice.entity.ContentBox;
import org.speech4j.contentservice.exception.ContentNotFoundException;
import org.speech4j.contentservice.repository.ContentBoxRepository;
import org.speech4j.contentservice.service.ContentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Set;

@Service
public class ContentServiceImpl implements ContentService<ContentBox> {
    @Value(value = "${remote.tenant-service.url}")
    private String remoteServiceURL;

    private ContentBoxRepository contentRepository;
    private RestTemplate template;
    private AmazonS3 amazonS3;

    @Autowired
    public ContentServiceImpl(ContentBoxRepository contentRepository,
                              AmazonS3 amazonS3) {
        this.contentRepository = contentRepository;
        this.amazonS3 = amazonS3;
        this.template = new RestTemplate();
    }

    @Override
    public ContentBox create(ContentBox entity) {
        List<ConfigDto> configs = getAllConfigByTenantId(entity.getTenantGuid());
        return contentRepository.save(entity);
    }

    @Override
    public ContentBox findById(String id) {
        return findByIdOrThrowException(id);
    }

    @Override
    public ContentBox update(ContentBox entity, String id) {
        ContentBox content = findByIdOrThrowException(id);
        content.setContentUrl(entity.getContentUrl());
        content.setTranscript(entity.getTranscript());

        return contentRepository.save(content);
    }

    @Override
    public void deleteById(String id) {
        findByIdOrThrowException(id);
        contentRepository.deleteById(id);
    }

    @Override
    public Page<ContentBox> findAllByTags(String tenantId, Set<String> tags, Pageable pageable) {
        return contentRepository.findAllByTags(tenantId, tags, pageable);
    }

    private ContentBox findByIdOrThrowException(String id) {
        //Checking if user is found
        return contentRepository.findById(id)
                .orElseThrow(() -> new ContentNotFoundException("Content not found!"));
    }

    private List<ConfigDto> getAllConfigByTenantId(String tenantId) {
        String url = remoteServiceURL + tenantId + "/configs";
        ResponseEntity<List<ConfigDto>> response =
                template.exchange(url, HttpMethod.GET, null, new ParameterizedTypeReference<List<ConfigDto>>() {});
        return response.getBody();
    }
}
