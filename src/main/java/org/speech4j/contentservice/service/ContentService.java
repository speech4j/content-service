package org.speech4j.contentservice.service;

import org.speech4j.contentservice.entity.ContentBox;

import java.util.List;
import java.util.Set;

public interface ContentService extends EntityService<ContentBox>{
    List<ContentBox> findAllByTags(String tenantId, Set<String> tags);
}
