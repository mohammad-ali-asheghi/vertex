package com.vertex.oauthui.service;

import com.vertex.core.api.oauth.CodeTypeApi;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(name = "OAUTH-SERVICE", contextId = "codeTypeClient")
public interface CodeTypeClient extends CodeTypeApi {

}