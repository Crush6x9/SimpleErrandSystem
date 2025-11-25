package com.errand.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(description = "订单统计信息")
public class OrderStats {
    @ApiModelProperty(value = "所有订单数")
    private Long totalOrders;

    @ApiModelProperty(value = "未接取订单数")
    private Long availableOrders;

    @ApiModelProperty(value = "本用户发布订单数")
    private Long myPublishedOrders;

    @ApiModelProperty(value = "本用户接取订单数")
    private Long myAcceptedOrders;

    public OrderStats(Long totalOrders, Long availableOrders, Long myPublishedOrders, Long myAcceptedOrders) {
        this.totalOrders = totalOrders;
        this.availableOrders = availableOrders;
        this.myPublishedOrders = myPublishedOrders;
        this.myAcceptedOrders = myAcceptedOrders;
    }
}
