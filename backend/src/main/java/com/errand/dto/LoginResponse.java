package com.errand.dto;

import lombok.Data;

@Data
public class LoginResponse {
    private String token;
    private UserInfo userInfo;

    @Data
    public static class UserInfo {
        private Long id;
        private String phone;
        private Integer role; // 0 CLIENT, 1 COURIER
        private String studentId; // 仅跑腿员有
    }
}
