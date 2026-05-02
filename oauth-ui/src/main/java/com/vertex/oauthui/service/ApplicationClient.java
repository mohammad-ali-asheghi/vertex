package com.vertex.oauthui.service;

import com.vertex.core.api.oauth.ApplicationApi;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(name = "OAUTH-SERVER", contextId = "applicationClient")
public interface ApplicationClient extends ApplicationApi {

}