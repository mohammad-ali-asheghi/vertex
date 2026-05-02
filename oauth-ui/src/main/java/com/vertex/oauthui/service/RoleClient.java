package com.vertex.oauthui.service;

import com.vertex.core.api.oauth.RoleApi;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(name = "OAUTH-SERVER", contextId = "roleClient")
public interface RoleClient extends RoleApi {

}