package com.speech4j.contentservice.mapper;

import com.speech4j.contentservice.dto.request.PairRequestDto;
import com.speech4j.contentservice.dto.response.PairResponseDto;
import com.speech4j.contentservice.entity.Pair;
import org.springframework.stereotype.Component;

@Component
public class PairDtoMapper implements AbstractEntityDtoMapper<PairRequestDto, Pair, PairResponseDto> {
    @Override
    public Pair toEntity(PairRequestDto dto) {
        return Pair.builder()
                .tag(dto.getTag())
                .audio(dto.getAudio())
                .url(dto.getUrl())
                .transcript(dto.getTranscript())
                .tenantId(dto.getTenantId())
                .build();
    }

    @Override
    public PairResponseDto toDto(Pair entity) {
        return PairResponseDto.builder()
                .id(entity.getId())
                .audio(entity.getAudio())
                .url(entity.getUrl())
                .transcript(entity.getTranscript())
                .tenantId(entity.getTenantId())
                .build();
    }
}
