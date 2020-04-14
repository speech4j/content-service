package org.speech4j.contentservice.mapper;

import org.speech4j.contentservice.controller.ContentController;
import org.speech4j.contentservice.dto.request.ContentRequestDto;
import org.speech4j.contentservice.dto.response.ContentResponseDto;
import org.speech4j.contentservice.entity.ContentBox;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class ContentDtoMapper extends RepresentationModelAssemblerSupport<ContentBox, ContentResponseDto>
        implements AbstractEntityDtoMapper<ContentRequestDto, ContentBox, ContentResponseDto> {

    public ContentDtoMapper() {
        super(ContentController.class, ContentResponseDto.class);
    }

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

    public Page<ContentResponseDto> toDtoPage(Page<ContentBox> entityPage) {
        return entityPage.map(this::toDto);
    }

    @Override
    public ContentResponseDto toModel(ContentBox entity) {
        ContentResponseDto contentResponseDto = toDto(entity);

        contentResponseDto.add(linkTo(
                methodOn(ContentController.class)
                        .findById(entity.getTenantGuid(), entity.getGuid()))
                .withSelfRel());

        return contentResponseDto;
    }
}