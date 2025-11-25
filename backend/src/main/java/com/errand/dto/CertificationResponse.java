package com.errand.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(description = "认证响应")
public class CertificationResponse {
    @ApiModelProperty(value = "认证状态")
    private Boolean success;

    @ApiModelProperty(value = "消息")
    private String message;

    @ApiModelProperty(value = "用户信息")
    private UserInfo userInfo;

    public CertificationResponse() {}

    public CertificationResponse(Boolean success, String message, UserInfo userInfo) {
        this.success = success;
        this.message = message;
        this.userInfo = userInfo;
    }
}
