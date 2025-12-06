package com.errand.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(description = "订单查询请求")
public class OrderQueryRequest {
    @ApiModelProperty(value = "查询类型")
    private String type = "all";

    @ApiModelProperty(value = "页码")
    private Integer page = 1;

    @ApiModelProperty(value = "每页大小")
    private Integer size = 10;

    public OrderQueryRequest() {}

    public OrderQueryRequest(String type, Integer page, Integer size) {
        this.type = type;
        this.page = page;
        this.size = size;
    }

    // 计算偏移量
    public Integer getOffset() {
        return size * (page - 1);
    }
}
