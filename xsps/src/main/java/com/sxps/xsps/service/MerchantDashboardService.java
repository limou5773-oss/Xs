package com.sxps.xsps.service;

import java.util.Map;

public interface MerchantDashboardService {

    /**
     * 仪表盘统计总览
     */
    Map<String, Object> summary(Long merchantId);
}
