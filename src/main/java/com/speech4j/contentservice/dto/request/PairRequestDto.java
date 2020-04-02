package com.speech4j.contentservice.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.nio.ByteBuffer;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PairRequestDto {
    private String id;
    private String tag;
    private ByteBuffer audio;
    private String url;
    private String transcript;
    private String tenantId;
}
