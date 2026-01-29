package com.sxps.xsps.controller;

import com.sxps.xsps.pojo.AfterSale;
import com.sxps.xsps.pojo.Result;
import com.sxps.xsps.service.MerchantAfterSaleService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/afterSale")
@RequiredArgsConstructor
public class UserAfterSaleController {

    private final MerchantAfterSaleService afterSaleService;

    /**
     * 用户提交售后申请
     */
    @PostMapping("/apply")
    public Result<?> apply(@RequestBody AfterSale afterSale) {
        if (afterSale.getUserId() == null) {
            return Result.fail("用户ID不能为空");
        }
        if (afterSale.getOrderId() == null) {
            return Result.fail("订单ID不能为空");
        }

        afterSale.setId(null);
        afterSale.setStatus(0);
        afterSale.setCreateTime(LocalDateTime.now());
        afterSale.setHandleTime(null);

        if (afterSale.getMerchantId() == null) {
            afterSale.setMerchantId(1L);
        }

        boolean ok = afterSaleService.apply(afterSale);
        return ok ? Result.ok("售后申请已提交，等待商家处理")
                : Result.fail("提交失败，请稍后重试");
    }

    /**
     * 用户取消售后申请
     */
    @PostMapping("/cancel")
    public Result<?> cancel(@RequestBody AfterSale afterSale) {
        if (afterSale.getOrderId() == null || afterSale.getUserId() == null) {
            return Result.fail("参数错误");
        }

        boolean ok = afterSaleService.cancel(
                afterSale.getOrderId(),
                afterSale.getUserId()
        );
        return ok ? Result.ok("已取消售后申请")
                : Result.fail("取消失败");


    }
    @GetMapping("/detail/{orderId}")
    public Result<?> detail(@PathVariable Long orderId) {
        AfterSale as = afterSaleService.getByOrderId(orderId);
        return Result.ok(as);   // 没有的话 data 就是 null
    }

}
