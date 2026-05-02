package com.vertex.oauthui.service;

import com.vertex.core.api.oauth.PermissionApi;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(name = "OAUTH-SERVER", contextId = "permissionClient")
public interface PermissionClient extends PermissionApi {

}