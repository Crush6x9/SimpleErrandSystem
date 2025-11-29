package com.errand.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotBlank;

@Data
@ApiModel(description = "认证请求")
public class CertificationRequest {
    @ApiModelProperty(value = "学号", required = true)
    @NotBlank(message = "学号不能为空")
    private String studentId;

    @ApiModelProperty(value = "证件图片")
    private MultipartFile idCardImage;

    @ApiModelProperty(value = "头像图片")
    private MultipartFile avatar;

    public CertificationRequest() {}

    public CertificationRequest(String studentId, MultipartFile idCardImage, MultipartFile avatar) {
        this.studentId = studentId;
        this.idCardImage = idCardImage;
        this.avatar = avatar;
    }
}
