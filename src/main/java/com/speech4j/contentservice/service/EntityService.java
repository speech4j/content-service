package com.speech4j.contentservice.service;

import java.util.List;

public interface EntityService<E> {
    E create(E entity);

    E findById(String id);

    E update(E entity, String id);

    List<E> findAllByTag(String tag);

    void deleteById(String id);
}
