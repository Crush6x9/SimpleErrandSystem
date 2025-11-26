package com.errand.dto;

import lombok.Data;
import java.util.List;

@Data
public class OrderResponse {
    private List<OrderInfo> orders;
    private Integer total;
    private Integer page;
    private Integer size;

    public OrderResponse() {}

    public OrderResponse(List<OrderInfo> orders, Integer total, Integer page, Integer size) {
        this.orders = orders;
        this.total = total;
        this.page = page;
        this.size = size;
    }
}
