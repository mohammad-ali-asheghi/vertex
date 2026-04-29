package com.vertex.oauth.mapper;

import com.vertex.core.dto.view.ViewPermissionDto;
import com.vertex.core.enums.PermissionEnum;
import com.vertex.oauth.view.ViewPermission;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValueMappingStrategy;
import org.mapstruct.factory.Mappers;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unused")
@Mapper(
        nullValueMappingStrategy = NullValueMappingStrategy.RETURN_NULL,
        componentModel = "spring"
)
public interface ViewPermissionMapper {

    static ViewPermissionMapper get() {
        return Mappers.getMapper(ViewPermissionMapper.class);
    }

    ViewPermissionDto toDto(ViewPermission view);

    List<ViewPermissionDto> toDto(List<ViewPermission> views);

    @AfterMapping
    default void handlePermissions(@MappingTarget ViewPermissionDto dto, ViewPermission entity) {
        if (entity.getPermission() != 0) {
            dto.setPermissions(PermissionEnum.unpackMask(entity.getPermission()));
        } else {
            dto.setPermissions(new ArrayList<>());
        }
    }
}