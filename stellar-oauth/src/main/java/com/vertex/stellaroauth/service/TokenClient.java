package com.vertex.stellaroauth.service;

import com.vertex.core.api.oauth.TokenApi;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(name = "OAUTH-SERVER", contextId = "tokenClient")
public interface TokenClient extends TokenApi {

}
