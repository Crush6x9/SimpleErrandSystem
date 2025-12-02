package com.errand.service.impl;

import com.errand.dto.*;
import com.errand.entity.Wallet;
import com.errand.entity.Withdrawal;
import com.errand.mapper.OrderMapper;
import com.errand.mapper.WalletMapper;
import com.errand.mapper.WithdrawalMapper;
import com.errand.service.WalletService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class WalletServiceImpl implements WalletService {
    @Autowired
    private WalletMapper walletMapper;

    @Autowired
    private WithdrawalMapper withdrawalMapper;

    @Autowired
    private OrderMapper orderMapper;

    @Override
    public Result getWalletInfo(Long userId) {
        try {
            Wallet wallet = walletMapper.selectWalletByUserId(userId);
            if (wallet == null) {
                // 如果钱包不存在，创建新钱包
                wallet = new Wallet();
                wallet.setUserId(userId);
                wallet.setTotalIncome(BigDecimal.ZERO);
                wallet.setBalance(BigDecimal.ZERO);
                wallet.setCreateTime(LocalDateTime.now());
                walletMapper.insertWallet(wallet);
            }
            WalletInfo walletInfo = convertToWalletInfo(wallet);
            return Result.success("获取钱包信息成功", walletInfo);
        } catch (Exception e) {
            return Result.error("获取钱包信息失败");
        }
    }

    @Override
    @Transactional
    public Result addIncome(Long userId, BigDecimal amount) {
        try {
            int result = walletMapper.addIncome(userId, amount);
            if (result > 0) {
                return Result.success("收入添加成功");
            } else {
                return Result.error("收入添加失败");
            }
        } catch (Exception e) {
            throw new RuntimeException("添加收入异常", e);
        }
    }

    @Override
    @Transactional
    public Result withdraw(Long userId, WithdrawalRequest request) {
        try {
            Wallet wallet = walletMapper.selectWalletByUserId(userId);
            if (wallet == null || wallet.getBalance().compareTo(request.getAmount()) < 0) {
                return Result.error("余额不足");
            }

            // 扣减余额
            int deductResult = walletMapper.deductBalance(userId, request.getAmount());
            if (deductResult <= 0) {
                return Result.error("扣减余额失败");
            }

            // 创建提现记录
            Withdrawal withdrawal = new Withdrawal();
            withdrawal.setUserId(userId);
            withdrawal.setAmount(request.getAmount());
            withdrawal.setStatus("0"); // 处理中
            withdrawal.setCreateTime(LocalDateTime.now());

            int withdrawResult = withdrawalMapper.insertWithdrawal(withdrawal);
            if (withdrawResult > 0) {
                return Result.success("提现申请提交成功");
            } else {
                // 回滚余额扣减
                walletMapper.addBalance(userId, request.getAmount());
                return Result.error("提现申请失败");
            }
        } catch (Exception e) {
            throw new RuntimeException("提现异常", e);
        }
    }

    @Override
    public Result getWithdrawalHistory(Long userId) {
        try {
            List<Withdrawal> withdrawals = withdrawalMapper.selectWithdrawalsByUserId(userId);
            return Result.success("获取提现记录成功", withdrawals);
        } catch (Exception e) {
            return Result.error("获取提现记录失败");
        }
    }

    @Override
    public Result getBillList(Long userId, BillQueryRequest request) {
        try {
            // 计算今日收益
            BigDecimal todayIncome = orderMapper.selectTodayIncome(userId);
            if (todayIncome == null) {
                todayIncome = BigDecimal.ZERO;
            }

            // 计算偏移量
            int offset = (request.getPage() - 1) * request.getSize();

            // 查询账单列表
            List<BillInfo> bills = orderMapper.selectBillListByUserId(userId, offset, request.getSize());

            // 查询总记录数
            Long total = orderMapper.countBillListByUserId(userId);

            // 构建响应
            BillResponse response = new BillResponse();
            response.setTodayIncome(todayIncome);
            response.setBills(bills);
            response.setTotal(total);
            response.setPage(request.getPage());
            response.setSize(request.getSize());

            return Result.success("获取账单列表成功", response);
        } catch (Exception e) {
            return Result.error("获取账单列表失败");
        }
    }

    private WalletInfo convertToWalletInfo(Wallet wallet) {
        WalletInfo info = new WalletInfo();
        info.setWalletId(wallet.getWalletId());
        info.setTotalIncome(wallet.getTotalIncome());
        info.setBalance(wallet.getBalance());
        return info;
    }
}
