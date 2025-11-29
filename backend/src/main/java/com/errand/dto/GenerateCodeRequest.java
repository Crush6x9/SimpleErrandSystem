package com.errand.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import javax.validation.constraints.NotBlank;

@Data
@ApiModel(description = "生成验证码请求")
public class GenerateCodeRequest {
    @ApiModelProperty(value = "手机号", required = true, example = "13812345678")
    @NotBlank(message = "手机号不能为空")
    private String phone;

    public GenerateCodeRequest() {}

    public GenerateCodeRequest(String phone) {
        this.phone = phone;
    }
}
