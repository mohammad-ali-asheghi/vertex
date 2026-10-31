package com.vertex.oauth.controller;

import com.vertex.core.api.oauth.UserApi;
import com.vertex.core.config.CommonConstant;
import com.vertex.core.dto.UserDto;
import com.vertex.core.util.PagedResponse;
import com.vertex.core.util.ResponseMessage;
import com.vertex.core.util.RestResponse;
import com.vertex.oauth.service.AdminUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class UserController implements UserApi {

    private final AdminUserService service;

    @PreAuthorize("hasPermission(T(com.vertex.oauth.config.OauthMenu).USER_MENU, T(com.vertex.core.enums.PermissionEnum).READ)")
    @Override
    public PagedResponse<UserDto.Response> getList(
            UserDto.Search search,
            @PageableDefault(
                    size = CommonConstant.PAGE_SIZE,
                    sort = "id", direction = Sort.Direction.DESC
            ) Pageable pageable
    ) {
        return service.getList(search, pageable);
    }

    @PreAuthorize("hasPermission(T(com.vertex.oauth.config.OauthMenu).USER_MENU, T(com.vertex.core.enums.PermissionEnum).READ)")
    @Override
    public RestResponse<List<UserDto.RoleOption>> getRoleOptions() {
        return RestResponse.ok(service.getRoleOptions());
    }

    @PreAuthorize("hasPermission(T(com.vertex.oauth.config.OauthMenu).USER_MENU, T(com.vertex.core.enums.PermissionEnum).READ)")
    @Override
    public RestResponse<UserDto.Response> findById(Long id) {
        return RestResponse.ok(service.findById(id));
    }

    @PreAuthorize("hasPermission(T(com.vertex.oauth.config.OauthMenu).USER_MENU, T(com.vertex.core.enums.PermissionEnum).CREATE)")
    @Override
    public RestResponse<UserDto.Response> create(UserDto.AdminRequest request) {
        return RestResponse.ok(service.create(request));
    }

    @PreAuthorize("hasPermission(T(com.vertex.oauth.config.OauthMenu).USER_MENU, T(com.vertex.core.enums.PermissionEnum).UPDATE)")
    @Override
    public RestResponse<UserDto.Response> update(Long id, UserDto.AdminRequest request) {
        return RestResponse.ok(service.update(id, request));
    }

    @PreAuthorize("hasPermission(T(com.vertex.oauth.config.OauthMenu).USER_MENU, T(com.vertex.core.enums.PermissionEnum).DELETE)")
    @Override
    public ResponseMessage delete(Long id) {
        service.delete(id);
        return ResponseMessage.success();
    }
}
