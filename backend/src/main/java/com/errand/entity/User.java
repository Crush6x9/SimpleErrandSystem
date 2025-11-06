package com.errand.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Data
@ApiModel("用户")
public class User {
    @ApiModelProperty(value = "用户ID", example = "101")
    private Long userId;

    @ApiModelProperty(value = "手机号", required = true)
    private String phone;

    @ApiModelProperty(value = "密码", required = true)
    private String password;

    @ApiModelProperty(value = "用户名")
    private String username;

    @ApiModelProperty(value = "头像路径")
    private String avatar;

    @ApiModelProperty(value = "学号")
    private String studentId;

    @ApiModelProperty(value = "证件图片路径")
    private String certificate;

    @ApiModelProperty(value = "用户角色", example = "0")
    private String role;

    @ApiModelProperty(value = "创建时间")
    private LocalDateTime createTime;

    @ApiModelProperty(value = "更新时间")
    private LocalDateTime updateTime;
}
