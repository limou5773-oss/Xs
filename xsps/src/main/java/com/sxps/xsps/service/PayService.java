package com.sxps.xsps.service;


public interface PayService {

    boolean payOrder(Long orderId);     // 用户支付 → 配送中
    boolean cancelOrder(Long orderId);  // 用户取消
    boolean takeOrder(Long orderId);    // 商家接单（1 → 2）
    boolean finishOrder(Long orderId);  // 商家完成配送（2 → 3）
}
