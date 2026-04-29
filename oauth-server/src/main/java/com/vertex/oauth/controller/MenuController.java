package com.vertex.oauth.controller;

import com.vertex.core.api.oauth.MenuApi;
import com.vertex.core.config.CommonConstant;
import com.vertex.core.dto.MenuModel;
import com.vertex.core.dto.view.ViewMenuDto;
import com.vertex.core.util.PagedResponse;
import com.vertex.core.util.ResponseMessage;
import com.vertex.core.util.RestResponse;
import com.vertex.oauth.service.MenuService;
import com.vertex.oauth.service.ViewMenuService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class MenuController implements MenuApi {

    private final MenuService service;
    private final ViewMenuService viewService;

    @PreAuthorize("hasPermission(T(com.vertex.oauth.config.OauthMenu).OAUTH_MENU, T(com.vertex.core.enums.PermissionEnum).UPDATE)")
    @Override
    public ResponseMessage update(MenuModel request) {
        service.updateMenu(request);
        return ResponseMessage.success();
    }

    @PreAuthorize("hasPermission(T(com.vertex.oauth.config.OauthMenu).OAUTH_MENU, T(com.vertex.core.enums.PermissionEnum).READ)")
    @Override
    public RestResponse<MenuModel> get(Long id) {
        return RestResponse.ok(service.getMenu(id));
    }

    @PreAuthorize("hasPermission(T(com.vertex.oauth.config.OauthMenu).OAUTH_MENU, T(com.vertex.core.enums.PermissionEnum).READ)")
    @Override
    public PagedResponse<ViewMenuDto> getMenuSearchKeyList(
            MenuModel search,
            @PageableDefault(
                    size = CommonConstant.PAGE_SIZE,
                    sort = "id", direction = Sort.Direction.DESC
            ) Pageable pageable
    ) {
        return this.getList(search, pageable, true);
    }

    @PreAuthorize("hasPermission(T(com.vertex.oauth.config.OauthMenu).OAUTH_MENU, T(com.vertex.core.enums.PermissionEnum).READ)")
    @Override
    public PagedResponse<ViewMenuDto> getSidebarList(
            MenuModel search,
            @PageableDefault(
                    size = CommonConstant.PAGE_SIZE,
                    sort = "id", direction = Sort.Direction.DESC
            ) Pageable pageable
    ) {
        return this.getList(search, pageable, false);
    }

    private PagedResponse<ViewMenuDto> getList(MenuModel search, Pageable pageable, Boolean isMenuSearchKey) {
        return viewService.listInfo(search, pageable, isMenuSearchKey);
    }
}