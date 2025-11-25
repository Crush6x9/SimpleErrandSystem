package com.errand.util.task;

import com.errand.mapper.WithdrawalMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime;

@Slf4j
@Component
public class WithdrawalScheduledTask {

    @Autowired
    private WithdrawalMapper withdrawalMapper;

    /**
     * 检查并更新提现记录状态（每10分钟执行一次）
     */
    @Scheduled(fixedRate = 600000) // 10分钟
    public void processWithdrawalStatus() {
        try {
            LocalDateTime twoHoursAgo = LocalDateTime.now().minusHours(2);
            int updatedCount = withdrawalMapper.updateExpiredWithdrawals(twoHoursAgo);

            if (updatedCount > 0) {
                log.info("成功更新 {} 条提现记录状态为已完成", updatedCount);
            }
        } catch (Exception e) {
            log.error("处理提现状态更新失败", e);
        }
    }
}
