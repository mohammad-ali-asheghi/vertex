package com.vertex.backendcore.mapper;

import com.vertex.backendcore.entity.UserInfoEntity;
import com.vertex.core.dto.UserInfoModel;
import org.mapstruct.Mapper;
import org.mapstruct.NullValueMappingStrategy;
import org.mapstruct.factory.Mappers;

import java.util.List;

@SuppressWarnings("unused")
@Mapper(
        nullValueMappingStrategy = NullValueMappingStrategy.RETURN_NULL,
        componentModel = "spring"
)
public interface UserInfoMapper {

    static UserInfoMapper get() {
        return Mappers.getMapper(UserInfoMapper.class);
    }

    UserInfoModel.Response entityToModel(UserInfoEntity entity);

    UserInfoEntity modelToEntity(UserInfoModel.Update model);

    List<UserInfoModel.Response> entitiesToModels(List<UserInfoEntity> entities);
}