package com.speech4j.contentservice.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode
public class ContentRequestDto {
    @NotBlank(message = "{field.not.empty}")
    private List<TagDto> tags;
    @NotBlank(message = "{field.not.empty}")
    private String contentUrl;
    @NotBlank(message = "{field.not.empty}")
    private String transcript;
}