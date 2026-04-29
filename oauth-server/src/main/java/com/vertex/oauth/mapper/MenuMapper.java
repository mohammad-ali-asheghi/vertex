package com.vertex.oauth.mapper;

import com.vertex.backendcore.entity.MenuEntity;
import com.vertex.core.dto.MenuModel;
import com.vertex.core.dto.view.ViewMenuDto;
import com.vertex.oauth.view.ViewMenu;
import org.mapstruct.Mapper;
import org.mapstruct.NullValueMappingStrategy;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(
        nullValueMappingStrategy = NullValueMappingStrategy.RETURN_NULL,
        componentModel = "spring"
)
public interface MenuMapper {

    static MenuMapper get() {
        return Mappers.getMapper(MenuMapper.class);
    }

    MenuModel toModel(MenuEntity entity);

    List<MenuModel> toModel(List<MenuEntity> entities);

    List<ViewMenuDto> toDto(List<ViewMenu> entities);
}