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

@Api(tags = {"评价操作"})
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
        Long userId = jwtUtil.getUserIdFromToken(token);
        return evaluationService.createEvaluation(orderId, userId, request);
    }

    @GetMapping("/{orderId}")
    @ApiOperation("获取订单评价")
    public Result getEvaluation(@PathVariable Long orderId) {
        return evaluationService.getEvaluationByOrderId(orderId);
    }

    @GetMapping("/stats")
    @ApiOperation("获取跑腿员的评价统计")
    public Result getHelperEvaluationStats(@RequestHeader("Authorization") String token) {
        Long userId = jwtUtil.getUserIdFromToken(token);
        return evaluationService.getHelperEvaluationStats(userId);
    }
}
