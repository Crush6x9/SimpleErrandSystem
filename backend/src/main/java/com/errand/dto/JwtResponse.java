package com.errand.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(description = "JWT令牌响应")
public class JwtResponse {
    @ApiModelProperty(value = "访问令牌", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
    private String accessToken;

    @ApiModelProperty(value = "刷新令牌", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
    private String refreshToken;

    @ApiModelProperty(value = "令牌类型", example = "Bearer")
    private String tokenType = "Bearer";

    @ApiModelProperty(value = "过期时间（毫秒）", example = "86400000")
    private Long expiresIn;

    @ApiModelProperty(value = "用户信息")
    private UserInfo userInfo;

    public JwtResponse(String accessToken, String refreshToken, Long expiresIn, UserInfo userInfo) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.expiresIn = expiresIn;
        this.userInfo = userInfo;
    }
}
