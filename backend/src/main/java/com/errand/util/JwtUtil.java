package com.errand.util;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtUtil {
    private static final Logger LOGGER = LoggerFactory.getLogger(JwtUtil.class);

    // 默认密钥
    @Value("${jwt.secret}")
    private String secret;

    // 访问令牌过期时间：30分钟
    @Getter
    @Value("${jwt.expiration}")
    private Long expiration;

    // 刷新令牌过期时间：7天
    @Value("${jwt.refresh-expiration}")
    private Long refreshExpiration;

    // 令牌前缀
    private static final String TOKEN_PREFIX = "Bearer ";

    // 令牌头名称
    private static final String HEADER_STRING = "Authorization";

    // 声明中的用户ID键
    private static final String CLAIM_KEY_USERID = "userId";

    // 声明中的用户角色键
    private static final String CLAIM_KEY_ROLE = "role";

    // 声明中的创建时间键
    private static final String CLAIM_KEY_CREATED = "created";

    /**
     * 生成访问令牌
     */
    public String generateToken(Long userId, String role) {
        Map<String, Object> claims = new HashMap<>();
        claims.put(CLAIM_KEY_USERID, userId);
        claims.put(CLAIM_KEY_ROLE, role);
        claims.put(CLAIM_KEY_CREATED, new Date());

        return generateToken(claims, expiration);
    }

    /**
     * 生成刷新令牌
     */
    public String generateRefreshToken(Long userId, String role) {
        Map<String, Object> claims = new HashMap<>();
        claims.put(CLAIM_KEY_USERID, userId);
        claims.put(CLAIM_KEY_ROLE, role);
        claims.put(CLAIM_KEY_CREATED, new Date());
        claims.put("type", "refresh");

        return generateToken(claims, refreshExpiration);
    }

    /**
     * 从请求头中提取令牌
     */
    public String extractToken(String originalToken) {
        if (originalToken == null) {
            return null;
        }

        // 移除令牌前缀
        return originalToken.substring(TOKEN_PREFIX.length());
    }

    /**
     * 从令牌中获取用户ID
     */
    public Long getUserIdFromToken(String originalToken) {
        String token = extractToken(originalToken);
        try {
            Claims claims = getClaimsFromToken(token);
            return claims.get(CLAIM_KEY_USERID, Long.class);
        } catch (Exception e) {
            LOGGER.error("从令牌获取用户ID失败", e);
            return null;
        }
    }

    /**
     * 从令牌中获取用户角色
     */
    public String getRoleFromToken(String originalToken) {
        String token = extractToken(originalToken);
        try {
            Claims claims = getClaimsFromToken(token);
            return claims.get(CLAIM_KEY_ROLE, String.class);
        } catch (Exception e) {
            LOGGER.error("从令牌获取用户角色失败", e);
            return null;
        }
    }

    /**
     * 从令牌中获取创建时间
     */
    public Date getCreatedDateFromToken(String originalToken) {
        String token = extractToken(originalToken);
        try {
            Claims claims = getClaimsFromToken(token);
            return claims.get(CLAIM_KEY_CREATED, Date.class);
        } catch (Exception e) {
            LOGGER.error("从令牌获取创建时间失败", e);
            return null;
        }
    }

    /**
     * 从令牌中获取过期时间
     */
    public Date getExpirationDateFromToken(String originalToken) {
        String token = extractToken(originalToken);
        try {
            Claims claims = getClaimsFromToken(token);
            return claims.getExpiration();
        } catch (Exception e) {
            LOGGER.error("从令牌获取过期时间失败", e);
            return null;
        }
    }

    /**
     * 验证令牌是否有效
     */
    public boolean validateToken(String originalToken) {
        String token = extractToken(originalToken);
        try {
            Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (ExpiredJwtException e) {
            LOGGER.error("令牌已过期: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            LOGGER.error("不支持的令牌: {}", e.getMessage());
        } catch (MalformedJwtException e) {
            LOGGER.error("令牌格式错误: {}", e.getMessage());
        } catch (SignatureException e) {
            LOGGER.error("令牌签名验证失败: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            LOGGER.error("令牌参数错误: {}", e.getMessage());
        } catch (Exception e) {
            LOGGER.error("令牌验证失败: {}", e.getMessage());
        }
        return false;
    }

    /**
     * 检查令牌是否即将过期（在指定时间内过期）
     */
    public boolean isTokenExpiringSoon(String originalToken, long millis) {
        String token = extractToken(originalToken);
        try {
            Date expiration = getExpirationDateFromToken(token);
            if (expiration == null) {
                return false;
            }
            return expiration.getTime() - System.currentTimeMillis() <= millis;
        } catch (Exception e) {
            LOGGER.error("检查令牌过期状态失败", e);
            return false;
        }
    }

    /**
     * 刷新令牌
     */
    public String refreshToken(String originalToken) {
        String token = extractToken(originalToken);
        try {
            Claims claims = getClaimsFromToken(token);
            Long userId = claims.get(CLAIM_KEY_USERID, Long.class);
            String role = claims.get(CLAIM_KEY_ROLE, String.class);

            return generateToken(userId, role);
        } catch (Exception e) {
            LOGGER.error("刷新令牌失败", e);
            return null;
        }
    }

    /**
     * 生成令牌
     */
    private String generateToken(Map<String, Object> claims, Long expiration) {
        Date createdDate = new Date();
        Date expirationDate = new Date(createdDate.getTime() + expiration);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(createdDate)
                .setExpiration(expirationDate)
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * 从令牌中获取声明
     */
    private Claims getClaimsFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * 获取签名密钥
     */
    private SecretKey getSigningKey() {
        byte[] keyBytes = secret.getBytes();
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
