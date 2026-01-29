package com.sxps.xsps.controller;

import com.sxps.xsps.pojo.Result;
import com.sxps.xsps.pojo.Order;
import com.sxps.xsps.service.MerchantOrderService;
import com.sxps.xsps.service.PayService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/order")
@RequiredArgsConstructor
public class OrderController {

    private final MerchantOrderService orderService;
    private final PayService payService;

    @PostMapping("/create")
    public Result<?> create(@RequestBody Order order) {

        boolean ok = orderService.createOrder(order);
        if (!ok) {
            return Result.fail("下单失败");
        }
        return Result.ok(order.getId());
    }


    @GetMapping("/list/{userId}")
    public Result<List<Order>> list(@PathVariable Long userId) {
        return Result.ok(orderService.listByUserId(userId));
    }

    @GetMapping("/detail/{id}")
    public Result<Order> detail(@PathVariable Long id) {
        return Result.ok(orderService.getDetail(id));
    }

    @PostMapping("/pay/{id}")
    public Result<?> pay(@PathVariable Long id) {
        return payService.payOrder(id)
                ? Result.ok("支付成功")
                : Result.fail("支付失败");
    }

    @PostMapping("/cancel/{id}")
    public Result<?> cancel(@PathVariable Long id) {
        return payService.cancelOrder(id)
                ? Result.ok("订单已取消")
                : Result.fail("取消失败");
    }
}
