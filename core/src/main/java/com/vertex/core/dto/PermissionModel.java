package com.vertex.core.dto;

import com.vertex.core.dto.base.BaseModel;
import com.vertex.core.enums.PermissionEnum;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@SuppressWarnings("unused")
@Getter
@Setter
public class PermissionModel extends BaseModel {

    @Builder
    public record Request(
            String role,
            List<Detail> details,
            String createdBy,
            String device,
            String userIp
    ) {
    }

    public record Response() {
    }

    public record Detail(
            Long menuId,
            List<PermissionEnum> permissions
    ) {
    }
}
