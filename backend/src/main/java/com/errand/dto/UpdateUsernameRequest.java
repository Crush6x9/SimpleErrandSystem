package com.errand.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
@ApiModel(description = "修改用户名请求")
public class UpdateUsernameRequest {
    @ApiModelProperty(value = "新用户名", required = true)
    @NotBlank(message = "用户名不能为空")
    @Size(min = 2, max = 10, message = "用户名长度2-10位")
    private String username;

    public UpdateUsernameRequest() {}

    public UpdateUsernameRequest(String username) {
        this.username = username;
    }
}
