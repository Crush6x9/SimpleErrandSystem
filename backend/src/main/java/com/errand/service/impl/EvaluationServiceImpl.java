package com.errand.service.impl;

import com.errand.dto.EvaluationRequest;
import com.errand.dto.EvaluationStats;
import com.errand.dto.Result;
import com.errand.entity.Evaluation;
import com.errand.entity.Order;
import com.errand.mapper.EvaluationMapper;
import com.errand.mapper.OrderMapper;
import com.errand.service.EvaluationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class EvaluationServiceImpl implements EvaluationService {
    @Autowired
    private EvaluationMapper evaluationMapper;

    @Autowired
    private OrderMapper orderMapper;

    @Override
    @Transactional
    public Result createEvaluation(Long orderId, Long userId, EvaluationRequest request) {
        try {
            Order order = orderMapper.selectOrderById(orderId);
            if (order == null) {
                return Result.error("订单不存在");
            }

            // 只有委托人可以评价，且订单必须已完成
            if (!order.getClientId().equals(userId)) {
                return Result.error("只有委托人可以评价");
            }
            if (!"2".equals(order.getStatus())) {
                return Result.error("只能评价已完成的订单");
            }

            // 检查是否已评价
            if (evaluationMapper.existsEvaluationByOrderId(orderId)) {
                return Result.error("该订单已评价");
            }

            Evaluation evaluation = new Evaluation();
            evaluation.setOrderId(orderId);
            evaluation.setReview(request.getReview());
            evaluation.setCreateTime(LocalDateTime.now());

            int result = evaluationMapper.insertEvaluation(evaluation);
            if (result > 0) {
                return Result.success("评价成功");
            } else {
                return Result.error("评价失败");
            }
        } catch (Exception e) {
            throw new RuntimeException("评价异常", e);
        }
    }

    @Override
    public Result getEvaluationByOrderId(Long orderId) {
        try {
            Evaluation evaluation = evaluationMapper.selectEvaluationByOrderId(orderId);
            if (evaluation == null) {
                return Result.error("该订单未评价");
            }
            return Result.success("获取评价成功", evaluation);
        } catch (Exception e) {
            return Result.error("获取评价失败");
        }
    }

    @Override
    public Result getHelperEvaluationStats(Long helperId) {
        try {
            // 获取好评数量
            Long goodReviews = evaluationMapper.countEvaluationsByHelperIdAndReview(helperId, "1");
            // 获取差评数量
            Long badReviews = evaluationMapper.countEvaluationsByHelperIdAndReview(helperId, "0");

            EvaluationStats stats = new EvaluationStats(goodReviews, badReviews);

            return Result.success("获取评价统计成功", stats);
        } catch (Exception e) {
            return Result.error("获取评价统计失败");
        }
    }
}
