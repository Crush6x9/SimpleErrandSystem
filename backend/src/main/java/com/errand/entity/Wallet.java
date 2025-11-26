package com.errand.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@ApiModel(description = "钱包")
public class Wallet {
    @ApiModelProperty(value = "钱包ID")
    private Long walletId;

    @ApiModelProperty(value = "用户ID", example = "101")
    private Long userId;

    @ApiModelProperty(value = "总收益")
    private BigDecimal totalIncome;

    @ApiModelProperty(value = "可用余额")
    private BigDecimal balance;

    @ApiModelProperty(value = "创建时间")
    private LocalDateTime createTime;

    @ApiModelProperty(value = "更新时间")
    private LocalDateTime updateTime;
}
