package org.speech4j.contentservice.repository;

import org.speech4j.contentservice.entity.ContentBox;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface ContentBoxRepository extends PagingAndSortingRepository<ContentBox, String> {
    @Query(value =
            "SELECT distinct c " +
            "FROM ContentBox c " +
            "JOIN c.tags t" +
            " WHERE t.name " +
            "IN (:tagNames) " +
            "AND c.tenantGuid = :tenantId")
    Page<ContentBox> findAllByTags(String tenantId, Set<String> tagNames, Pageable pageable);
}
