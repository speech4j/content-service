package com.speech4j.contentservice.controller;

import com.speech4j.contentservice.dto.request.ContentBoxRequestDto;
import com.speech4j.contentservice.dto.response.ContentBoxResponseDto;
import com.speech4j.contentservice.entity.ContentBox;
import com.speech4j.contentservice.mapper.ContentBoxDtoMapper;
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
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RestController
@RequestMapping("/tenants/{tenantId}/contents")
public class ContentController {

    private EntityService<ContentBox> service;
    private ContentBoxDtoMapper mapper;

    @Autowired
    public ContentController(EntityService<ContentBox> service, ContentBoxDtoMapper mapper) {
        this.service = service;
        this.mapper = mapper;
    }
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ContentBoxResponseDto save(@RequestBody ContentBoxRequestDto dto) {
        return mapper.toDto(service.create(mapper.toEntity(dto)));
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ContentBoxResponseDto findById(@PathVariable("id")String id) {
        return mapper.toDto(service.findById(id));
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ContentBoxResponseDto update(@RequestBody ContentBoxRequestDto dto, String id) {
        return mapper.toDto(service.update(mapper.toEntity(dto), id));
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<ContentBoxResponseDto> findByTag(@PathVariable("tag") String tag) {
        return mapper.toDtoList(service.findAllByTag(tag));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete( @PathVariable("id") String id) {
        service.deleteById(id);
    }

}
