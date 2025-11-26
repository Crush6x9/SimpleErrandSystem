package com.errand.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@ApiModel(description = "用户信息")
public class UserInfo {
    @ApiModelProperty(value = "用户ID", example = "101")
    private Long id;

    @ApiModelProperty(value = "手机号")
    private String phone;

    @ApiModelProperty(value = "用户名")
    private String username;

    @ApiModelProperty(value = "用户角色", example = "0")
    private String role;

    @ApiModelProperty(value = "学号")
    private String studentId;

    @ApiModelProperty(value = "是否认证", example = "false")
    private Boolean verified;

    @ApiModelProperty(value = "头像URL")
    private String avatarUrl;

    @ApiModelProperty(value = "证件URL")
    private String certificateUrl;

    @ApiModelProperty(value = "创建时间")
    private LocalDateTime createdTime;

    @ApiModelProperty(value = "更新时间")
    private LocalDateTime updatedTime;
}
