package com.speech4j.contentservice.mapper;

import com.speech4j.contentservice.dto.request.ContentRequestDto;
import com.speech4j.contentservice.dto.response.ContentResponseDto;
import com.speech4j.contentservice.entity.ContentBox;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class ContentDtoMapper implements AbstractEntityDtoMapper<ContentRequestDto, ContentBox, ContentResponseDto> {
    @Autowired
    TagDtoMapper mapper;

    @Override
    public ContentBox toEntity(ContentRequestDto dto) {
        return ContentBox.builder()
                .tags(mapper.toEntityList(dto.getTags()))
                .contentUrl(dto.getContentUrl())
                .transcript(dto.getTranscript())
                .build();
    }

    @Override
    public ContentResponseDto toDto(ContentBox entity) {
        return ContentResponseDto.builder()
                .contentGuid(entity.getGuid())
                .tags(mapper.toDtoList(entity.getTags()))
                .contentUrl(entity.getContentUrl())
                .transcript(entity.getTranscript())
                .tenantId(entity.getTenantGuid())
                .build();
    }

    public Set<ContentResponseDto> toDtoSet(List<ContentBox> entitySet) {
        return entitySet.stream().map(this::toDto).collect(Collectors.toSet());
    }

}