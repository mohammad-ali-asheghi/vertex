package com.vertex.oauthui.service;

import com.vertex.core.api.oauth.CodeTypeItemApi;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(name = "OAUTH-SERVICE", contextId = "codeTypeItemClient")
public interface CodeTypeItemClient extends CodeTypeItemApi {

}