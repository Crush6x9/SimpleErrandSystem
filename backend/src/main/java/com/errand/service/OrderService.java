package com.errand.service;

import com.errand.dto.OrderQueryRequest;
import com.errand.dto.OrderRequest;
import com.errand.dto.Result;

public interface OrderService {
    Result publishOrder(Long clientId, OrderRequest request);

    Result acceptOrder(Long helperId, Long orderId);

    Result completeOrder(Long userId, Long orderId);

    Result cancelOrder(Long orderId, Long userId);

    Result cancelAcceptOrder(Long orderId, Long userId);

    Result getOrderStats(Long userId);

    Result getOrderList(OrderQueryRequest request, Long userId);

    Result getOrderDetail(Long orderId, Long userId);
}
