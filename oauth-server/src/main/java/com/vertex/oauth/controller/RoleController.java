package com.vertex.oauth.controller;

import com.vertex.core.api.oauth.RoleApi;
import com.vertex.core.config.CommonConstant;
import com.vertex.core.dto.RoleModel;
import com.vertex.core.util.PagedResponse;
import com.vertex.core.util.ResponseMessage;
import com.vertex.core.util.RestResponse;
import com.vertex.oauth.service.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequiredArgsConstructor
public class RoleController implements RoleApi {

    private final RoleService service;

    @PreAuthorize("hasPermission(T(com.vertex.oauth.config.OauthMenu).ROLE_MENU, T(com.vertex.core.enums.PermissionEnum).CREATE)")
    @Override
    public ResponseMessage create(RoleModel request) {
        service.createRole(request);
        return ResponseMessage.success();
    }

    @PreAuthorize("hasPermission(T(com.vertex.oauth.config.OauthMenu).ROLE_MENU, T(com.vertex.core.enums.PermissionEnum).UPDATE)")
    @Override
    public ResponseMessage update(RoleModel request) {
        service.updateRole(request);
        return ResponseMessage.success();
    }

    @PreAuthorize("hasPermission(T(com.vertex.oauth.config.OauthMenu).ROLE_MENU, T(com.vertex.core.enums.PermissionEnum).DELETE)")
    @Override
    public ResponseMessage delete(Long id) {
        service.deleteRole(id);
        return ResponseMessage.success();
    }

    @PreAuthorize("hasPermission(T(com.vertex.oauth.config.OauthMenu).ROLE_MENU, T(com.vertex.core.enums.PermissionEnum).READ)")
    @Override
    public RestResponse<RoleModel> get(Long id) {
        return RestResponse.ok(service.getRole(id));
    }

    @PreAuthorize("hasPermission(T(com.vertex.oauth.config.OauthMenu).ROLE_MENU, T(com.vertex.core.enums.PermissionEnum).READ)")
    @Override
    public PagedResponse<RoleModel> getList(
            RoleModel search,
            @PageableDefault(
                    size = CommonConstant.PAGE_SIZE,
                    sort = "id", direction = Sort.Direction.DESC
            ) Pageable pageable
    ) {
        return service.listInfo(search, pageable);
    }
}