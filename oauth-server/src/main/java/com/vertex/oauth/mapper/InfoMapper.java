package com.vertex.oauth.mapper;

import com.vertex.core.dto.UserInfoModel;
import org.mapstruct.Mapper;
import org.mapstruct.NullValueMappingStrategy;
import org.mapstruct.factory.Mappers;

@Mapper(
        nullValueMappingStrategy = NullValueMappingStrategy.RETURN_NULL,
        componentModel = "spring"
)
public interface InfoMapper {

    static InfoMapper get() {
        return Mappers.getMapper(InfoMapper.class);
    }

    UserInfoModel.Update toModel(UserInfoModel.Create request);
}