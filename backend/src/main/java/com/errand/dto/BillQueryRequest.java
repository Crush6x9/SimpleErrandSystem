package com.errand.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(description = "账单查询请求")
public class BillQueryRequest {
    @ApiModelProperty(value = "页码", example = "1")
    private Integer page = 1;

    @ApiModelProperty(value = "每页大小", example = "10")
    private Integer size = 10;

    public BillQueryRequest() {}

    public BillQueryRequest(Integer page, Integer size) {
        this.page = page;
        this.size = size;
    }
}
