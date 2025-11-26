package com.errand.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@ApiModel(description = "订单请求")
public class OrderRequest {
    @NotBlank(message = "订单内容不能为空")
    @ApiModelProperty(value = "订单需求内容", required = true)
    private String content;

    @NotNull(message = "帮助时间不能为空")
    @ApiModelProperty(value = "订单指定时间", required = true)
    private LocalDateTime helpTime;

    @NotBlank(message = "联系电话不能为空")
    @ApiModelProperty(value = "联系电话", required = true)
    private String phone;

    @NotNull(message = "悬赏金额不能为空")
    @ApiModelProperty(value = "悬赏金额", required = true)
    private BigDecimal reward;
}
