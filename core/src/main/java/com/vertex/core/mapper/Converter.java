package com.vertex.core.mapper;

import java.io.Serializable;
import java.util.List;

@SuppressWarnings("unused")
public interface Converter<E, M> extends Serializable {

    M toRecord(E model);

    E toModel(M record);

    default List<M> toRecord(List<E> modelList) {
        if (modelList == null)
            return null;

        return modelList.stream()
                .map(this::toRecord)
                .toList();
    }

    default List<E> toModel(List<M> recordList) {
        if (recordList == null)
            return null;

        return recordList.stream()
                .map(this::toModel)
                .toList();
    }
}