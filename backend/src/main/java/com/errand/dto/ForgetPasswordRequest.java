package com.errand.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Data
@ApiModel(description = "忘记密码请求")
public class ForgetPasswordRequest {
    @ApiModelProperty(value = "手机号", required = true)
    @NotBlank(message = "手机号不能为空")
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    private String phone;

    public ForgetPasswordRequest() {}

    public ForgetPasswordRequest(String phone) {
        this.phone = phone;
    }
}
