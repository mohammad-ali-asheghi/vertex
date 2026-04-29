package com.vertex.oauth.mapper;

import com.vertex.backendcore.entity.redis.CodeTypeDocument;
import com.vertex.core.dto.CodeTypeModel;
import org.mapstruct.Mapper;
import org.mapstruct.NullValueMappingStrategy;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(
        nullValueMappingStrategy = NullValueMappingStrategy.RETURN_NULL,
        componentModel = "spring"
)
public interface CodeTypeMapper {

    static CodeTypeMapper get() {
        return Mappers.getMapper(CodeTypeMapper.class);
    }

    CodeTypeModel toModel(CodeTypeDocument request);

    List<CodeTypeModel> toModel(List<CodeTypeDocument> request);
}