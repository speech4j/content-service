package com.speech4j.contentservice.controller;

import com.speech4j.contentservice.dto.request.ContentRequestDto;
import com.speech4j.contentservice.dto.response.ContentResponseDto;
import com.speech4j.contentservice.entity.Compose;
import com.speech4j.contentservice.entity.ContentBox;
import com.speech4j.contentservice.entity.Tag;
import com.speech4j.contentservice.mapper.ContentDtoMapper;
import com.speech4j.contentservice.mapper.TagDtoMapper;
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
import java.util.Set;


@RestController
@RequestMapping("/api/tenants/{tenantId}/contents")
public class ContentController {

    private EntityService<ContentBox> contentService;
    private EntityService<Tag> tagService;
    private EntityService<Compose> composeService;
    private ContentDtoMapper contentMapper;
    private TagDtoMapper tagMapper;

    @Autowired
    public ContentController(EntityService<ContentBox> contentService,
                             EntityService<Tag> tagService,
                             EntityService<Compose> composeService,
                             ContentDtoMapper contentMapper,
                             TagDtoMapper tagMapper) {
        this.contentService = contentService;
        this.tagService = tagService;
        this.composeService = composeService;
        this.contentMapper = contentMapper;
        this.tagMapper = tagMapper;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ContentResponseDto save(@RequestBody ContentRequestDto dto) {
        ContentBox content = contentService.create(contentMapper.toEntity(dto));
        Set<Tag> tags = tagMapper.toEntitySet(dto.getTags());

        tags.forEach((i)->{
            Tag createdTag = tagService.create(i);

            Compose compose = new Compose();
            compose.setContent(content);
            compose.setTag(createdTag);
            composeService.create(compose);
        });

        return contentMapper.toDto(content);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ContentResponseDto findById(@PathVariable String tenantId, @PathVariable String id) {
        return contentMapper.toDto(contentService.findById(id));
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ContentResponseDto update(@RequestBody ContentRequestDto dto, String id) {
        return contentMapper.toDto(contentService.update(contentMapper.toEntity(dto), id));
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<ContentResponseDto> findByTag(@RequestParam("tag") String tag) {
        return contentMapper.toDtoList(contentService.findAllByTag(tag));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete( @PathVariable("id") String id) {
        contentService.deleteById(id);
    }

}
