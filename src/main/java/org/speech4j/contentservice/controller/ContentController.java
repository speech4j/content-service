package org.speech4j.contentservice.controller;

import org.speech4j.contentservice.dto.request.ContentRequestDto;
import org.speech4j.contentservice.dto.response.ContentResponseDto;
import org.speech4j.contentservice.dto.validation.ExistData;
import org.speech4j.contentservice.dto.validation.NewData;
import org.speech4j.contentservice.entity.ContentBox;
import org.speech4j.contentservice.entity.Tag;
import org.speech4j.contentservice.exception.ContentNotFoundException;
import org.speech4j.contentservice.mapper.ContentDtoMapper;
import org.speech4j.contentservice.service.ContentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


@RestController
@RequestMapping("/api/tenants/{tenantId}/contents")
public class ContentController {

    private ContentService contentService;
    private ContentDtoMapper contentMapper;

    @Autowired
    public ContentController(ContentService contentService,
                            ContentDtoMapper contentMapper) {
        this.contentService = contentService;
        this.contentMapper = contentMapper;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ContentResponseDto save(@PathVariable String tenantId,
                                   @Validated({NewData.class}) @RequestBody ContentRequestDto dto) {
        ContentBox contentBox = contentMapper.toEntity(dto);
        contentBox.setTenantGuid(tenantId);
        return contentMapper.toDto(contentService.create(contentBox));
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ContentResponseDto findById(@PathVariable String tenantId, @PathVariable String id) {
        checkIfExist(tenantId, id);
        return contentMapper.toDto(contentService.findById(id));
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ContentResponseDto update(@Validated({ExistData.class}) @RequestBody ContentRequestDto dto,
                                     @PathVariable String tenantId,
                                     @PathVariable String id) {
        checkIfExist(tenantId, id);
        return contentMapper.toDto(contentService.update(contentMapper.toEntity(dto), id));
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<ContentResponseDto> findByTags(@PathVariable String tenantId, @RequestParam Set<String> tagNames) {
        List<ContentBox> contents = contentService.findAllByTags(tenantId, tagNames);
        if (contents.isEmpty()){
            throw new ContentNotFoundException("Content not found!");
        }
        return contentMapper.toDtoList(contents);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable String tenantId, @PathVariable("id") String id) {
        checkIfExist(tenantId, id);
        contentService.deleteById(id);
    }

    private void checkIfExist(String tenantId, String id){
        ContentBox content = contentService.findById(id);
        if (!content.getTenantGuid().equals(tenantId)) {
            throw new ContentNotFoundException("Content not found!");
        }
    }

}
