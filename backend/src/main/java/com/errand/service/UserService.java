package com.errand.service;

import com.errand.dto.LoginRequest;
import com.errand.dto.RegisterRequest;
import com.errand.dto.Result;

public interface UserService {
    Result register(RegisterRequest request);

    Result login(LoginRequest request);
}
