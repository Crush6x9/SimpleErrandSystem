package com.errand.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import javax.validation.constraints.NotBlank;

@Data
@ApiModel(description = "验证验证码请求")
public class VerifyCodeRequest {
    @ApiModelProperty(value = "手机号", required = true)
    @NotBlank(message = "手机号不能为空")
    private String phone;

    @ApiModelProperty(value = "验证码", required = true, example = "123456")
    @NotBlank(message = "验证码不能为空")
    private String code;

    public VerifyCodeRequest() {}

    public VerifyCodeRequest(String phone, String code) {
        this.phone = phone;
        this.code = code;
    }
}
