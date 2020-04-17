package org.speech4j.contentservice.service.impl;

import org.speech4j.contentservice.dto.response.ConfigDto;
import org.speech4j.contentservice.entity.Content;
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
public class ContentServiceImpl implements ContentService<Content> {
    @Value(value = "${remote.tenant-service.url}")
    private String remoteServiceURL;

    private ContentBoxRepository contentRepository;
    private RestTemplate template;

    @Autowired
    public ContentServiceImpl(ContentBoxRepository contentRepository) {
        this.contentRepository = contentRepository;
        this.template = new RestTemplate();
    }

    @Override
    public Content create(Content entity) {
        //List<ConfigDto> configs = getAllConfigByTenantId(entity.getTenantGuid());
        return contentRepository.save(entity);
    }

    @Override
    public Content findById(String id) {
        return findByIdOrThrowException(id);
    }

    @Override
    public Content update(Content entity, String id) {
        Content content = findByIdOrThrowException(id);
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
    public Page<Content> findAllByTags(String tenantId, Set<String> tags, Pageable pageable) {
        return contentRepository.findAllByTags(tenantId, tags, pageable);
    }

    private Content findByIdOrThrowException(String id) {
        //Checking if user is found
        return contentRepository.findById(id)
                .orElseThrow(() -> new ContentNotFoundException("Content not found!"));
    }

    private List<ConfigDto> getAllConfigByTenantId(String tenantId) {
        String url = remoteServiceURL + tenantId + "/configs";
        ResponseEntity<List<ConfigDto>> response =
                template.exchange(url, HttpMethod.GET, null, new ParameterizedTypeReference<List<ConfigDto>>(){});
        return response.getBody();
    }
}
