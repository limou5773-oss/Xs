package com.sxps.xsps.controller;

import com.sxps.xsps.service.PayService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/pay")
@RequiredArgsConstructor
public class PayController {

    private final PayService payService;

    @PostMapping("/doPay/{id}")
    public String pay(@PathVariable Long id) {
        return payService.payOrder(id) ? "支付成功" : "支付失败";
    }

    @PostMapping("/finish/{id}")
    public String finish(@PathVariable Long id) {
        return payService.finishOrder(id) ? "订单已完成配送" : "操作失败";
    }
}
