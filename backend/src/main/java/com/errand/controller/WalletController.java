package com.errand.controller;

import com.errand.dto.BillQueryRequest;
import com.errand.dto.Result;
import com.errand.dto.WithdrawalRequest;
import com.errand.service.WalletService;
import com.errand.util.JwtUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;

@Api(tags = {"钱包操作"})
@RestController
@RequestMapping("/api/wallet")
public class WalletController {
    @Autowired
    private WalletService walletService;

    @Autowired
    private JwtUtil jwtUtil;

    @GetMapping
    @ApiOperation("获取钱包信息")
    public Result getWalletInfo(@RequestHeader("Authorization") String token) {
        Long userId = jwtUtil.getUserIdFromToken(token);
        return walletService.getWalletInfo(userId);
    }

    @PostMapping("/withdraw")
    @ApiOperation("提现")
    public Result withdraw(
            @RequestHeader("Authorization") String token,
            @RequestBody @Valid WithdrawalRequest request) {
        Long userId = jwtUtil.getUserIdFromToken(token);
        return walletService.withdraw(userId, request);
    }

    @GetMapping("/withdrawals")
    @ApiOperation("获取提现记录")
    public Result getWithdrawalHistory(@RequestHeader("Authorization") String token) {
        Long userId = jwtUtil.getUserIdFromToken(token);
        return walletService.getWithdrawalHistory(userId);
    }

    @GetMapping("/bills")
    @ApiOperation("获取账单列表")
    public Result getBillList(
            @RequestHeader("Authorization") String token,
            @RequestBody @Valid BillQueryRequest request) {
        Long userId = jwtUtil.getUserIdFromToken(token);
        return walletService.getBillList(userId, request);
    }
}
