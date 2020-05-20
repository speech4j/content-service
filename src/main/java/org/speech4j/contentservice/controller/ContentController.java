package org.speech4j.contentservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.speech4j.contentservice.dto.request.ContentRequestDto;
import org.speech4j.contentservice.dto.request.ContentUploadRequestDto;
import org.speech4j.contentservice.dto.response.ContentResponseDto;
import org.speech4j.contentservice.entity.Content;
import org.speech4j.contentservice.exception.ContentNotFoundException;
import org.speech4j.contentservice.mapper.ContentDtoMapper;
import org.speech4j.contentservice.service.ContentService;
import org.speech4j.contentservice.service.S3Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.Set;


@RestController
@RequestMapping("/api/tenants/{tenantId}/contents")
public class ContentController {
    private ContentService<Content> contentService;
    private ContentDtoMapper contentMapper;
    private S3Service s3Service;
    private PagedResourcesAssembler<Content> pagedResourcesAssembler;

    @Autowired
    public ContentController(ContentService contentService,
                             ContentDtoMapper contentMapper,
                             S3Service s3Service,
                             PagedResourcesAssembler<Content> pagedResourcesAssembler) {
        this.contentService = contentService;
        this.contentMapper = contentMapper;
        this.s3Service = s3Service;
        this.pagedResourcesAssembler = pagedResourcesAssembler;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(
            summary = "Create content by the tenant ID",
            responses = {
                    @ApiResponse(responseCode = "400", description = "Validation exception")})
    public ContentResponseDto create(
            @Parameter(description = "Tenant ID for saving", required = true)
            @PathVariable String tenantId,
            @Parameter(description = "Content OBJECT that needs to be added to database", required = true)
            @Validated @RequestBody ContentRequestDto dto
    ) {
        Content content = contentMapper.toEntity(dto);
        content.setTenantGuid(tenantId);

        return contentMapper.toDto(contentService.create(content))
                .add(new Link(ServletUriComponentsBuilder.fromCurrentRequest().build().toUriString()));
    }

    @GetMapping("/{contentId}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(
            summary = "Get content by the tenant ID and content ID",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Content is found"),
                    @ApiResponse(responseCode = "404", description = "Content not found")})
    public ContentResponseDto findById(
            @Parameter(description = "Tenant ID for get", required = true)
            @PathVariable String tenantId,
            @Parameter(description = "Content ID for get", required = true)
            @PathVariable String contentId
    ) {
        checkIfExist(tenantId, contentId);
        return contentMapper.toDto(contentService.findById(contentId))
                .add(new Link(ServletUriComponentsBuilder.fromCurrentRequest().build().toUriString()));
    }

