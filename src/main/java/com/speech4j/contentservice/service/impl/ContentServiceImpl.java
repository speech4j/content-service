package com.speech4j.contentservice.service.impl;

import com.speech4j.contentservice.entity.ContentBox;
import com.speech4j.contentservice.exception.ContentNotFoundException;
import com.speech4j.contentservice.repository.ContentBoxRepository;
import com.speech4j.contentservice.service.ContentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ContentServiceImpl implements ContentService {
    private ContentBoxRepository repository;

    @Autowired
    public ContentServiceImpl(ContentBoxRepository repository) {
        this.repository = repository;
    }

    @Override
    public ContentBox create(ContentBox entity) {
        return repository.save(entity);
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

        return repository.save(content);
    }

    @Override
    public void deleteById(String id) {
        findByIdOrThrowException(id);
        repository.deleteById(id);
    }

    @Override
    public List<ContentBox> findByTenantId(String tenantId) {
        return repository.findByTenantId(tenantId);
    }

    private ContentBox findByIdOrThrowException(String id) {
        //Checking if user is found
        return repository.findById(id)
                .orElseThrow(() -> new ContentNotFoundException("Content not found!"));
    }
}
