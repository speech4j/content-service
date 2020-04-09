package org.speech4j.contentservice.service.impl;

import org.speech4j.contentservice.entity.ContentBox;
import org.speech4j.contentservice.exception.ContentNotFoundException;
import org.speech4j.contentservice.repository.ContentBoxRepository;
import org.speech4j.contentservice.service.ContentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
public class ContentServiceImpl implements ContentService {
    private ContentBoxRepository contentRepository;

    @Autowired
    public ContentServiceImpl(ContentBoxRepository contentRepository) {
        this.contentRepository = contentRepository;
    }

    @Override
    public ContentBox create(ContentBox entity) {
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
    public List<ContentBox> findAllByTags(String tenantId, Set<String> tags) {
        return contentRepository.findAllByTags(tenantId, tags);
    }

    private ContentBox findByIdOrThrowException(String id) {
        //Checking if user is found
        return contentRepository.findById(id)
                .orElseThrow(() -> new ContentNotFoundException("Content not found!"));
    }
}
