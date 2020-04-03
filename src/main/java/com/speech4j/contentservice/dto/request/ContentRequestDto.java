package com.speech4j.contentservice.dto.request;

import com.speech4j.contentservice.entity.Tag;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ContentRequestDto {
    private Set<Tag> tags;
    private String contentUrl;
    private String transcript;
    private String tenantId;
}