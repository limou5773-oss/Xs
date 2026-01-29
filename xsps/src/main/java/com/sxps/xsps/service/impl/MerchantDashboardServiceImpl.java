package com.sxps.xsps.service.impl;

import com.sxps.xsps.mapper.DashboardMapper;
import com.sxps.xsps.service.MerchantDashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
public class MerchantDashboardServiceImpl implements MerchantDashboardService {

    private final DashboardMapper dashboardMapper;

    // 你可以在接口里定义成 summary(Long merchantId)
    @Override
    public Map<String, Object> summary(Long merchantId) {

        Map<String, Object> map = new HashMap<>();

        // 今日订单数
        Integer todayOrders = dashboardMapper.countTodayOrders(merchantId);
        map.put("todayOrders", todayOrders == null ? 0 : todayOrders);

        // 今日销售额
        BigDecimal todaySales = dashboardMapper.sumTodaySales(merchantId);
        map.put("todaySales", todaySales == null ? BigDecimal.ZERO : todaySales);

        // 待处理售后
        Integer pendingAfterSale = dashboardMapper.countPendingAfterSales(merchantId);
        map.put("pendingAfterSale", pendingAfterSale == null ? 0 : pendingAfterSale);

        // 上架商品数
        Integer onSaleProduct = dashboardMapper.countOnShelfProducts(merchantId);
        map.put("onSaleProduct", onSaleProduct == null ? 0 : onSaleProduct);

        // ========================
        // 最近 7 天订单趋势（关键部分）
        // ========================

        // 1. 先查出数据库里“有订单的那些天”
        List<Map<String, Object>> dbList =
                dashboardMapper.countLast7DaysOrders(merchantId);
        // dbList 形如：[{date=2025-11-27, count=3}, {date=2025-11-30, count=5}...]

        // 2. 先转成 Map<日期字符串, 数量>，方便后面补 0
        Map<String, Integer> dbCountMap = new HashMap<>();
        for (Map<String, Object> row : dbList) {
            String dateStr = String.valueOf(row.get("date"));
            Number c = (Number) row.get("count");
            dbCountMap.put(dateStr, c == null ? 0 : c.intValue());
        }

        // 3. 生成最近 7 天的日期（包括今天），没有数据的补 0
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate today = LocalDate.now();

        List<Map<String, Object>> recentOrders = new ArrayList<>();

        // 从 6 天前 到 今天，共 7 天，保持顺序
        IntStream.rangeClosed(0, 6).forEach(i -> {
            LocalDate day = today.minusDays(6 - i);   // 6,5,4,...,0
            String d = day.format(fmt);
            Integer cnt = dbCountMap.getOrDefault(d, 0);

            Map<String, Object> item = new HashMap<>();
            item.put("date", d);
            item.put("count", cnt);
            recentOrders.add(item);
        });

        map.put("recentOrders", recentOrders);

        return map;
    }
}
