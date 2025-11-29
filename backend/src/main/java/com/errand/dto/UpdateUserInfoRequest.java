package com.errand.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Data
@ApiModel(description = "更新用户信息请求")
public class UpdateUserInfoRequest {
    @ApiModelProperty(value = "用户名")
    @Size(min = 2, max = 10, message = "用户名长度2-10位")
    private String username;

    @ApiModelProperty(value = "手机号")
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    private String phone;

    @ApiModelProperty(value = "学号")
    private String studentId;

    @ApiModelProperty(value = "证件URL")
    private MultipartFile idCardImage;

    public UpdateUserInfoRequest() {}

    public UpdateUserInfoRequest(String username, String phone, String studentId, MultipartFile idCardImage) {
        this.username = username;
        this.phone = phone;
        this.studentId = studentId;
        this.idCardImage = idCardImage;
    }
}
