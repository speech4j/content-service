package org.speech4j.contentservice.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.speech4j.contentservice.dto.TagDto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode
@ToString
public class ContentRequestDto {
    @NotEmpty( message = "{field.not.empty}")
    private List<TagDto> tags;
    @NotBlank( message = "{field.not.empty}")
    private String contentUrl;
    @NotBlank( message = "{field.not.empty}")
    private String transcript;
}