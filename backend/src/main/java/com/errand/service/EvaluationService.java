package com.errand.service;

import com.errand.dto.EvaluationRequest;
import com.errand.dto.Result;

public interface EvaluationService {
    Result createEvaluation(Long orderId, Long userId, EvaluationRequest request);

    Result getEvaluationByOrderId(Long orderId);

    Result getHelperEvaluationStats(Long helperId);
}
