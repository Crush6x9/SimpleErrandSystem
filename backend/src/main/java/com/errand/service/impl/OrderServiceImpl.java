package com.errand.service.impl;

import com.errand.dto.OrderInfo;
import com.errand.dto.OrderQueryRequest;
import com.errand.dto.OrderRequest;
import com.errand.dto.Result;
import com.errand.entity.Order;
import com.errand.mapper.EvaluationMapper;
import com.errand.mapper.OrderMapper;
import com.errand.mapper.UserMapper;
import com.errand.service.OrderService;
import com.errand.service.WalletService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderServiceImpl implements OrderService {
    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private EvaluationMapper evaluationMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private WalletService walletService;

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    @Transactional
    public Result publishOrder(Long clientId, OrderRequest request) {
        try {
            Order order = new Order();
            order.setClientId(clientId);
            order.setContent(request.getContent());
            order.setHelpTime(request.getHelpTime());
            order.setPhone(request.getPhone());
            order.setReward(request.getReward());
            order.setStatus("0");
            order.setPublishTime(LocalDateTime.now());

            int result = orderMapper.insertOrder(order);
            if (result > 0) {
                return Result.success("订单发布成功", order.getOrderId());
            } else {
                return Result.error("订单发布失败");
            }
        } catch (Exception e) {
            throw new RuntimeException("发布订单异常", e);
        }
    }

    @Override
    @Transactional
    public Result acceptOrder(Long helperId, Long orderId) {
        try {
            Order order = orderMapper.selectOrderById(orderId);
            if (order == null) {
                return Result.error("订单不存在");
            }
            if (!"0".equals(order.getStatus())) {
                return Result.error("订单已被接取");
            }

            // 检查用户是否为跑腿员
            String role = userMapper.selectRoleById(helperId);
            if (!"1".equals(role)) {
                return Result.error("只有跑腿员可以接单");
            }

            // 不能接取自己发布的订单
            if (order.getClientId().equals(helperId)) {
                return Result.error("不能接取自己发布的订单");
            }

            order.setHelperId(helperId);
            order.setStatus("1");
            order.setAcceptTime(LocalDateTime.now());

            int result = orderMapper.updateOrder(order);
            if (result > 0) {
                return Result.success("接单成功");
            } else {
                return Result.error("接单失败");
            }
        } catch (Exception e) {
            throw new RuntimeException("接单异常", e);
        }
    }

    @Override
    @Transactional
    public Result completeOrder(Long userId, Long orderId) {
        try {
            Order order = orderMapper.selectOrderById(orderId);
            if (order == null) {
                return Result.error("订单不存在");
            }

            // 验证权限：只有跑腿员可以完成订单
            if (!order.getHelperId().equals(userId)) {
                return Result.error("无权操作此订单");
            }
            if (!"1".equals(order.getStatus())) {
                return Result.error("订单状态异常");
            }

            order.setStatus("2");
            order.setCompleteTime(LocalDateTime.now());

            int result = orderMapper.updateOrder(order);
            if (result > 0) {
                // 完成订单后，将悬赏金额转入跑腿员钱包
                walletService.addIncome(order.getHelperId(), order.getReward());
                return Result.success("订单完成成功");
            } else {
                return Result.error("订单完成失败");
            }
        } catch (Exception e) {
            throw new RuntimeException("完成订单异常", e);
        }
    }

    @Override
    @Transactional
    public Result cancelOrder(Long orderId, Long userId) {
        try {
            Order order = orderMapper.selectOrderById(orderId);
            if (order == null) {
                return Result.error("订单不存在");
            }

            // 只有发布者可以取消订单，且只能取消待帮助的订单
            if (!order.getClientId().equals(userId)) {
                return Result.error("无权取消此订单");
            }
            if (!"0".equals(order.getStatus())) {
                return Result.error("只能取消待帮助的订单");
            }

            int result = orderMapper.deleteOrder(orderId);
            if (result > 0) {
                return Result.success("订单取消成功");
            } else {
                return Result.error("订单取消失败");
            }
        } catch (Exception e) {
            throw new RuntimeException("取消订单异常", e);
        }
    }

    @Override
    public Result getOrderList(OrderQueryRequest request, Long userId) {
        try {
            List<Order> orders = orderMapper.selectOrdersByCondition(request, userId);
            List<OrderInfo> orderInfos = orders.stream().map(this::convertToOrderInfo).collect(Collectors.toList());

            // 添加分页信息
            int total = orderMapper.countOrdersByCondition(request, userId);
            OrderListResponse response = new OrderListResponse();
            response.setOrders(orderInfos);
            response.setTotal(total);
            response.setPage(request.getPage());
            response.setSize(request.getSize());

            return Result.success("查询成功", response);
        } catch (Exception e) {
            return Result.error("查询订单列表失败");
        }
    }

    @Override
    public Result getOrderDetail(Long orderId, Long userId) {
        try {
            Order order = orderMapper.selectOrderById(orderId);
            if (order == null) {
                return Result.error("订单不存在");
            }

            // 检查权限：只有订单相关用户可以查看详情
            if (!order.getClientId().equals(userId) &&
                    (order.getHelperId() == null || !order.getHelperId().equals(userId))) {
                return Result.error("无权查看此订单");
            }

            OrderInfo orderInfo = convertToOrderInfo(order);
            return Result.success("获取订单详情成功", orderInfo);
        } catch (Exception e) {
            return Result.error("获取订单详情失败");
        }
    }

    @Override
    public Result getAvailableOrders() {
        try {
            List<Order> orders = orderMapper.selectAvailableOrders();
            List<OrderInfo> orderInfos = orders.stream().map(this::convertToOrderInfo).collect(Collectors.toList());
            return Result.success("获取可接订单成功", orderInfos);
        } catch (Exception e) {
            return Result.error("获取可接订单失败");
        }
    }

    private OrderInfo convertToOrderInfo(Order order) {
        OrderInfo info = new OrderInfo();
        info.setOrderId(order.getOrderId());
        info.setClientId(order.getClientId());
        info.setHelperId(order.getHelperId());
        info.setContent(order.getContent());
        info.setHelpTime(order.getHelpTime());
        info.setPhone(order.getPhone());
        info.setReward(order.getReward());
        info.setStatus(order.getStatus());
        info.setPublishTime(order.getPublishTime());
        info.setAcceptTime(order.getAcceptTime());
        info.setCompleteTime(order.getCompleteTime());
        info.setClientUsername(order.getClientUsername());
        info.setHelperUsername(order.getHelperUsername());
        info.setClientAvatar(order.getClientAvatar());
        info.setHelperAvatar(order.getHelperAvatar());
        info.setEvaluated(evaluationMapper.existsEvaluationByOrderId(order.getOrderId()));
        return info;
    }
}
