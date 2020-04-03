package com.speech4j.contentservice.mapper;

import com.speech4j.contentservice.dto.request.ContentRequestDto;
import com.speech4j.contentservice.dto.response.ContentResponseDto;
import com.speech4j.contentservice.entity.ContentBox;
import org.springframework.stereotype.Component;

@Component
public class ContentDtoMapper implements AbstractEntityDtoMapper<ContentRequestDto, ContentBox, ContentResponseDto> {
    @Override
    public ContentBox toEntity(ContentRequestDto dto) {
        return ContentBox.builder()
                .contentUrl(dto.getContentUrl())
                .transcript(dto.getTranscript())
                .tenantId(dto.getTenantId())
                .build();
    }

    @Override
    public ContentResponseDto toDto(ContentBox entity) {
        return ContentResponseDto.builder()
                .contentGuid(entity.getGuid())
                .contentUrl(entity.getContentUrl())
                .transcript(entity.getTranscript())
                .tenantId(entity.getTenantId())
                .build();
    }
}