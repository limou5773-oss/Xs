package com.sxps.xsps.controller;

import com.sxps.xsps.pojo.Result;
import com.sxps.xsps.service.MerchantDashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/merchant/dashboard")
@RequiredArgsConstructor
public class MerchantDashboardController {

    private final MerchantDashboardService dashboardService;

    /**
     * 仪表盘统计
     */
    @GetMapping("/summary")
    public Result<?> getSummary(@RequestParam(required = false) Long merchantId) {
        // 先简单写死一个默认值，你以后可以从登录用户里拿
        if (merchantId == null) {
            merchantId = 1L;
        }
        Map<String, Object> data = dashboardService.summary(merchantId);
        return Result.ok(data);
    }
}
