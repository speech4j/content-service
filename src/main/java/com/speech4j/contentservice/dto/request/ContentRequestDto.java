package com.speech4j.contentservice.dto.request;

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
public class ContentRequestDto {
    private List<TagDto> tags;
    private String contentUrl;
    private String transcript;
    private String tenantId;
}