package com.errand.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class UserInfo {
    @ApiModelProperty(value = "用户ID", example = "1")
    private Long id;

    @ApiModelProperty(value = "手机号", example = "13812345678")
    private String phone;

    @ApiModelProperty(value = "用户角色", example = "CLIENT")
    private String role;

    @ApiModelProperty(value = "学号", example = "2021001001")
    private String studentId;

    @ApiModelProperty(value = "是否认证", example = "false")
    private Boolean verified;

    @ApiModelProperty(value = "创建时间")
    private LocalDateTime createdTime;

    @ApiModelProperty(value = "更新时间")
    private LocalDateTime updatedTime;
}