    @PutMapping("/{contentId}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(
            summary = "Update entity by the tenant ID and content ID",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Content is updated"),
                    @ApiResponse(responseCode = "404", description = "Content not found"),
                    @ApiResponse(responseCode = "400", description = "Validation exception")})
    public ContentResponseDto update(
            @Parameter(description = "Content OBJECT that needs to be update", required = true)
            @Validated @RequestBody ContentRequestDto dto,
            @Parameter(description = "Tenant ID for update", required = true)
            @PathVariable String tenantId,
            @Parameter(description = "Content ID for update", required = true)
            @PathVariable String contentId
    ) {
        checkIfExist(tenantId, contentId);
        Content content = contentMapper.toEntity(dto);
        content.setGuid(contentId);
        return contentMapper.toDto(contentService.update(content))
                .add(new Link(ServletUriComponentsBuilder.fromCurrentRequest().build().toUriString()));
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @Operation(
            summary = "Get all configs by the list of TAGs and by the tenant ID",
            description = "Get list of contents",
            responses = {
                    @ApiResponse(responseCode = "404", description = "Content not found"),
                    @ApiResponse(responseCode = "200", description = "OK")})
    public PagedModel<ContentResponseDto> findAllByTags(
            @Parameter(description = "Tenant ID for get", required = true)
            @PathVariable String tenantId,
            @Parameter(description = "List of the TAG NAMES for get", required = true)
            @RequestParam Set<String> tagNames,
            @PageableDefault(page = 0, size = 2, sort = {"guid"}, direction = Sort.Direction.ASC) Pageable pageable
    ) {
        Page<Content> contents = contentService.findAllByTags(tenantId, tagNames, pageable);
        if (contents.isEmpty()){
            throw new ContentNotFoundException("Content not found!");
        }
        return pagedResourcesAssembler.toModel(contents, contentMapper);
    }

    @DeleteMapping("/{contentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(
            summary = "Delete content by the tenant ID and content ID",
            responses = {
                    @ApiResponse(responseCode = "404", description = "Content not found")})
    public void delete(
            @Parameter(description = "Tenant ID for delete", required = true)
            @PathVariable String tenantId,
            @Parameter(description = "Content ID for delete", required = true)
            @PathVariable String contentId
    ) {
        checkIfExist(tenantId, contentId);
        contentService.deleteById(contentId);
    }

    @PostMapping(value = "/upload" , consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.OK)
    @Operation(
            summary = "Upload an audio file and save the content with a particular URL to database by the tenant ID",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Content is updated"),
                    @ApiResponse(responseCode = "500", description = "AWS server error"),
                    @ApiResponse(responseCode = "400", description = "Validation exception"),
                    @ApiResponse(responseCode = "404", description = "Content not found")})
    public String uploadAudioFile(
            @Parameter(description = "Tenant ID for the saving content", required = true)
            @PathVariable String tenantId,
            @Parameter(description = "Audio file for uploading to AWS S3", required = true)
            @RequestPart MultipartFile file,
            @Parameter(description = "Content OBJECT for saving to db with an audio url", required = true)
            @RequestPart ContentUploadRequestDto dto
    ){
        Content content = contentMapper.fromUploadEntity(dto);
        content.setTenantGuid(tenantId);
        Content createdContent = contentService.create(content);

        String url = s3Service.uploadAudioFile(tenantId, createdContent.getGuid(), file);
        createdContent.setContentUrl(url);
        contentService.update(createdContent);

        return "File was uploaded successfully! Your current identifier is: " + createdContent.getGuid();
    }

    @GetMapping(value = "/{contentId}/download")
    @ResponseStatus(HttpStatus.OK)
    @Operation(
            summary = "Download an audio file by the tenant ID and content ID",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Content is updated"),
                    @ApiResponse(responseCode = "500", description = "AWS server error"),
                    @ApiResponse(responseCode = "400", description = "Validation exception"),
                    @ApiResponse(responseCode = "404", description = "Content not found")})
    public ResponseEntity<ByteArrayResource> getAudioFile(
            @Parameter(description = "Tenant ID for get", required = true)
            @PathVariable String tenantId,
            @Parameter(description = "Content ID for get", required = true)
            @PathVariable String contentId
    ){
        checkIfExist(tenantId, contentId);
        String contentUrl = contentService.findById(contentId).getContentUrl();
        String filename = contentUrl.substring(contentUrl.lastIndexOf('/') + 1);

        final ByteArrayResource byteArrayResource =
                new ByteArrayResource(s3Service.downloadAudioFile(tenantId + "/" + filename));
        return ResponseEntity
                .ok()
                .contentLength(byteArrayResource.contentLength())
                .contentType(MediaType.parseMediaType("multipart/mixed;boundary=gc0p4Jq0M2Yt08jU534c0p"))
                .header("Content-Disposition", "attachment; filename=" + filename)
                .body(byteArrayResource);
    }

    private void checkIfExist(String tenantId, String contentId){
        Content content = contentService.findById(contentId);
        if (!content.getTenantGuid().equals(tenantId)) {
            throw new ContentNotFoundException("Content not found!");
        }
    }

}
