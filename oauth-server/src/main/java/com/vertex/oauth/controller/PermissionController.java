package com.vertex.oauth.controller;

import com.vertex.core.api.oauth.PermissionApi;
import com.vertex.core.dto.PermissionModel;
import com.vertex.core.dto.interfaces.UnrelatedMenuProjection;
import com.vertex.core.dto.view.ViewPermissionDto;
import com.vertex.core.util.ResponseMessage;
import com.vertex.core.util.RestResponse;
import com.vertex.oauth.service.PermissionService;
import com.vertex.oauth.service.ViewPermissionService;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RestController;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.List;

@RestController
public class PermissionController implements PermissionApi {

    private final PermissionService service;
    private final ViewPermissionService viewService;
    private final SecretKey secretKey;

    public PermissionController(
            @Value("${security.password.secret-key}") String secretKey,
            PermissionService service,
            ViewPermissionService viewService
    ) {
        byte[] keyBytes = secretKey.getBytes(StandardCharsets.UTF_8);
        this.secretKey = Keys.hmacShaKeyFor(keyBytes);
        this.service = service;
        this.viewService = viewService;
    }

    @PreAuthorize("hasPermission(T(com.vertex.oauth.config.OauthMenu).PERMISSION_MENU, T(com.vertex.core.enums.PermissionEnum).CREATE)")
    @Override
    public ResponseMessage sync(PermissionModel.Request request, String token) {
        service.syncRolePermissions(request, secretKey, token);
        return ResponseMessage.success();
    }

    @PreAuthorize("hasPermission(T(com.vertex.oauth.config.OauthMenu).PERMISSION_MENU, T(com.vertex.core.enums.PermissionEnum).READ)")
    @Override
    public RestResponse<List<ViewPermissionDto>> getRelatedList(String role) {
        return RestResponse.ok(viewService.getRelatedList(role));
    }

    @PreAuthorize("hasPermission(T(com.vertex.oauth.config.OauthMenu).PERMISSION_MENU, T(com.vertex.core.enums.PermissionEnum).READ)")
    @Override
    public RestResponse<List<UnrelatedMenuProjection>> getUnrelatedList(String role) {
        return RestResponse.ok(service.getUnrelatedMenus(role));
    }
}