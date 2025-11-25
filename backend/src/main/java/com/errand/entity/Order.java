package com.errand.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@ApiModel(description = "订单")
public class Order {
    @ApiModelProperty(value = "订单ID")
    private Long orderId;

    @ApiModelProperty(value = "委托人ID", example = "101")
    private Long clientId;

    @ApiModelProperty(value = "跑腿员ID", example = "101")
    private Long helperId;

    @ApiModelProperty(value = "订单需求内容")
    private String content;

    @ApiModelProperty(value = "订单指定时间")
    private LocalDateTime helpTime;

    @ApiModelProperty(value = "联系电话")
    private String phone;

    @ApiModelProperty(value = "悬赏金额")
    private BigDecimal reward;

    @ApiModelProperty(value = "订单状态", example = "0")
    private String status;

    @ApiModelProperty(value = "发布时间")
    private LocalDateTime publishTime;

    @ApiModelProperty(value = "接单时间")
    private LocalDateTime acceptTime;

    @ApiModelProperty(value = "完成时间")
    private LocalDateTime completeTime;

    @ApiModelProperty(value = "委托人用户名")
    private String clientUsername;

    @ApiModelProperty(value = "跑腿员用户名")
    private String helperUsername;

    @ApiModelProperty(value = "委托人头像路径")
    private String clientAvatar;

    @ApiModelProperty(value = "跑腿员头像路径")
    private String helperAvatar;
}
