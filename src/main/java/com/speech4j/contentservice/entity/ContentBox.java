package com.speech4j.contentservice.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ContentBox {
    private String contentGuid;
    private List<String> tags;
    private String contentUrl;
    private String transcript;
    // temporary field, planned to save in separate schema
    private String tenantId;
}