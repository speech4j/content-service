package com.speech4j.contentservice.repository;

import com.speech4j.contentservice.entity.Tag;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TagRepository extends CrudRepository<Tag,String> {
}
