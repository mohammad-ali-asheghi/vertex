package com.vertex.backendcore.mapper;

import com.vertex.backendcore.entity.ApplicationEntity;
import com.vertex.core.dto.ApplicationModel;
import org.mapstruct.Mapper;
import org.mapstruct.NullValueMappingStrategy;
import org.mapstruct.factory.Mappers;

import java.util.List;

@SuppressWarnings("unused")
@Mapper(
        nullValueMappingStrategy = NullValueMappingStrategy.RETURN_NULL,
        componentModel = "spring"
)
public interface ApplicationMapper {

    static ApplicationMapper get() {
        return Mappers.getMapper(ApplicationMapper.class);
    }

    ApplicationModel entityToModel(ApplicationEntity entity);

    ApplicationEntity modelToEntity(ApplicationModel model);

    List<ApplicationModel> entitiesToModels(List<ApplicationEntity> entities);
}