package org.speech4j.contentservice.service.impl;

import lombok.extern.slf4j.Slf4j;
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
@Slf4j
public class ContentServiceImpl implements ContentService<Content> {
    private ContentBoxRepository contentRepository;

    @Autowired
    public ContentServiceImpl(ContentBoxRepository contentRepository) {
        this.contentRepository = contentRepository;
    }

    @Override
    public Content create(Content entity) {
        Content content = contentRepository.save(entity);
        log.debug("CONTENT-SERVICE: Content with [ id: {}] was successfully created!", entity.getGuid());
        return content;
    }

    @Override
    public Content findById(String id) {
        Content content = findByIdOrThrowException(id);
        log.debug("CONTENT-SERVICE: Content with [ id: {}] was successfully found!", id);
        return content;
    }

    @Override
    public Content update(Content entity) {
        Content content = findByIdOrThrowException(entity.getGuid());
        content.setContentUrl(entity.getContentUrl());
        content.setTranscript(entity.getTranscript());

        Content updatedContent = contentRepository.save(content);
        log.debug("CONTENT-SERVICE: Content with [ id: {}] was successfully updated!", entity.getGuid());
        return updatedContent;
    }

    @Override
    public void deleteById(String id) {
        findByIdOrThrowException(id);
        contentRepository.deleteById(id);
        log.debug("CONTENT-SERVICE: Content with [ id: {}] was successfully deleted!", id);
    }

    @Override
    public Page<Content> findAllByTags(String tenantId, Set<String> tags, Pageable pageable) {
        Page<Content> contents = contentRepository.findAllByTags(tenantId, tags, pageable);
        log.debug("CONTENT-SERVICE: Contents with [ tenantId: {}] were successfully found!", tenantId);
        return contents;
    }

    private Content findByIdOrThrowException(String id) {
        //Checking if user is found
        return contentRepository.findById(id)
                .orElseThrow(() -> new ContentNotFoundException("Content not found!"));
    }
}
