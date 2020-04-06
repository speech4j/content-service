package com.speech4j.contentservice.repository;

import com.speech4j.contentservice.entity.Compose;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ComposeRepository extends CrudRepository<Compose, String> {
}
