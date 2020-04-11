package org.speech4j.contentservice.service;

import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Set;

public interface ContentService<E> {
    E create(E entity);

    E findById(String id);

    E update(E entity, String id);

    void deleteById(String id);

    List<E> findAllByTags(String tenantId, Set<String> tags, Pageable pageable);

}
