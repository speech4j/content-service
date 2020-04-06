package com.speech4j.contentservice.mapper;

import java.util.List;
import java.util.stream.Collectors;

public interface AbstractEntityDtoMapper<D, E, R> {
    E toEntity(D dto);

    R toDto(E entity);

    default List<R> toDtoList(List<E> entityList) {
        return entityList.stream().map(this::toDto).collect(Collectors.toList());
    }

    default List<E> toEntityList(List<D> dtoList) {
        return dtoList.stream().map(this::toEntity).collect(Collectors.toList());
    }
}