package com.errand.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Data
@ApiModel("用户")
public class User {
    @ApiModelProperty(value = "用户ID", example = "1")
    private Long id;

    @NotBlank(message = "手机号不能为空")
    @ApiModelProperty(value = "手机号", required = true)
    private String phone;

    @NotBlank(message = "密码不能为空")
    @ApiModelProperty(value = "密码", required = true)
    private String password;

    @ApiModelProperty(value = "用户角色", example = "CLIENT")
    private String role;

    @ApiModelProperty(value = "学号")
    private String studentId;

    @ApiModelProperty(value = "证件图片路径")
    private String idCardImage;

    @ApiModelProperty(value = "认证状态")
    private Boolean verified;

    @ApiModelProperty(value = "创建时间")
    private LocalDateTime createdTime;

    @ApiModelProperty(value = "更新时间")
    private LocalDateTime updatedTime;
}
