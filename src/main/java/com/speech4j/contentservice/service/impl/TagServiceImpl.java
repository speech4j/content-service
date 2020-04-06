package com.speech4j.contentservice.service.impl;

import com.speech4j.contentservice.entity.Tag;
import com.speech4j.contentservice.exception.TagNotFoundException;
import com.speech4j.contentservice.repository.TagRepository;
import com.speech4j.contentservice.service.TagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TagServiceImpl implements TagService {
    private TagRepository repository;

    @Autowired
    public TagServiceImpl(TagRepository repository) {
        this.repository = repository;
    }

    @Override
    public Tag create(Tag entity) {
        return repository.save(entity);
    }

    @Override
    public Tag findById(String id) {
        return findByIdOrThrowException(id);
    }

    @Override
    public Tag update(Tag entity, String id) {
        Tag tag = findByIdOrThrowException(id);
        tag.setName(entity.getName());

        return repository.save(tag);
    }

    @Override
    public void deleteById(String id) {
        findByIdOrThrowException(id);
        repository.deleteById(id);
    }

    private Tag findByIdOrThrowException(String id) {
        //Checking if user is found
        return repository.findById(id)
                .orElseThrow(() -> new TagNotFoundException("Tag not found!"));
    }
}
