package com.speech4j.contentservice.service.impl;

import com.speech4j.contentservice.entity.Compose;
import com.speech4j.contentservice.exception.ComposeNotFoundException;
import com.speech4j.contentservice.repository.ComposeRepository;
import com.speech4j.contentservice.service.ComposeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ComposeServiceImpl  implements ComposeService {
    private ComposeRepository repository;

    @Autowired
    public ComposeServiceImpl(ComposeRepository repository) {
        this.repository = repository;
    }

    @Override
    public Compose create(Compose entity) {
        return repository.save(entity);
    }

    @Override
    public Compose findById(String id) {
        return findByIdOrThrowException(id);
    }

    @Override
    public Compose update(Compose entity, String id) {
        Compose compose = findByIdOrThrowException(id);
        return repository.save(compose);
    }

    @Override
    public void deleteById(String id) {
        findByIdOrThrowException(id);
        repository.deleteById(id);
    }

    @Override
    public List<Compose> findAllByContentId(String contentId){
        return repository.findAllByContentGuid(contentId);
    }

    @Override
    public List<Compose> findAllByTagId(String tagId){
        return repository.findAllByTagGuid(tagId);
    }

    private Compose findByIdOrThrowException(String id) {
        //Checking if user is found
        return repository.findById(id)
                .orElseThrow(() -> new ComposeNotFoundException("Compose not found!"));
    }
}
