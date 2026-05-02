package com.vertex.stellaroauth.service;

import com.vertex.core.api.oauth.UserInfoApi;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(name = "OAUTH-SERVER", contextId = "userInfoClient")
public interface UserInfoClient extends UserInfoApi {

}
