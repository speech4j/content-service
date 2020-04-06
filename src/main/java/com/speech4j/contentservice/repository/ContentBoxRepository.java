package com.speech4j.contentservice.repository;

import com.speech4j.contentservice.entity.ContentBox;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ContentBoxRepository extends CrudRepository<ContentBox, String> {
    List<ContentBox> findByTenantGuid(String tenantId);
}
