package com.errand.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data
@ApiModel(description = "提现请求")
public class WithdrawalRequest {
    @NotNull(message = "提现金额不能为空")
    @ApiModelProperty(value = "提现金额", required = true)
    private BigDecimal amount;
}
