package com.errand.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import javax.validation.constraints.NotBlank;

@Data
@ApiModel(description = "登录请求")
public class LoginRequest {
    @ApiModelProperty(value = "手机号", required = true, example = "13812345678")
    @NotBlank(message = "手机号不能为空")
    private String phone;

    @ApiModelProperty(value = "密码", required = true, example = "123456")
    @NotBlank(message = "密码不能为空")
    private String password;

    public LoginRequest() {}

    public LoginRequest(String phone, String password) {
        this.phone = phone;
        this.password = password;
    }
}
