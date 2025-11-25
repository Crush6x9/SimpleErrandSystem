package com.errand.mapper;

import com.errand.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;

@Mapper
public interface UserMapper {
    /**
     * 新增用户
     *
     * @param user 新用户
     * @return 结果
     */
    int insert(User user);

    /**
     * 根据手机号查询用户
     *
     * @param phone 手机号
     * @return 用户信息
     */
    User findByPhone(String phone);

    /**
     * 更新用户信息
     *
     * @param user 用户
     * @return 结果
     */
    int update(User user);

    /**
     * 更新用户角色
     *
     * @param id 用户ID
     * @param role 用户角色
     * @param updateTime 更新时间
     * @return 结果
     */
    int updateRole(
            @Param("id") Long id,
            @Param("role") String role,
            @Param("updatedTime") LocalDateTime updateTime
    );

    /**
     * 更新用户认证状态
     *
     * @param id 用户ID
     * @param verified 认证状态
     * @param studentId 学号
     * @param idCardImage 证件
     * @param updatedTime 更新时间
     * @return 结果
     */
    int updateVerification(
            @Param("id") Long id,
            @Param("verified") Boolean verified,
            @Param("studentId") String studentId,
            @Param("idCardImage") String idCardImage,
            @Param("updatedTime") LocalDateTime updatedTime
    );

    /**
     * 更新密码
     *
     * @param id 用户ID
     * @param password 密码
     * @param updatedTime 更新时间
     * @return 结果
     */
    int updatePassword(
            @Param("id") Long id,
            @Param("password") String password,
            @Param("updatedTime") LocalDateTime updatedTime
    );

    /**
     * 根据用户ID查询用户
     *
     * @param userId 用户ID
     * @return 用户
     */
    User findById(@Param("userId") Long userId);

    /**
     * 更新用户名
     *
     * @param userId 用户ID
     * @param username 用户名
     * @param updateTime 更新时间
     * @return 结果
     */
    int updateUsername(
            @Param("userId") Long userId,
            @Param("username") String username,
            @Param("updateTime") LocalDateTime updateTime
    );

    /**
     * 根据用户ID查询用户角色
     *
     * @param userId 用户ID
     * @return 角色信息
     */
    String selectRoleById(Long userId);

    /**
     * 根据用户ID更改角色
     *
     * @param userId 用户ID
     * @param role 角色
     * @return 结果
     */
    int updateRole(
            @Param("userId") Long userId,
            @Param("role") String role);

    /**
     * 根据用户ID更新认证状态
     *
     * @param userId 用户ID
     * @param verified 认证状态
     * @return 结果
     */
    int updateVerified(
            @Param("userId") Long userId,
            @Param("verified") Boolean verified);
}
