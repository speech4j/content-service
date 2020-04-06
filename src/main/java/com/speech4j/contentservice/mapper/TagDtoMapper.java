package com.speech4j.contentservice.mapper;

import com.speech4j.contentservice.dto.request.TagDto;
import com.speech4j.contentservice.entity.Tag;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;

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

    public Set<Tag> toEntitySet(Set<TagDto> dtoSet) {
        return dtoSet.stream().map(this::toEntity).collect(Collectors.toSet());
    }
}
