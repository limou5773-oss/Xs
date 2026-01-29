package com.sxps.xsps.service;

import com.sxps.xsps.pojo.Order;
import com.sxps.xsps.pojo.PageParam;
import com.sxps.xsps.pojo.PageResult;

import java.util.List;

public interface MerchantOrderService {

    /** 创建订单（包含 order_items） */
    boolean createOrder(Order order);

    /** 用户订单列表（包含子表 items） */
    List<Order> listByUserId(Long userId);

    /** 更新订单状态（自动消息提醒） */
    boolean updateStatus(Long id, Integer status);

    /** 商家订单列表（包含子表 items） */
    List<Order> listForMerchant(Long merchantId, Integer status);

    /** 自动取消未支付订单（30分钟） */
    void autoCancelUnpaidOrders();

    /** 自动退款未配送订单（24小时未发货） */
    void autoRefundNoShipOrders();

    /** 订单详情（主表 + items 子表） */
    Order getDetail(Long orderId);
    //分页
    PageResult<Order> page(PageParam param, Long merchantId);
}
