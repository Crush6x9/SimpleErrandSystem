package com.errand.mapper;

import com.errand.entity.Evaluation;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface EvaluationMapper {
    /**
     * 新增评价
     *
     * @param evaluation 新评价
     * @return 结果
     */
    int insertEvaluation(Evaluation evaluation);

    /**
     * 根据订单ID查询评价
     *
     * @param orderId 订单ID
     * @return 评价信息
     */
    Evaluation selectEvaluationByOrderId(Long orderId);

    /**
     * 检查订单是否已评价
     *
     * @param orderId 订单ID
     * @return 评价状态
     */
    boolean existsEvaluationByOrderId(Long orderId);
}
