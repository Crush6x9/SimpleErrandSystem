package com.errand.mapper;

import com.errand.dto.OrderQueryRequest;
import com.errand.entity.Order;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface OrderMapper {
    /**
     * 新增订单
     *
     * @param order 新订单
     * @return 结果
     */
    int insertOrder(Order order);

    /**
     * 根据ID查询订单
     *
     * @param orderId 订单ID
     * @return 订单信息
     */
    Order selectOrderById(Long orderId);

    /**
     * 更新订单
     *
     * @param order 订单
     * @return 结果
     */
    int updateOrder(Order order);

    /**
     * 根据条件查询订单
     *
     * @param query 查询请求
     * @param userId 用户ID
     * @return 订单列表
     */
    List<Order> selectOrdersByCondition(
            @Param("query") OrderQueryRequest query,
            @Param("userId") Long userId);

    /**
     * 查询可接取的订单
     *
     * @return 订单列表
     */
    List<Order> selectAvailableOrders();

    /**
     * 查询用户发布的订单数量
     *
     * @param userId 用户ID
     * @return 数量
     */
    int countPublishedOrders(Long userId);

    /**
     * 查询用户接取的订单数量
     *
     * @param userId 用户ID
     * @return 数量
     */
    int countAcceptedOrders(Long userId);
}
