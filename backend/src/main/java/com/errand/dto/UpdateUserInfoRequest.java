package com.errand.dto;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Data
@ApiModel(description = "更新用户信息请求")
public class UpdateUserInfoRequest {
    @Size(min = 2, max = 10, message = "用户名长度2-10位")
    private String username;

    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    private String phone;

    private String studentId;

    private MultipartFile idCardImage;
}
