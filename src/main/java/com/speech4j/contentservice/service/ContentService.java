package com.speech4j.contentservice.service;

import com.speech4j.contentservice.entity.ContentBox;
import com.speech4j.contentservice.entity.Tag;

import java.util.List;

public interface ContentService extends EntityService<ContentBox>{
    List<Tag> findAllByName(String name);
}
