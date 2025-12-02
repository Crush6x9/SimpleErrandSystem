package com.errand.mapper;

import com.errand.entity.Evaluation;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

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

    // 统计跑腿员的好评/差评数量
    Long countEvaluationsByHelperIdAndReview(
            @Param("helperId") Long helperId,
            @Param("review") String review);
}
