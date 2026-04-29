package com.vertex.oauth.controller;

import com.vertex.core.api.oauth.UserInfoApi;
import com.vertex.core.config.CommonConstant;
import com.vertex.core.dto.UserInfoModel;
import com.vertex.core.util.PagedResponse;
import com.vertex.core.util.ResponseMessage;
import com.vertex.core.util.RestResponse;
import com.vertex.oauth.mapper.InfoMapper;
import com.vertex.oauth.service.UserInfoService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class UserInfoController implements UserInfoApi {

    private final UserInfoService service;

    @PreAuthorize("hasPermission(T(com.vertex.oauth.config.OauthMenu).USER_MENU, T(com.vertex.core.enums.PermissionEnum).CREATE)")
    @Override
    public ResponseMessage create(UserInfoModel.Create request) {
        UserInfoModel.UserRecord record = UserInfoModel.UserRecord.builder()
                .username(request.getUsername())
                .password(request.getPassword())
                .build();
        UserInfoModel.Update model = InfoMapper.get().toModel(request);
        service.createUserInfo(model, record);
        return ResponseMessage.success();
    }

    @PreAuthorize("hasPermission(T(com.vertex.oauth.config.OauthMenu).USER_MENU, T(com.vertex.core.enums.PermissionEnum).UPDATE)")
    @Override
    public ResponseMessage update(UserInfoModel.Update request) {
        service.updateUserInfo(request);
        return ResponseMessage.success();
    }

    @PreAuthorize("hasPermission(T(com.vertex.oauth.config.OauthMenu).USER_MENU, T(com.vertex.core.enums.PermissionEnum).DELETE)")
    @Override
    public ResponseMessage delete(Long id) {
        service.deleteUserInfo(id);
        return ResponseMessage.success();
    }

    @PreAuthorize("hasPermission(T(com.vertex.oauth.config.OauthMenu).USER_MENU, T(com.vertex.core.enums.PermissionEnum).READ)")
    @Override
    public RestResponse<UserInfoModel.Response> get(Long id) {
        return RestResponse.ok(service.getUserInfo(id));
    }

    @PreAuthorize("hasPermission(T(com.vertex.oauth.config.OauthMenu).USER_MENU, T(com.vertex.core.enums.PermissionEnum).READ)")
    @Override
    public PagedResponse<UserInfoModel.Response> getList(
            UserInfoModel.Search search,
            @PageableDefault(
                    size = CommonConstant.PAGE_SIZE,
                    sort = "id", direction = Sort.Direction.DESC
            ) Pageable pageable
    ) {
        return service.listInfo(search, pageable);
    }
}