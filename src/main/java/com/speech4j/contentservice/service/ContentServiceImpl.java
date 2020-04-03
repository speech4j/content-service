package com.speech4j.contentservice.service;

import com.speech4j.contentservice.entity.ContentBox;
import com.speech4j.contentservice.exception.ContentBoxNotFoundException;
import com.speech4j.contentservice.repository.ContentBoxRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ContentServiceImpl implements EntityService{
    private ContentBoxRepository repository;

    @Autowired
    public ContentServiceImpl(ContentBoxRepository repository) {
        this.repository = repository;
    }

    @Override
    public Object create(Object entity) {
        return null;
    }

    @Override
    public Object findById(String id) {
        return null;
    }

    @Override
    public Object update(Object entity, String id) {
        return null;
    }

    @Override
    public List findByTag(String tag) {
        return null;
    }

    @Override
    public void deleteById(String id) {

    }

    private ContentBox findByIdOrThrowException(String id) {
        //Checking if user is found
        return repository.findById(id)
                .orElseThrow(() -> new ContentBoxNotFoundException("Content not found!"));
    }
}
