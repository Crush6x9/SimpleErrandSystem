package com.errand.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import java.math.BigDecimal;

@Data
@ApiModel(description = "账单项")
public class BillInfo {
    @ApiModelProperty(value = "订单ID")
    private Long orderId;

    @ApiModelProperty(value = "收益金额")
    private BigDecimal income;

    @ApiModelProperty(value = "完成时间")
    private String completeTime;

    @ApiModelProperty(value = "订单主题")
    private String title;
}
