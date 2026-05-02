package com.vertex.stellaroauth.service;

import com.vertex.core.api.oauth.CodeTypeApi;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(name = "OAUTH-SERVER", contextId = "codeTypeClient")
public interface CodeTypeClient extends CodeTypeApi {

}