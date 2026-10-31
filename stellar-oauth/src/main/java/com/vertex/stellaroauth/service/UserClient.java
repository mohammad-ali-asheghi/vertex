package com.vertex.stellaroauth.service;

import com.vertex.core.api.oauth.UserApi;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(name = "OAUTH-SERVER", contextId = "userClient")
public interface UserClient extends UserApi {

}
