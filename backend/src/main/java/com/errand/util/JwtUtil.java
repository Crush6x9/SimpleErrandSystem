package com.errand.util;

import io.jsonwebtoken.*;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtUtil {
    private static final Logger logger = LoggerFactory.getLogger(JwtUtil.class);

    // 默认密钥，生产环境应该从配置文件中读取
    @Value("${jwt.secret:errandSystemSecretKey2025ForSimpleErrandApplication}")
    private String secret;

    // 获取过期时间的方法
    // 默认过期时间：24小时
    @Getter
    @Value("${jwt.expiration:86400000}")
    private Long expiration;

    // 刷新令牌过期时间：7天
    @Value("${jwt.refresh-expiration:604800000}")
    private Long refreshExpiration;

    // 令牌前缀
    private static final String TOKEN_PREFIX = "Bearer ";

    // 令牌头名称
    private static final String HEADER_STRING = "Authorization";

    // 声明中的用户ID键
    private static final String CLAIM_KEY_USERID = "userId";

    // 声明中的用户手机号键
    private static final String CLAIM_KEY_PHONE = "sub";

    // 声明中的用户角色键
    private static final String CLAIM_KEY_ROLE = "role";

    // 声明中的创建时间键
    private static final String CLAIM_KEY_CREATED = "created";

    /**
     * 生成访问令牌
     */
    public String generateToken(Long userId, String phone, String role) {
        Map<String, Object> claims = new HashMap<>();
        claims.put(CLAIM_KEY_USERID, userId);
        claims.put(CLAIM_KEY_ROLE, role);
        claims.put(CLAIM_KEY_CREATED, new Date());

        return generateToken(claims, phone, expiration);
    }

    /**
     * 生成刷新令牌
     */
    public String generateRefreshToken(Long userId, String phone, String role) {
        Map<String, Object> claims = new HashMap<>();
        claims.put(CLAIM_KEY_USERID, userId);
        claims.put(CLAIM_KEY_ROLE, role);
        claims.put(CLAIM_KEY_CREATED, new Date());
        claims.put("type", "refresh");

        return generateToken(claims, phone, refreshExpiration);
    }

    /**
     * 生成令牌
     */
    private String generateToken(Map<String, Object> claims, String subject, Long expiration) {
        Date createdDate = new Date();
        Date expirationDate = new Date(createdDate.getTime() + expiration);

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(createdDate)
                .setExpiration(expirationDate)
                .signWith(SignatureAlgorithm.HS256, secret)
                .compact();
    }

    /**
     * 从令牌中获取用户ID
     */
    public Long getUserIdFromToken(String token) {
        try {
            Claims claims = getClaimsFromToken(token);
            return claims.get(CLAIM_KEY_USERID, Long.class);
        } catch (Exception e) {
            logger.error("从令牌获取用户ID失败", e);
            return null;
        }
    }

    /**
     * 从令牌中获取手机号
     */
    public String getPhoneFromToken(String token) {
        try {
            Claims claims = getClaimsFromToken(token);
            return claims.getSubject();
        } catch (Exception e) {
            logger.error("从令牌获取手机号失败", e);
            return null;
        }
    }

    /**
     * 从令牌中获取用户角色
     */
    public String getRoleFromToken(String token) {
        try {
            Claims claims = getClaimsFromToken(token);
            return claims.get(CLAIM_KEY_ROLE, String.class);
        } catch (Exception e) {
            logger.error("从令牌获取用户角色失败", e);
            return null;
        }
    }

    /**
     * 从令牌中获取创建时间
     */
    public Date getCreatedDateFromToken(String token) {
        try {
            Claims claims = getClaimsFromToken(token);
            return claims.get(CLAIM_KEY_CREATED, Date.class);
        } catch (Exception e) {
            logger.error("从令牌获取创建时间失败", e);
            return null;
        }
    }

    /**
     * 从令牌中获取过期时间
     */
    public Date getExpirationDateFromToken(String token) {
        try {
            Claims claims = getClaimsFromToken(token);
            return claims.getExpiration();
        } catch (Exception e) {
            logger.error("从令牌获取过期时间失败", e);
            return null;
        }
    }

    /**
     * 验证令牌是否有效
     */
    public boolean validateToken(String token) {
        try {
            getClaimsFromToken(token);
            return true;
        } catch (ExpiredJwtException e) {
            logger.error("令牌已过期: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            logger.error("不支持的令牌: {}", e.getMessage());
        } catch (MalformedJwtException e) {
            logger.error("令牌格式错误: {}", e.getMessage());
        } catch (SignatureException e) {
            logger.error("令牌签名验证失败: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            logger.error("令牌参数错误: {}", e.getMessage());
        } catch (Exception e) {
            logger.error("令牌验证失败: {}", e.getMessage());
        }
        return false;
    }

    /**
     * 检查令牌是否即将过期（在指定时间内过期）
     */
    public boolean isTokenExpiringSoon(String token, long millis) {
        try {
            Date expiration = getExpirationDateFromToken(token);
            if (expiration == null) {
                return false;
            }
            return expiration.getTime() - System.currentTimeMillis() <= millis;
        } catch (Exception e) {
            logger.error("检查令牌过期状态失败", e);
            return false;
        }
    }

    /**
     * 刷新令牌
     */
    public String refreshToken(String token) {
        try {
            Claims claims = getClaimsFromToken(token);
            Long userId = claims.get(CLAIM_KEY_USERID, Long.class);
            String phone = claims.getSubject();
            String role = claims.get(CLAIM_KEY_ROLE, String.class);

            return generateToken(userId, phone, role);
        } catch (Exception e) {
            logger.error("刷新令牌失败", e);
            return null;
        }
    }

    /**
     * 从令牌中获取声明
     */
    private Claims getClaimsFromToken(String token) {
        return Jwts.parser()
                .setSigningKey(secret)
                .parseClaimsJws(token)
                .getBody();
    }
}
