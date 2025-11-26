package com.errand.mapper;

import com.errand.entity.Withdrawal;
import org.apache.ibatis.annotations.Mapper;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface WithdrawalMapper {
    /**
     * 新增提现
     *
     * @param withdrawal 提现信息
     * @return 结果
     */
    int insertWithdrawal(Withdrawal withdrawal);

    /**
     * 根据用户ID查询提现
     *
     * @param userId 用户ID
     * @return 提现列表
     */
    List<Withdrawal> selectWithdrawalsByUserId(Long userId);

    /**
     * 更新提现状态为已完成
     *
     * @param expiredTime 过期时间
     * @return 更新记录数
     */
    int updateExpiredWithdrawals(LocalDateTime expiredTime);
}
