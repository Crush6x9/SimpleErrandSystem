package com.errand.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@ApiModel(description = "评价")
public class Evaluation {
    @ApiModelProperty(value = "评价ID")
    private Long evaluationId;

    @ApiModelProperty(value = "订单ID")
    private Long orderId;

    @ApiModelProperty(value = "评价信息", example = "0")
    private String review;

    @ApiModelProperty(value = "创建时间", example = "0")
    private LocalDateTime createTime;
}
