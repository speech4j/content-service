package com.speech4j.contentservice.dto.request;

import com.speech4j.contentservice.dto.validation.ExistData;
import com.speech4j.contentservice.dto.validation.NewData;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode
public class ContentRequestDto {
    @NotEmpty(groups = {NewData.class, ExistData.class}, message = "{field.not.empty}")
    private List<TagDto> tags;
    @NotBlank(groups = {NewData.class, ExistData.class}, message = "{field.not.empty}")
    private String contentUrl;
    @NotBlank(groups = {NewData.class, ExistData.class}, message = "{field.not.empty}")
    private String transcript;
}