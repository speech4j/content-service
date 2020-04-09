package org.speech4j.contentservice.repository;

import org.speech4j.contentservice.entity.ContentBox;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface ContentBoxRepository extends CrudRepository<ContentBox, String> {
    @Query(value = "SELECT distinct c FROM ContentBox c JOIN FETCH c.tags t WHERE t.name IN (:tagNames) AND c.tenantGuid = :tenantId")
    List<ContentBox> findAllByTags(String tenantId, Set<String> tagNames);
}
