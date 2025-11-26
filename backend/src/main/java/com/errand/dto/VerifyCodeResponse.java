package com.errand.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(description = "验证码响应")
public class VerifyCodeResponse {
    @ApiModelProperty(value = "验证码", example = "123456")
    private String verifyCode;

    @ApiModelProperty(value = "过期时间", example = "300")
    private Integer expireSeconds;

    public VerifyCodeResponse(String verifyCode, Integer expireSeconds) {
        this.verifyCode = verifyCode;
        this.expireSeconds = expireSeconds;
    }
}
