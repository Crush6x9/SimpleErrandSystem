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
        userInfo.setVerified("1".equals(user.getRole())); // 跑腿员为已认证

        // 构建JWT响应
        JwtResponse jwtResponse = new JwtResponse(
                accessToken,
                refreshToken,
                jwtUtil.getExpiration(),
                userInfo
        );

        return Result.success("登录成功", jwtResponse);
    }
}
