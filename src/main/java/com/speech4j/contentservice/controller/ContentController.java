package com.speech4j.contentservice.controller;

import com.speech4j.contentservice.dto.request.ContentRequestDto;
import com.speech4j.contentservice.dto.response.ContentResponseDto;
import com.speech4j.contentservice.entity.ContentBox;
import com.speech4j.contentservice.entity.Tag;
import com.speech4j.contentservice.exception.ContentNotFoundException;
import com.speech4j.contentservice.mapper.ContentDtoMapper;
import com.speech4j.contentservice.mapper.TagDtoMapper;
import com.speech4j.contentservice.service.ContentService;
import com.speech4j.contentservice.service.TagService;
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

    private ContentService contentService;
    private TagService tagService;
    private ContentDtoMapper contentMapper;
    private TagDtoMapper tagMapper;

    @Autowired
    public ContentController(ContentService contentService,
                            TagService tagService,
                            ContentDtoMapper contentMapper,
                            TagDtoMapper tagMapper) {
        this.contentService = contentService;
        this.tagService = tagService;
        this.contentMapper = contentMapper;
        this.tagMapper = tagMapper;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ContentResponseDto save(@PathVariable String tenantId, @RequestBody ContentRequestDto dto) {
        ContentBox contentBox = contentMapper.toEntity(dto);
        contentBox.setTenantGuid(tenantId);
        return contentMapper.toDto(contentService.create(contentBox));
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ContentResponseDto findById(@PathVariable String tenantId, @PathVariable String id) {
        checkIfExist(tenantId, id);
        return null;
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ContentResponseDto update(@RequestBody ContentRequestDto dto,
                                     @PathVariable String tenantId,
                                     @PathVariable String id) {
        checkIfExist(tenantId, id);
        return contentMapper.toDto(contentService.update(contentMapper.toEntity(dto), id));
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<ContentResponseDto> findByTag( @PathVariable String tenantId, @RequestParam("tag") String tagName) {
        //contentService.findByTenantId(tenantId);
        List<Tag> tags = tagService.findAllByName(tagName);
        return null ;
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
