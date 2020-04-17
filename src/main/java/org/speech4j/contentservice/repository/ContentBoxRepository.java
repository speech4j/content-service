package org.speech4j.contentservice.repository;

import org.speech4j.contentservice.entity.Content;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.Set;

public interface ContentBoxRepository extends PagingAndSortingRepository<Content, String> {
    @Query(value =
            "SELECT distinct c " +
            "FROM Content c " +
            "JOIN c.tags t" +
            " WHERE t.name " +
            "IN (:tagNames) " +
            "AND c.tenantGuid = :tenantId")
    Page<Content> findAllByTags(String tenantId, Set<String> tagNames, Pageable pageable);
}
