package org.speech4j.contentservice.repository;

import org.speech4j.contentservice.entity.ContentBox;
import org.speech4j.contentservice.entity.Tag;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ContentBoxRepository extends CrudRepository<ContentBox, String> {
    @Query()
    List<ContentBox> findByTagsIn(List<Tag> tags);
}
