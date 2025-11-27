package com.errand.mapper;

import com.errand.dto.BillInfo;
import com.errand.dto.OrderQueryRequest;
import com.errand.entity.Order;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
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
            @Param("userId") Long userId
    );

    /**
     * 统计所有订单数量
     *
     * @return 订单数量
     */
    Long countAllOrders();

    /**
     * 统计未接取订单数量
     *
     * @return 订单数量
     */
    Long countAvailableOrders();

    /**
     * 统计用户发布的订单数量
     *
     * @param userId 用户ID
     * @return 订单数量
     */
    Long countPublishedOrders(@Param("userId") Long userId);

    /**
     * 统计用户接取的订单数量
     *
     * @param userId 用户ID
     * @return 订单数量
     */
    Long countAcceptedOrders(@Param("userId") Long userId);

    /**
     * 根据条件查询订单数量
     *
     * @param query 查询请求
     * @param userId 用户ID
     * @return 订单数量
     */
    int countOrdersByCondition(
            @Param("query") OrderQueryRequest query,
            @Param("userId") Long userId
    );

    /**
     * 查询用户今日收益
     *
     * @param userId 用户ID
     * @return 今日收益
     */
    BigDecimal selectTodayIncome(@Param("userId") Long userId);

    /**
     * 查询用户账单列表
     *
     * @param userId 用户ID
     * @param offset 偏移量
     * @param size 每页大小
     * @return 账单列表
     */
    List<BillInfo> selectBillListByUserId(
            @Param("userId") Long userId,
            @Param("offset") int offset,
            @Param("size") int size
    );

    /**
     * 统计用户账单数量
     *
     * @param userId 用户ID
     * @return 账单数量
     */
    Long countBillListByUserId(@Param("userId") Long userId);
}
