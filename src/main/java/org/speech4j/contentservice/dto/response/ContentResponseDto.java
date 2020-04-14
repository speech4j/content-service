package org.speech4j.contentservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.speech4j.contentservice.dto.TagDto;
import org.springframework.hateoas.RepresentationModel;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
@EqualsAndHashCode(callSuper = false)
public class ContentResponseDto extends RepresentationModel<ContentResponseDto> {
    private String contentGuid;
    private List<TagDto> tags;
    private String contentUrl;
    private String transcript;
    private String tenantId;
}
