package org.speech4j.contentservice.mapper;

import org.speech4j.contentservice.dto.TagDto;
import org.speech4j.contentservice.entity.Tag;
import org.springframework.stereotype.Component;

@Component
public class TagDtoMapper implements AbstractEntityDtoMapper<TagDto, Tag, TagDto> {
    @Override
    public Tag toEntity(TagDto dto) {
        return Tag.builder()
                .name(dto.getName())
                .build();
    }

    @Override
    public TagDto toDto(Tag entity) {
        return TagDto.builder()
                .name(entity.getName())
                .build();
    }
}
