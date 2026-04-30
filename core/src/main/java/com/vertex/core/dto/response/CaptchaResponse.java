package com.vertex.core.dto.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CaptchaResponse {

    private String captchaId;
    private String image;

    public CaptchaResponse(String captchaId, String image) {
        this.captchaId = captchaId;
        this.image = image;
    }
}
