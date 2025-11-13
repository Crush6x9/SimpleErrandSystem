package com.errand.controller;

import com.errand.dto.EvaluationRequest;
import com.errand.dto.Result;
import com.errand.service.EvaluationService;
import com.errand.util.JwtUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Api(tags = {"评价"})
@RestController
@RequestMapping("/api/evaluations")
public class EvaluationController {
    @Autowired
    private EvaluationService evaluationService;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/{orderId}")
    @ApiOperation("订单评价")
    public Result createEvaluation(
            @RequestHeader("Authorization") String token,
            @PathVariable Long orderId,
            @RequestBody @Valid EvaluationRequest request) {
        Long userId = jwtUtil.getUserIdFromToken(token.replace("Bearer ", ""));
        return evaluationService.createEvaluation(orderId, userId, request);
    }

    @GetMapping("/{orderId}")
    @ApiOperation("获取订单评价")
    public Result getEvaluation(@PathVariable Long orderId) {
        return evaluationService.getEvaluationByOrderId(orderId);
    }

    @GetMapping("/helper/{helperId}")
    @ApiOperation("获取跑腿员的评价列表")
    public Result getHelperEvaluations(@PathVariable Long helperId) {
        return evaluationService.getHelperEvaluations(helperId);
    }
}
