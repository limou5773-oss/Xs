package com.sxps.xsps.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Mapper
public interface DashboardMapper {

    /** 今日订单数 */
    Integer countTodayOrders(@Param("merchantId") Long merchantId);

    /** 今日销售额 */
    BigDecimal sumTodaySales(@Param("merchantId") Long merchantId);

    /** 待处理售后数量（after_sale.status = 0） */
    Integer countPendingAfterSales(@Param("merchantId") Long merchantId);

    /** 上架商品数量（product.status = 1） */
    Integer countOnShelfProducts(@Param("merchantId") Long merchantId);

    /**
     * 最近 7 天订单趋势
     * 每一条记录形如：{date: '2025-11-30', count: 3}
     */
    List<Map<String, Object>> countLast7DaysOrders(@Param("merchantId") Long merchantId);
}
