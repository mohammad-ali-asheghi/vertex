package com.vertex.oauth.controller;


import com.vertex.core.api.oauth.ApplicationApi;
import com.vertex.core.config.CommonConstant;
import com.vertex.core.dto.ApplicationModel;
import com.vertex.core.util.PagedResponse;
import com.vertex.core.util.RestResponse;
import com.vertex.oauth.service.ApplicationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ApplicationController implements ApplicationApi {

    private final ApplicationService service;

    @PreAuthorize("hasPermission(T(com.vertex.oauth.config.OauthMenu).APPLICATION_MENU, T(com.vertex.core.enums.PermissionEnum).READ)")
    @Override
    public RestResponse<ApplicationModel> get(Long id) {
        return RestResponse.ok(service.getApplication(id));
    }

    @PreAuthorize("hasPermission(T(com.vertex.oauth.config.OauthMenu).APPLICATION_MENU, T(com.vertex.core.enums.PermissionEnum).READ)")
    @Override
    public PagedResponse<ApplicationModel> getList(
            ApplicationModel search,
            @PageableDefault(
                    size = CommonConstant.PAGE_SIZE,
                    sort = "id", direction = Sort.Direction.DESC
            ) Pageable pageable
    ) {
        return service.listInfo(search, pageable);
    }
}