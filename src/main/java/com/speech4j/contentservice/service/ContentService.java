package com.speech4j.contentservice.service;

import com.speech4j.contentservice.entity.ContentBox;

import java.util.List;

public interface ContentService extends EntityService<ContentBox>{
    List<ContentBox> findByTenantId(String tenantId);
}
