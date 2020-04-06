package com.speech4j.contentservice.repository;

import com.speech4j.contentservice.entity.Compose;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ComposeRepository extends CrudRepository<Compose, String> {
    List<Compose> findAllByContentGuid(String contentId);
    List<Compose> findAllByTagGuid(String tagId);
}
