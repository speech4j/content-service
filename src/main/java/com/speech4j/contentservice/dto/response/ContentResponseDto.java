package com.speech4j.contentservice.dto.response;

import com.speech4j.contentservice.dto.request.TagDto;
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
public class ContentResponseDto {
    private String contentGuid;
    private Set<TagDto> tags;
    private String contentUrl;
    private String transcript;
    private String tenantId;
}
