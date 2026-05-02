package com.vertex.oauthui.service;

import com.vertex.core.api.oauth.MenuApi;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(name = "OAUTH-SERVER", contextId = "menuClient")
public interface MenuClient extends MenuApi {

}
