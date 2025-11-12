package com.errand.controller;

import com.errand.dto.OrderQueryRequest;
import com.errand.dto.OrderRequest;
import com.errand.dto.Result;
import com.errand.service.OrderService;
import com.errand.util.JwtUtil;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/orders")
public class OrderController {
    @Autowired
    private OrderService orderService;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping
    @ApiOperation("发布订单")
    public Result publishOrder(
            @RequestHeader("Authorization") String token,
            @RequestBody @Valid OrderRequest request) {
        // 从token中获取用户ID
        Long userId = jwtUtil.getUserIdFromToken(token.replace("Bearer ", ""));
        return orderService.publishOrder(userId, request);
    }

    @PutMapping("/{orderId}/accept")
    @ApiOperation("接取订单")
    public Result acceptOrder(
            @RequestHeader("Authorization") String token,
            @PathVariable Long orderId) {
        Long userId = jwtUtil.getUserIdFromToken(token.replace("Bearer ", ""));
        return orderService.acceptOrder(userId, orderId);
    }

    @PutMapping("/{orderId}/complete")
    @ApiOperation("完成订单")
    public Result completeOrder(
            @RequestHeader("Authorization") String token,
            @PathVariable Long orderId) {
        Long userId = jwtUtil.getUserIdFromToken(token.replace("Bearer ", ""));
        return orderService.completeOrder(userId, orderId);
    }

    @GetMapping
    @ApiOperation("查询订单列表")
    public Result getOrderList(
            @RequestHeader("Authorization") String token,
            OrderQueryRequest request) {
        Long userId = jwtUtil.getUserIdFromToken(token.replace("Bearer ", ""));
        return orderService.getOrderList(request, userId);
    }
}
