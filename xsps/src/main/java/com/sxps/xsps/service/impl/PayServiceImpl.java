package com.sxps.xsps.service.impl;

import com.sxps.xsps.mapper.MerchantOrderMapper;
import com.sxps.xsps.pojo.Order;
import com.sxps.xsps.service.PayService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class PayServiceImpl implements PayService {

    private final MerchantOrderMapper orderMapper;

    /** 用户支付（0 -> 1 待接单） */
    @Override
    public boolean payOrder(Long orderId) {
        Order o = orderMapper.selectById(orderId);
        if (o == null || o.getStatus() != 0) return false;

        // 0 → 1：已支付 / 待商家接单
        o.setStatus(1);
        o.setUpdateTime(LocalDateTime.now());
        return orderMapper.updateById(o) > 0;
    }

    /** 用户取消订单（只能 0 和 1） */
    @Override
    public boolean cancelOrder(Long orderId) {
        Order o = orderMapper.selectById(orderId);
        if (o == null) return false;

        if (o.getStatus() > 1) return false;

        o.setStatus(4);
        o.setUpdateTime(LocalDateTime.now());
        return orderMapper.updateById(o) > 0;
    }

    /** 商家接单（1 → 2） */
    @Override
    public boolean takeOrder(Long orderId) {
        Order o = orderMapper.selectById(orderId);
        if (o == null || o.getStatus() != 1) return false;

        o.setStatus(2);
        o.setUpdateTime(LocalDateTime.now());
        return orderMapper.updateById(o) > 0;
    }

    /** 商家完成配送（2 → 3） */
    @Override
    public boolean finishOrder(Long orderId) {
        Order o = orderMapper.selectById(orderId);
        if (o == null || o.getStatus() != 2) return false;

        o.setStatus(3);
        o.setUpdateTime(LocalDateTime.now());
        return orderMapper.updateById(o) > 0;
    }
}

