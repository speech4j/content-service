package com.speech4j.contentservice.mapper;

import com.speech4j.contentservice.dto.request.ContentBoxRequestDto;
import com.speech4j.contentservice.dto.response.ContentBoxResponseDto;
import com.speech4j.contentservice.entity.ContentBox;
import org.springframework.stereotype.Component;

@Component
public class ContentBoxDtoMapper implements AbstractEntityDtoMapper<ContentBoxRequestDto, ContentBox, ContentBoxResponseDto> {
    @Override
    public ContentBox toEntity(ContentBoxRequestDto dto) {
        return ContentBox.builder()
                .tags(dto.getTags())
                .contentUrl(dto.getContentUrl())
                .transcript(dto.getTranscript())
                .tenantId(dto.getTenantId())
                .build();
    }

    @Override
    public ContentBoxResponseDto toDto(ContentBox entity) {
        return ContentBoxResponseDto.builder()
                .tags(entity.getTags())
                .contentGuid(entity.getGuid())
                .contentUrl(entity.getContentUrl())
                .transcript(entity.getTranscript())
                .tenantId(entity.getTenantId())
                .build();
    }
}