package com.errand.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import java.math.BigDecimal;

@Data
@ApiModel(description = "钱包信息")
public class WalletInfo {
    @ApiModelProperty(value = "钱包ID")
    private Long walletId;

    @ApiModelProperty(value = "总收益")
    private BigDecimal totalIncome;

    @ApiModelProperty(value = "可用余额")
    private BigDecimal balance;

    public WalletInfo() {}

    public WalletInfo(Long walletId, BigDecimal totalIncome, BigDecimal balance) {
        this.walletId = walletId;
        this.totalIncome = totalIncome;
        this.balance = balance;
    }
}
