package com.sxps.xsps.controller;

import com.sxps.xsps.pojo.*;
import com.sxps.xsps.service.MerchantAfterSaleLogService;
import com.sxps.xsps.service.MerchantAfterSaleService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/merchant/afterSale")//商家端售后申请接口
@RequiredArgsConstructor
public class MerchantAfterSaleController {

    private final MerchantAfterSaleService afterSaleService;
    private final MerchantAfterSaleLogService afterSaleLogService;

    @GetMapping("/list")
    public Result<List<AfterSale>> list(
            @RequestParam Long merchantId,
            @RequestParam(required = false) Integer status) {

        return Result.ok(afterSaleService.list(merchantId, status));
    }

    @GetMapping("/page")
    public Result<PageResult<AfterSale>> page(PageParam param) {
        return Result.ok(afterSaleService.page(param));
    }

    @PostMapping("/approve/{id}")
    public Result<?> approve(@PathVariable Long id) {
        return afterSaleService.handle(id, 1)
                ? Result.ok("已同意退款")
                : Result.fail("操作失败");
    }

    @PostMapping("/reject/{id}")
    public Result<?> reject(@PathVariable Long id) {
        return afterSaleService.handle(id, 2)
                ? Result.ok("已拒绝退款")
                : Result.fail("操作失败");
    }

    @GetMapping("/logs/{afterSaleId}")
    public Result<List<AfterSaleLog>> logs(@PathVariable Long afterSaleId) {
        return Result.ok(afterSaleLogService.listByAfterSaleId(afterSaleId));
    }
}
