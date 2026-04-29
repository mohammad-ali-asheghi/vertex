package com.vertex.oauth.mapper;

import com.vertex.backendcore.entity.redis.CodeTypeItemDocument;
import com.vertex.core.dto.CodeTypeItemModel;
import org.mapstruct.Mapper;
import org.mapstruct.NullValueMappingStrategy;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(
        nullValueMappingStrategy = NullValueMappingStrategy.RETURN_NULL,
        componentModel = "spring"
)
public interface CodeTypeItemMapper {

    static CodeTypeItemMapper get() {
        return Mappers.getMapper(CodeTypeItemMapper.class);
    }

    CodeTypeItemDocument toDocument(CodeTypeItemModel request);

    CodeTypeItemModel toModel(CodeTypeItemDocument request);

    List<CodeTypeItemModel> toModel(List<CodeTypeItemDocument> request);
}