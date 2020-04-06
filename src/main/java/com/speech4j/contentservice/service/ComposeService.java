package com.speech4j.contentservice.service;

import com.speech4j.contentservice.entity.Compose;

import java.util.List;

public interface ComposeService extends EntityService<Compose> {
    List<Compose> findAllByContentId(String contentId);
    List<Compose> findAllByTagId(String tagId);
}
