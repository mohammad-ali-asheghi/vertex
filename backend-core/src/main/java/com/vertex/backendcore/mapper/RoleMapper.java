package com.vertex.backendcore.mapper;

import com.vertex.backendcore.entity.RoleEntity;
import com.vertex.core.dto.RoleModel;
import org.mapstruct.Mapper;
import org.mapstruct.NullValueMappingStrategy;
import org.mapstruct.factory.Mappers;

import java.util.List;

@SuppressWarnings("unused")
@Mapper(
        nullValueMappingStrategy = NullValueMappingStrategy.RETURN_NULL,
        componentModel = "spring"
)
public interface RoleMapper {

    static RoleMapper get() {
        return Mappers.getMapper(RoleMapper.class);
    }

    RoleModel entityToModel(RoleEntity entity);

    RoleEntity modelToEntity(RoleModel model);

    List<RoleModel> entitiesToModels(List<RoleEntity> entities);
}