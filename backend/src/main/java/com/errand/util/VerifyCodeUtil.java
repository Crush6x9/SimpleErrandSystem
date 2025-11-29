package com.errand.util;

import com.errand.dto.Result;
import com.errand.dto.VerifyCodeResponse;
import org.springframework.stereotype.Component;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

@Component
public class VerifyCodeUtil {
    // 存储验证码，key:手机号, value:验证码和生成时间
    private final Map<String, VerifyCodeUtil.CodeInfo> codeStorage = new ConcurrentHashMap<>();

    // 验证码有效期（5分钟）
    private static final long EXPIRE_TIME = 5 * 60 * 1000;

    // 验证码长度
    private static final int CODE_LENGTH = 6;

    private static class CodeInfo {
        String code;
        long generateTime;

        CodeInfo(String code, long generateTime) {
            this.code = code;
            this.generateTime = generateTime;
        }

        boolean isExpired() {
            return System.currentTimeMillis() - generateTime > EXPIRE_TIME;
        }
    }

    /**
     * 生成验证码
     */
    public Result generateCode(String phone) {
        // 生成6位随机数字验证码
        String code = generateRandomCode();

        // 存储验证码
        codeStorage.put(phone, new VerifyCodeUtil.CodeInfo(code, System.currentTimeMillis()));

        // 构建响应
        VerifyCodeResponse response = new VerifyCodeResponse(
                code,
                (int) TimeUnit.MILLISECONDS.toSeconds(EXPIRE_TIME)
        );

        return Result.success("验证码生成成功", response);
    }

    /**
     * 验证验证码
     */
    public Result verifyCode(String phone, String code) {
        VerifyCodeUtil.CodeInfo codeInfo = codeStorage.get(phone);

        if (codeInfo == null) {
            return Result.error("验证码不存在或已过期");
        }

        if (codeInfo.isExpired()) {
            codeStorage.remove(phone);
            return Result.error("验证码已过期");
        }

        if (!codeInfo.code.equals(code)) {
            return Result.error("验证码错误");
        }

        return Result.success("验证码验证成功");
    }

    /**
     * 移除验证码
     */
    public void removeCode(String phone) {
        codeStorage.remove(phone);
    }

    /**
     * 生成随机6位数字验证码
     */
    private String generateRandomCode() {
        Random random = new Random();
        StringBuilder code = new StringBuilder();
        for (int i = 0; i < CODE_LENGTH; i++) {
            code.append(random.nextInt(10));
        }
        return code.toString();
    }
}
