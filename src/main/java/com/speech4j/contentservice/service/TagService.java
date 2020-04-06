package com.speech4j.contentservice.service;

import com.speech4j.contentservice.entity.Tag;

import java.util.List;

public interface TagService extends EntityService<Tag>{
    List<Tag> findAllByName(String name);
}
