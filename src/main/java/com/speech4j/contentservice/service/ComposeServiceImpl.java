package com.speech4j.contentservice.service;

import com.speech4j.contentservice.entity.Compose;
import com.speech4j.contentservice.exception.ComposeNotFoundException;
import com.speech4j.contentservice.repository.ComposeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ComposeServiceImpl  implements EntityService<Compose> {
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
    public List<Compose> findAllByTag(String tag) {
        return null;
    }

    @Override
    public void deleteById(String id) {
        findByIdOrThrowException(id);
        repository.deleteById(id);
    }

    private Compose findByIdOrThrowException(String id) {
        //Checking if user is found
        return repository.findById(id)
                .orElseThrow(() -> new ComposeNotFoundException("Compose not found!"));
    }
}
