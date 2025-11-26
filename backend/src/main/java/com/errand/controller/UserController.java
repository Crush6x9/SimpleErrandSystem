package com.errand.controller;

import com.errand.dto.*;
import com.errand.service.UserService;
import com.errand.util.JwtUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import javax.validation.Valid;

@Api(tags = {"用户操作"})
@RestController
@RequestMapping("/api/user")
public class UserController {
    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtil jwtUtil;

    @PutMapping("/username")
    @ApiOperation("修改用户名")
    public Result updateUsername(
            @RequestHeader("Authorization") String token,
            @RequestBody @Valid UpdateUsernameRequest request) {
        Long userId = jwtUtil.getUserIdFromToken(token);
        if (userId == null) {
            return Result.error("令牌无效或已过期");
        }
        return userService.updateUsername(userId, request);
    }

    @PostMapping(value = "/certification", consumes = "multipart/form-data")
    @ApiOperation("用户认证成为跑腿员")
    public Result certification(
            @RequestHeader("Authorization") String token,
            @RequestParam("studentId") String studentId,
            @RequestParam(value = "avatar", required = false) MultipartFile avatar,
            @RequestParam("idCardImage") MultipartFile idCardImage) {

        Long userId = jwtUtil.getUserIdFromToken(token);
        if (userId == null) {
            return Result.error("令牌无效或已过期");
        }

        CertificationRequest request = new CertificationRequest();
        request.setStudentId(studentId);
        request.setAvatar(avatar);
        request.setIdCardImage(idCardImage);

        return userService.certification(userId, request);
    }

    @GetMapping("/info")
    @ApiOperation("获取用户信息")
    public Result getUserInfo(@RequestHeader("Authorization") String token) {
        Long userId = jwtUtil.getUserIdFromToken(token);
        if (userId == null) {
            return Result.error("令牌无效或已过期");
        }

        UserInfo userInfo = userService.getUserInfo(userId);
        if (userInfo != null) {
            return Result.success("获取用户信息成功", userInfo);
        } else {
            return Result.error("用户不存在");
        }
    }

    @PutMapping(value = "/info", consumes = "multipart/form-data")
    @ApiOperation("更新用户信息")
    public Result updateUserInfo(
            @RequestHeader("Authorization") String token,
            @ModelAttribute UpdateUserInfoRequest request) {
        Long userId = jwtUtil.getUserIdFromToken(token);
        if (userId == null) {
            return Result.error("令牌无效或已过期");
        }
        return userService.updateUserInfo(userId, request);
    }

    @PostMapping(value = "/avatar", consumes = "multipart/form-data")
    @ApiOperation("上传头像")
    public Result uploadAvatar(
            @RequestHeader("Authorization") String token,
            @RequestParam("avatar") MultipartFile avatar) {
        Long userId = jwtUtil.getUserIdFromToken(token);
        if (userId == null) {
            return Result.error("令牌无效或已过期");
        }
        return userService.uploadAvatar(userId, avatar);
    }
}
