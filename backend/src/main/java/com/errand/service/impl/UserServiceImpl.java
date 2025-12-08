package com.errand.service.impl;

import com.errand.dto.*;
import com.errand.entity.User;
import com.errand.mapper.UserMapper;
import com.errand.service.UserService;
import com.errand.util.FileUploadUtil;
import com.errand.util.JwtUtil;
import com.errand.util.VerifyCodeUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserMapper userMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private VerifyCodeUtil verifyCodeUtil;

    @Value("${app.upload.storage-path}")
    private String uploadDir;

    @Value("${app.upload.base-url}")
    private String baseUrl;

    @Value("${app.upload.max-file-size}")
    private long maxFileSize;

    @Override
    public Result register(RegisterRequest request) {
        try {
            Result verifyResult = verifyCodeUtil.verifyCode(request.getPhone(), request.getVerifyCode());
            if (!verifyResult.getCode().equals(200)) {
                return verifyResult;
            }

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
                // 获取生成的用户ID
                Long userId = user.getUserId();

                // 设置默认用户名
                String defaultUsername = "用户" + userId;
                user.setUsername(defaultUsername);

                // 设置默认头像路径
                String defaultAvatarPath = "avatar/A-Default.png";
                user.setAvatar(defaultAvatarPath);

                // 更新用户信息（包含默认用户名和头像）
                user.setUpdateTime(LocalDateTime.now());
                int updateResult = userMapper.update(user);

                if (updateResult == 0) {
                    // 如果更新失败，记录日志但依然返回注册成功
                    System.err.println("注册成功但默认信息设置失败！用户ID: " + userId);
                }

                // 注册成功后移除验证码
                verifyCodeUtil.removeCode(request.getPhone());

                return Result.success("注册成功");
            } else {
                return Result.error("注册失败");
            }
        } catch (Exception e) {
            return Result.error("系统错误，请稍后重试");
        }
    }

    @Override
    public Result login(LoginRequest request) {
        try {
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
            String accessToken = jwtUtil.generateToken(user.getUserId(), user.getRole());
            String refreshToken = jwtUtil.generateRefreshToken(user.getUserId(), user.getRole());

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
        } catch (Exception e) {
            return Result.error("系统错误，请稍后重试");
        }
    }

    @Override
    public Result checkPhoneExists(ForgetPasswordRequest request) {
        try {
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
            // 检查用户是否存在
            User user = userMapper.findByPhone(request.getPhone());
            if (user == null) {
                return Result.error("用户不存在");
            }

            // 验证验证码
            Result verifyResult = verifyCodeUtil.verifyCode(request.getPhone(), request.getVerifyCode());
            if (!verifyResult.getCode().equals(200)) {
                return verifyResult;
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
                // 更新密码成功后移除验证码
                verifyCodeUtil.removeCode(request.getPhone());

                return Result.success("密码重置成功");
            } else {
                return Result.error("密码重置失败");
            }

        } catch (Exception e) {
            return Result.error("系统错误，请稍后重试");
        }
    }

    @Override
    @Transactional
    public Result updateUsername(Long userId, UpdateUsernameRequest request) {
        try {
            // 验证用户名长度
            if (request.getUsername().length() < 2 || request.getUsername().length() > 10) {
                return Result.error("用户名长度2-10位");
            }

            // 检查用户是否存在
            User user = userMapper.findById(userId);
            if (user == null) {
                return Result.error("用户不存在");
            }

            // 更新用户名
            user.setUsername(request.getUsername());
            user.setUpdateTime(LocalDateTime.now());

            int result = userMapper.update(user);
            if (result > 0) {
                // 返回更新后的用户信息
                UserInfo userInfo = convertToUserInfo(user);
                return Result.success("用户名修改成功", userInfo);
            } else {
                return Result.error("用户名修改失败");
            }

        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("系统错误，请稍后重试");
        }
    }

    @Override
    @Transactional
    public Result certification(Long userId, CertificationRequest request) {
        try {
            // 检查用户是否存在
            User user = userMapper.findById(userId);
            if (user == null) {
                return Result.error("用户不存在");
            }

            // 验证必填字段
            if (request.getStudentId() == null || request.getStudentId().trim().isEmpty()) {
                return Result.error("学号不能为空");
            }

            if (request.getIdCardImage() == null || request.getIdCardImage().isEmpty()) {
                return Result.error("证件图片不能为空");
            }

            // 验证证件图片
            String idCardValidation = FileUploadUtil.validateFile(request.getIdCardImage(), "证件图片");
            if (idCardValidation != null) {
                return Result.error(idCardValidation);
            }

            // 处理头像上传（如果有）
            String avatarPath = user.getAvatar(); // 保持原有头像
            if (request.getAvatar() != null && !request.getAvatar().isEmpty()) {
                // 验证头像图片
                String avatarValidation = FileUploadUtil.validateFile(request.getAvatar(), "头像");
                if (avatarValidation != null) {
                    return Result.error(avatarValidation);
                }
                avatarPath = saveFile(request.getAvatar(), "avatar", userId);
                if (avatarPath == null) {
                    return Result.error("头像上传失败");
                }
            }

            // 处理证件图片上传
            String idCardImagePath = saveFile(request.getIdCardImage(), "certificate", userId);
            if (idCardImagePath == null) {
                return Result.error("证件图片上传失败");
            }

            // 更新用户信息
            user.setStudentId(request.getStudentId());
            user.setAvatar(avatarPath);
            user.setCertificate(idCardImagePath);
            user.setRole("1"); // 更新为跑腿员角色
            user.setUpdateTime(LocalDateTime.now());

            int result = userMapper.update(user);
            if (result > 0) {
                UserInfo userInfo = convertToUserInfo(user);
                return Result.success("认证成功，您已成为跑腿员", userInfo);
            } else {
                return Result.error("认证失败");
            }

        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("系统错误，请稍后重试");
        }
    }

    @Override
    public Result getUserInfo(Long userId) {
        User user = userMapper.findById(userId);
        if (user != null) {
            UserInfo userInfo = convertToUserInfo(user);
            return Result.success("获取用户信息成功", userInfo);
        } else {
            return Result.error("用户不存在");
        }
    }

    @Override
    @Transactional
    public Result updateUserInfo(Long userId, UpdateUserInfoRequest request) {
        try {
            // 检查用户是否存在
            User user = userMapper.findById(userId);
            if (user == null) {
                return Result.error("用户不存在");
            }

            // 验证用户名长度
            if (request.getUsername() != null && !request.getUsername().trim().isEmpty()) {
                if (request.getUsername().length() < 2 || request.getUsername().length() > 10) {
                    return Result.error("用户名长度2-10位");
                }
                user.setUsername(request.getUsername().trim());
            }

            // 验证手机号唯一性
            if (request.getPhone() != null && !request.getPhone().trim().isEmpty()) {
                User existingUser = userMapper.findByPhone(request.getPhone());
                if (existingUser != null && !existingUser.getUserId().equals(userId)) {
                    return Result.error("手机号已被其他用户使用");
                }
                user.setPhone(request.getPhone().trim());
            }

            // 更新学号
            boolean hasStuId = false;
            if (request.getStudentId() != null && !request.getStudentId().trim().isEmpty()) {
                user.setStudentId(request.getStudentId().trim());
                hasStuId = true;
            }

            // 处理证件图片上传
            boolean hasIdCard = false;
            if (request.getIdCardImage() != null && !request.getIdCardImage().isEmpty()) {
                String idCardValidation = FileUploadUtil.validateFile(request.getIdCardImage(), "证件图片");
                if (idCardValidation != null) {
                    return Result.error(idCardValidation);
                }

                String idCardImagePath = saveFile(request.getIdCardImage(), "certificate", userId);
                if (idCardImagePath == null) {
                    return Result.error("证件图片上传失败");
                }
                user.setCertificate(idCardImagePath);
                hasIdCard = true;
            }

            // 如果补全了学号和证件，更新用户身份为跑腿员
            if (hasStuId && hasIdCard) {
                user.setRole("1");
            }

            user.setUpdateTime(LocalDateTime.now());

            int result = userMapper.update(user);
            if (result > 0) {
                UserInfo userInfo = convertToUserInfo(user);
                return Result.success("用户信息更新成功", userInfo);
            } else {
                return Result.error("用户信息更新失败");
            }

        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("系统错误，请稍后重试");
        }
    }

    @Override
    @Transactional
    public Result uploadAvatar(Long userId, MultipartFile avatar) {
        try {
            // 检查用户是否存在
            User user = userMapper.findById(userId);
            if (user == null) {
                return Result.error("用户不存在");
            }

            // 验证头像文件
            if (avatar == null || avatar.isEmpty()) {
                return Result.error("头像文件不能为空");
            }

            String avatarValidation = FileUploadUtil.validateFile(avatar, "头像");
            if (avatarValidation != null) {
                return Result.error(avatarValidation);
            }

            // 保存头像文件
            String avatarPath = saveFile(avatar, "avatar", userId);
            if (avatarPath == null) {
                return Result.error("头像上传失败");
            }

            // 更新用户头像路径
            user.setAvatar(avatarPath);
            user.setUpdateTime(LocalDateTime.now());

            int result = userMapper.update(user);
            if (result > 0) {
                return Result.success("头像上传成功", avatarPath);
            } else {
                return Result.error("头像更新失败");
            }

        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("系统错误，请稍后重试");
        }
    }

    /**
     * 保存上传的文件
     */
    private String saveFile(MultipartFile file, String type, Long userId) throws IOException {
        if (file.isEmpty()) {
            return null;
        }

        // 检查文件大小
        if (file.getSize() > maxFileSize) {
            throw new IOException("文件大小超过限制");
        }

        // 检查文件类型
        if (!FileUploadUtil.isImageFile(file)) {
            throw new IOException("文件类型不支持");
        }

        // 创建上传目录
        File dir = new File(uploadDir + type);
        if (!dir.exists()) {
            boolean created = dir.mkdirs();
            if (!created) {
                throw new IOException("创建上传目录失败");
            }
        }

        // 获取文件扩展名
        String originalFilename = file.getOriginalFilename();
        String fileExtension = FileUploadUtil.getFileExtension(originalFilename);

        // 如果无法获取扩展名，根据内容类型确定
        if (fileExtension.isEmpty()) {
            String contentType = file.getContentType();
            if (contentType != null) {
                switch (contentType) {
                    case "image/jpeg":
                        fileExtension = "jpeg";
                        break;
                    case "image/jpg":
                        fileExtension = "jpg";
                        break;
                    case "image/png":
                        fileExtension = "png";
                        break;
                    default:
                        throw new IOException("不支持的图片格式");
                }
            } else {
                throw new IOException("无法确定文件类型");
            }
        }

        // 根据类型生成文件名
        String prefix = "";
        if ("avatar".equals(type)) {
            prefix = "A" + userId;
        } else if ("certificate".equals(type)) {
            prefix = "C" + userId;
        } else {
            prefix = UUID.randomUUID().toString(); // 其他类型使用UUID
        }

        // 检查并删除可能存在的同名文件
        deleteExistingFiles(dir, prefix);

        String filename = prefix + "." + fileExtension;
        String filePath = dir.getAbsolutePath() + File.separator + filename;

        // 保存文件
        File dest = new File(filePath);
        try {
            file.transferTo(dest);
        } catch (IOException e) {
            throw new IOException("文件保存失败: " + e.getMessage());
        }

        // 返回相对路径
        return type + "/" + filename;
    }

    /**
     * 删除已存在的同名文件（检查三类常见图片后缀）
     */
    private void deleteExistingFiles(File directory, String filenamePrefix) {
        // 常见的图片后缀
        String[] imageExtensions = {"jpg", "jpeg", "png"};

        for (String ext : imageExtensions) {
            String filename = filenamePrefix + "." + ext;
            File existingFile = new File(directory, filename);
            if (existingFile.exists()) {
                boolean deleted = existingFile.delete();
                if (!deleted) {
                    System.err.println("无法删除已存在的文件: " + existingFile.getAbsolutePath());
                }
            }
        }
    }

    /**
     * 将User实体转换为UserInfo数据
     */
    private UserInfo convertToUserInfo(User user) {
        UserInfo userInfo = new UserInfo();
        userInfo.setId(user.getUserId());
        userInfo.setPhone(user.getPhone());
        userInfo.setUsername(user.getUsername());
        userInfo.setRole(user.getRole());
        userInfo.setStudentId(user.getStudentId());
        userInfo.setVerified("1".equals(user.getRole())); // 跑腿员为已认证
        userInfo.setCreatedTime(user.getCreateTime());
        userInfo.setUpdatedTime(user.getUpdateTime());

        // 设置头像URL
        userInfo.setAvatarUrl(baseUrl + "/uploads/" + user.getAvatar());

        // 设置证件URL
        if (user.getCertificate() != null && !user.getCertificate().isEmpty()) {
            userInfo.setCertificateUrl(baseUrl + "/uploads/" + user.getCertificate());
        }

        return userInfo;
    }
}
