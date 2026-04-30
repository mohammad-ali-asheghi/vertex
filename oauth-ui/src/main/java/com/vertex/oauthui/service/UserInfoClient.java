package com.vertex.oauthui.service;

import com.vertex.core.api.oauth.UserInfoApi;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(name = "OAUTH-SERVICE", contextId = "userInfoClient")
public interface UserInfoClient extends UserInfoApi {

}
