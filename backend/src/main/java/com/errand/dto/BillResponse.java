package com.errand.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

@Data
@ApiModel(description = "账单信息")
public class BillResponse {
    @ApiModelProperty(value = "今日收益")
    private BigDecimal todayIncome;

    @ApiModelProperty(value = "账单列表")
    private List<BillInfo> bills;

    @ApiModelProperty(value = "总记录数")
    private Long total;

    @ApiModelProperty(value = "当前页码")
    private Integer page;

    @ApiModelProperty(value = "每页大小")
    private Integer size;
}