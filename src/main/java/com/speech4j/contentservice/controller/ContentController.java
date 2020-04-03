package com.speech4j.contentservice.controller;

import com.speech4j.contentservice.dto.request.ContentRequestDto;
import com.speech4j.contentservice.dto.response.ContentResponseDto;
import com.speech4j.contentservice.entity.ContentBox;
import com.speech4j.contentservice.entity.Tag;
import com.speech4j.contentservice.mapper.ContentDtoMapper;
import com.speech4j.contentservice.service.EntityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RestController
@RequestMapping("/api/tenants/{tenantId}/contents")
public class ContentController {

    private EntityService<ContentBox> contentService;
    private EntityService<Tag> tagService;
    private ContentDtoMapper mapper;

    @Autowired
    public ContentController(EntityService<ContentBox> contentService, EntityService<Tag> tagService, ContentDtoMapper mapper) {
        this.contentService = contentService;
        this.tagService = tagService;
        this.mapper = mapper;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ContentResponseDto save(@RequestBody ContentRequestDto dto) {
        return mapper.toDto(contentService.create(mapper.toEntity(dto)));
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ContentResponseDto findById(@PathVariable("id")String id) {
        return mapper.toDto(contentService.findById(id));
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ContentResponseDto update(@RequestBody ContentRequestDto dto, String id) {
        return mapper.toDto(contentService.update(mapper.toEntity(dto), id));
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<ContentResponseDto> findByTag(@RequestParam("tag") String tag) {
        return mapper.toDtoList(contentService.findAllByTag(tag));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete( @PathVariable("id") String id) {
        contentService.deleteById(id);
    }

}
