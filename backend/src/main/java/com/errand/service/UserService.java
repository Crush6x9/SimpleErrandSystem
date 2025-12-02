package com.errand.service;

import com.errand.dto.*;
import org.springframework.web.multipart.MultipartFile;

public interface UserService {
    Result register(RegisterRequest request);

    Result login(LoginRequest request);

    Result checkPhoneExists(ForgetPasswordRequest request);

    Result resetPassword(ResetPasswordRequest request);

    Result updateUsername(Long userId, UpdateUsernameRequest request);

    Result certification(Long userId, CertificationRequest request);

    Result getUserInfo(Long userId);

    Result updateUserInfo(Long userId, UpdateUserInfoRequest request);

    Result uploadAvatar(Long userId, MultipartFile avatar);
}
