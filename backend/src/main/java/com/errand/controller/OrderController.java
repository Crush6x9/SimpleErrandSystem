package com.errand.controller;

import com.errand.dto.OrderQueryRequest;
import com.errand.dto.OrderRequest;
import com.errand.dto.Result;
import com.errand.service.OrderService;
import com.errand.util.JwtUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Api(tags = {"订单操作"})
@RestController
@RequestMapping("/api/orders")
public class OrderController {
    @Autowired
    private OrderService orderService;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/publish")
    @ApiOperation("发布订单")
    public Result publishOrder(
            @RequestHeader("Authorization") String token,
            @RequestBody @Valid OrderRequest request) {
        Long userId = jwtUtil.getUserIdFromToken(token);
        return orderService.publishOrder(userId, request);
    }

    @PutMapping("/{orderId}/accept")
    @ApiOperation("接取订单")
    public Result acceptOrder(
            @RequestHeader("Authorization") String token,
            @PathVariable Long orderId) {
        Long userId = jwtUtil.getUserIdFromToken(token);
        return orderService.acceptOrder(userId, orderId);
    }

    @PutMapping("/{orderId}/complete")
    @ApiOperation("完成订单")
    public Result completeOrder(
            @RequestHeader("Authorization") String token,
            @PathVariable Long orderId) {
        Long userId = jwtUtil.getUserIdFromToken(token);
        return orderService.completeOrder(userId, orderId);
    }

    @PutMapping("/{orderId}/cancel")
    @ApiOperation("取消订单")
    public Result cancelOrder(
            @RequestHeader("Authorization") String token,
            @PathVariable Long orderId) {
        Long userId = jwtUtil.getUserIdFromToken(token);
        return orderService.cancelOrder(orderId, userId);
    }

    @PutMapping("/{orderId}/cancel-accept")
    @ApiOperation("取消接单")
    public Result cancelAcceptOrder(
            @RequestHeader("Authorization") String token,
            @PathVariable Long orderId) {
        Long userId = jwtUtil.getUserIdFromToken(token);
        return orderService.cancelAcceptOrder(orderId, userId);
    }

    @GetMapping("/stats")
    @ApiOperation("获取订单统计信息")
    public Result getOrderStats(@RequestHeader("Authorization") String token) {
        Long userId = jwtUtil.getUserIdFromToken(token.replace("Bearer ", ""));
        return orderService.getOrderStats(userId);
    }

    @GetMapping("/list")
    @ApiOperation("查询订单列表")
    public Result getOrderList(
            @RequestHeader("Authorization") String token,
            @RequestBody @Valid OrderQueryRequest request) {
        Long userId = jwtUtil.getUserIdFromToken(token);
        return orderService.getOrderList(request, userId);
    }

    @GetMapping("/{orderId}")
    @ApiOperation("获取订单详情")
    public Result getOrderDetail(
            @RequestHeader("Authorization") String token,
            @PathVariable Long orderId) {
        Long userId = jwtUtil.getUserIdFromToken(token);
        return orderService.getOrderDetail(orderId, userId);
    }

    @GetMapping("/available")
    @ApiOperation("获取可接取的订单列表")
    public Result getAvailableOrders() {
        return orderService.getAvailableOrders();
    }
}
