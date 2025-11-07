package com.errand.service.impl;

import com.errand.dto.*;
import com.errand.entity.User;
import com.errand.mapper.UserMapper;
import com.errand.service.UserService;
import com.errand.util.JwtUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserMapper userMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    private boolean isValidPhone(String phone) {
        String pattern = "^1[3-9]\\d{9}$";
        return phone.matches(pattern);
    }

    private boolean isValidPassword(String password) {
        String pattern = "^(?=.*[A-Za-z])(?=.*\\d).{6,20}$";
        return password.matches(pattern);
    }

    @Override
    public Result register(RegisterRequest request) {
        User existingUser = userMapper.findByPhone(request.getPhone());
        if (existingUser != null) {
            return Result.error("手机号已注册");
        }

        User user = new User();
        user.setPhone(request.getPhone());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole("0"); // 默认角色为委托人
        user.setCreateTime(LocalDateTime.now());
        user.setUpdateTime(LocalDateTime.now());

        int result = userMapper.insert(user);
        if (result > 0) {
            return Result.success("注册成功");
        } else {
            return Result.error("注册失败");
        }
    }

    @Override
    public Result login(LoginRequest request) {
        // 根据手机号查询用户
        User user = userMapper.findByPhone(request.getPhone());
        if (user == null) {
            return Result.error("用户不存在");
        }

        // 验证密码
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            return Result.error("密码错误");
        }

        // 生成访问令牌和刷新令牌
        String accessToken = jwtUtil.generateToken(user.getUserId(), user.getPhone(), user.getRole());
        String refreshToken = jwtUtil.generateRefreshToken(user.getUserId(), user.getPhone(), user.getRole());

        // 构建用户信息
        UserInfo userInfo = new UserInfo();
        BeanUtils.copyProperties(user, userInfo);
        userInfo.setId(user.getUserId());
        userInfo.setVerified("1".equals(user.getRole()));

        // 构建JWT响应
        JwtResponse jwtResponse = new JwtResponse(
                accessToken,
                refreshToken,
                jwtUtil.getExpiration(),
                userInfo
        );

        return Result.success("登录成功", jwtResponse);
    }

    @Override
    public Result checkPhoneExists(ForgetPasswordRequest request) {
        try {
            // 验证手机号格式
            if (!isValidPhone(request.getPhone())) {
                return Result.error("手机号格式不正确");
            }

            // 检查手机号是否存在
            User user = userMapper.findByPhone(request.getPhone());
            if (user == null) {
                return Result.error("该手机号未注册");
            }

            // 返回成功信息（不返回用户具体信息以保护隐私）
            return Result.success("手机号验证成功，可以重置密码");

        } catch (Exception e) {
            return Result.error("系统错误，请稍后重试");
        }
    }

    @Override
    public Result resetPassword(ResetPasswordRequest request) {
        try {
            // 验证手机号格式
            if (!isValidPhone(request.getPhone())) {
                return Result.error("手机号格式不正确");
            }

            // 验证密码格式
            if (!isValidPassword(request.getNewPassword())) {
                return Result.error("密码需包含字母和数字，长度6-20位");
            }

            // 检查用户是否存在
            User user = userMapper.findByPhone(request.getPhone());
            if (user == null) {
                return Result.error("用户不存在");
            }

            // 加密新密码
            String encodedPassword = passwordEncoder.encode(request.getNewPassword());

            // 更新密码
            int result = userMapper.updatePassword(
                    user.getUserId(),
                    encodedPassword,
                    LocalDateTime.now()
            );

            if (result > 0) {
                return Result.success("密码重置成功");
            } else {
                return Result.error("密码重置失败");
            }

        } catch (Exception e) {
            return Result.error("系统错误，请稍后重试");
        }
    }
}
