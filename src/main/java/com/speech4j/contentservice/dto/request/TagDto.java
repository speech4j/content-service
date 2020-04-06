package com.speech4j.contentservice.dto.request;

import com.speech4j.contentservice.dto.validation.ExistData;
import com.speech4j.contentservice.dto.validation.NewData;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TagDto {
    @NotBlank(groups = {NewData.class, ExistData.class}, message = "{field.not.empty}")
    private String name;
}
