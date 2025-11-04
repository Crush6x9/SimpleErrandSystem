package com.errand.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(description = "登录响应")
public class LoginResponse {
    @ApiModelProperty(value = "JWT令牌")
    private String token;

    @ApiModelProperty(value = "用户信息")
    private UserInfo userInfo;

    @Data
    public static class UserInfo {
        private Long id;
        private String phone;
        private Integer role; // 0 CLIENT, 1 COURIER
        private String studentId; // 仅跑腿员有
    }
}
