package com.sxps.xsps.controller;

import com.sxps.xsps.pojo.PageParam;
import com.sxps.xsps.pojo.PageResult;
import com.sxps.xsps.pojo.Result;
import com.sxps.xsps.pojo.Order;
import com.sxps.xsps.service.MerchantOrderService;
import com.sxps.xsps.service.PayService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/merchant")
@RequiredArgsConstructor
public class MerchantOrderController {

    private final MerchantOrderService orderService;
    private final PayService payService;

    /** 老接口：不分页 单纯获取其他数据*/
    @GetMapping("/orders")
    public Result<?> orders(
            @RequestParam Long merchantId,
            @RequestParam(required = false) Integer status
    ) {
        return Result.ok(orderService.listForMerchant(merchantId, status));
    }

    /** 接单（1 → 2） */
    @PostMapping("/take/{id}")
    public Result<?> take(@PathVariable Long id) {
        return payService.takeOrder(id)
                ? Result.ok("接单成功，订单进入配送中")
                : Result.fail("接单失败");
    }

    /** 完成配送（2 → 3） */
    @PostMapping("/finish/{id}")
    public Result<?> finish(@PathVariable Long id) {
        return payService.finishOrder(id)
                ? Result.ok("配送完成")
                : Result.fail("操作失败");
    }

    /** 分页接口（商家 ID 默认 = 1） */
    @PostMapping("/order/page")
    public Result<PageResult<Order>> page(@RequestBody PageParam param) {

        Long merchantId = 1L; // 默认商家ID

        return Result.ok(orderService.page(param, merchantId));
    }
}
