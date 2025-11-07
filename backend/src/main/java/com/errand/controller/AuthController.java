package com.errand.controller;

import com.errand.dto.*;
import com.errand.service.UserService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import javax.validation.Valid;

@RestController
@RequestMapping("/api/auth")
@Validated
public class AuthController {
    @Autowired
    private UserService userService;

    @PostMapping("/register")
    @ApiOperation("用户注册")
    public Result register(@RequestBody @Valid RegisterRequest request) {
        return userService.register(request);
    }

    @PostMapping("/login")
    @ApiOperation("用户登录")
    public Result login(@RequestBody @Valid LoginRequest request) {
        return userService.login(request);
    }

    @PostMapping("/forget-password/check")
    @ApiOperation("验证手机号是否存在")
    public Result checkPhoneExists(@RequestBody @Valid ForgetPasswordRequest request) {
        return userService.checkPhoneExists(request);
    }

    @PostMapping("/forget-password/reset")
    @ApiOperation("重置密码")
    public Result resetPassword(@RequestBody @Valid ResetPasswordRequest request) {
        return userService.resetPassword(request);
    }
}
