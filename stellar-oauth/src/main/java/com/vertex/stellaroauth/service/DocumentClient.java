package com.vertex.stellaroauth.service;

import com.vertex.core.api.oauth.DocumentApi;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(name = "OAUTH-SERVER", contextId = "documentClient")
public interface DocumentClient extends DocumentApi {

}
