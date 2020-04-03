package com.speech4j.contentservice.dto.response;

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
public class ContentBoxResponseDto {
    private String contentGuid;
    private List<String> tags;
    private String contentUrl;
    private String transcript;
    private String tenantId;
}
