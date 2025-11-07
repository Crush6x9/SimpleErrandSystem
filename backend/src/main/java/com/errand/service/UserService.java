package com.errand.service;

import com.errand.dto.*;

public interface UserService {
    Result register(RegisterRequest request);

    Result login(LoginRequest request);

    Result checkPhoneExists(ForgetPasswordRequest request);

    Result resetPassword(ResetPasswordRequest request);
}
