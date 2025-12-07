package com.errand.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@ApiModel(description = "订单信息")
public class OrderInfo {
    @ApiModelProperty(value = "订单ID")
    private Long orderId;

    @ApiModelProperty(value = "委托人ID")
    private Long clientId;

    @ApiModelProperty(value = "跑腿员ID")
    private Long helperId;

    @ApiModelProperty(value = "订单主题")
    private String title;

    @ApiModelProperty(value = "订单指定地点")
    private String address;

    @ApiModelProperty(value = "订单描述")
    private String description;

    @ApiModelProperty(value = "订单指定时间")
    private LocalDateTime helpTime;

    @ApiModelProperty(value = "联系电话")
    private String phone;

    @ApiModelProperty(value = "悬赏金额")
    private BigDecimal reward;

    @ApiModelProperty(value = "订单状态")
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

    @ApiModelProperty(value = "委托人头像")
    private String clientAvatar;

    @ApiModelProperty(value = "跑腿员头像")
    private String helperAvatar;

    @ApiModelProperty(value = "是否已评价")
    private Boolean evaluated;

    @ApiModelProperty(value = "评价（0 差评，1 好评）")
    private String review;
}
