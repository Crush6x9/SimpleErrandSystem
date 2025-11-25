package com.errand.mapper;


import com.errand.entity.Wallet;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;

@Mapper
public interface WalletMapper {
    /**
     * 新增钱包
     *
     * @param wallet 新钱包
     * @return 结果
     */
    int insertWallet(Wallet wallet);

    /**
     * 查询用户钱包
     *
     * @param userId 用户ID
     * @return 钱包信息
     */
    Wallet selectWalletByUserId(Long userId);

    /**
     * 更新钱包
     *
     * @param wallet 钱包
     * @return 结果
     */
    int updateWallet(Wallet wallet);

    /**
     * 增加可用余额
     *
     * @param userId 用户ID
     * @param amount 金额
     * @return 结果
     */
    int addBalance(
            @Param("userId") Long userId,
            @Param("amount") BigDecimal amount);

    /**
     * 减少可用余额
     *
     * @param userId 用户ID
     * @param amount 金额
     * @return 结果
     */
    int deductBalance(
            @Param("userId") Long userId,
            @Param("amount") BigDecimal amount);

    /**
     * 增加总收益
     *
     * @param userId 用户ID
     * @param amount 金额
     * @return 结果
     */
    int addIncome(
            @Param("userId") Long userId,
            @Param("amount") BigDecimal amount);
}
