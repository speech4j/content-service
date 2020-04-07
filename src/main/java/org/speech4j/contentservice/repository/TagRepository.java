package org.speech4j.contentservice.repository;

import org.speech4j.contentservice.entity.Tag;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TagRepository extends CrudRepository<Tag,String> {
    List<Tag> findAllByName(String name);
}
