package com.errand.service;

import com.errand.dto.BillQueryRequest;
import com.errand.dto.Result;
import com.errand.dto.WithdrawalRequest;
import java.math.BigDecimal;

public interface WalletService {
    Result getWalletInfo(Long userId);

    Result addIncome(Long userId, BigDecimal amount);

    Result withdraw(Long userId, WithdrawalRequest request);

    Result getWithdrawalHistory(Long userId);

    Result getBillList(Long userId, BillQueryRequest request);
}
