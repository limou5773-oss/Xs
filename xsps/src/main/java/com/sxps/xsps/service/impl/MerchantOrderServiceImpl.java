package com.sxps.xsps.service.impl;

import com.github.pagehelper.PageHelper;
import com.sxps.xsps.mapper.CartMapper;
import com.sxps.xsps.mapper.ProductMapper;
import com.sxps.xsps.mapper.MerchantOrderItemMapper;
import com.sxps.xsps.mapper.MerchantOrderMapper;
import com.sxps.xsps.pojo.Order;
import com.sxps.xsps.pojo.OrderItem;
import com.sxps.xsps.pojo.PageParam;
import com.sxps.xsps.pojo.PageResult;
import com.sxps.xsps.service.MessageService;
import com.sxps.xsps.service.MerchantOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MerchantOrderServiceImpl implements MerchantOrderService {

    private final MerchantOrderMapper orderMapper;
    private final MerchantOrderItemMapper orderItemMapper;
    private final MessageService messageService;
    private final CartMapper cartMapper;
    private final ProductMapper productMapper;

    /** =========================================================
     * ① 创建订单（创建即触发商家和后台商家提醒）
     * ========================================================= */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean createOrder(Order order) {

        if (order.getItems() == null || order.getItems().isEmpty()) {
            return false;
        }

        order.setOrderNo("O" + System.currentTimeMillis());
        order.setStatus(0); // 待支付
        order.setCreateTime(LocalDateTime.now());
        order.setUpdateTime(LocalDateTime.now());

        BigDecimal total = BigDecimal.ZERO;
        for (OrderItem item : order.getItems()) {
            if (item.getPrice() == null || item.getQuantity() == null) {
                return false;
            }

            // --- 扣减库存 ---
            int rows = productMapper.deductStock(item.getProductId(), item.getQuantity());
            if (rows <= 0) {
                throw new RuntimeException("商品库存不足，下单失败！商品ID: " + item.getProductId());
            }

            total = total.add(item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())));
        }
        order.setTotalPrice(total);
        order.setPayPrice(total);

        if (orderMapper.insert(order) <= 0) return false;

        for (OrderItem item : order.getItems()) {
            item.setOrderId(order.getId());
            orderItemMapper.insert(item);
            // --- 清空购物车  ---
            cartMapper.deleteCartItem(order.getUserId(), item.getProductId());
        }

        try {
            messageService.notifyMerchant(order.getId());
            messageService.notifyMerchantBackend(order.getId());
        } catch (Exception e) {
            e.printStackTrace();
        }

        return true;
    }

    /** =========================================================
     * ② 用户订单列表（带 items）
     * ========================================================= */
    @Override
    public List<Order> listByUserId(Long userId) {
        List<Order> list = orderMapper.listByUserId(userId);
        for (Order o : list) {
            o.setItems(orderItemMapper.listByOrderId(o.getId()));
        }
        return list;
    }

    /** =========================================================
     * ③ 商家订单列表（带 items） - 老接口
     * ========================================================= */
    @Override
    public List<Order> listForMerchant(Long merchantId, Integer status) {
        List<Order> list = orderMapper.listByMerchant(merchantId, status);
        for (Order o : list) {
            o.setItems(orderItemMapper.listByOrderId(o.getId()));
        }
        return list;
    }

    /** =========================================================
     * ④ 更新订单状态 → 自动推送用户消息
     * ========================================================= */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateStatus(Long id, Integer status) {

        Order order = orderMapper.selectById(id);
        if (order == null) return false;

        // ⭐ 新增逻辑：如果是从“待支付/已支付”变为“已取消”，必须把库存还回去
        if ((order.getStatus() == 0 || order.getStatus() == 1) && status == 4) {
            restoreStock(id); // 调用下方的辅助方法
        }

        order.setStatus(status);
        order.setUpdateTime(LocalDateTime.now());

        boolean ok = orderMapper.updateById(order) > 0;

        if (ok) {
            try {
                messageService.sendOrderAuto(id, status);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return ok;
    }

    /** =========================================================
     * ⑤ 自动取消未支付订单（3 分钟未支付）
     * ========================================================= */
    @Override
    @Transactional(rollbackFor = Exception.class) // ⭐ 加上事务注解比较安全
    public void autoCancelUnpaidOrders() {

        List<Order> orders = orderMapper.listByStatus(0); // 0=未支付
        LocalDateTime now = LocalDateTime.now();

        for (Order o : orders) {

            long minutes = Duration.between(o.getCreateTime(), now).toMinutes();

            // 3 分钟自动取消
            if (minutes >= 3) {
                o.setStatus(4); // 已取消
                o.setUpdateTime(now);
                orderMapper.updateById(o);

                // ⭐ 新增逻辑：自动取消时，也必须把库存还回去
                restoreStock(o.getId());

                try {
                    messageService.sendOrderAuto(o.getId(), 4);
                } catch (Exception e) {}
            }
        }
    }

    /** =========================================================
     * ⭐ 辅助方法：还库存 (私有方法，不影响接口)
     * ========================================================= */
    private void restoreStock(Long orderId) {
        List<OrderItem> items = orderItemMapper.listByOrderId(orderId);
        if (items != null) {
            for (OrderItem item : items) {
                // 调用 Mapper 把库存加回去
                productMapper.incrementStock(item.getProductId(), item.getQuantity());
            }
        }
    }

    /** =========================================================
     * ⑥ 自动退款（24小时未发货）
     * ========================================================= */
    @Override
    public void autoRefundNoShipOrders() {
        List<Order> orders = orderMapper.listByStatus(1);
        LocalDateTime now = LocalDateTime.now();

        for (Order o : orders) {
            if (Duration.between(o.getUpdateTime(), now).toHours() >= 24) {
                o.setStatus(5);
                o.setUpdateTime(now);
                orderMapper.updateById(o);

                try {
                    messageService.sendOrderAuto(o.getId(), 5);
                } catch (Exception e) {}
            }
        }
    }

    /** =========================================================
     * ⑦ 获取订单详情（主表 + 子表）
     * ========================================================= */
    @Override
    public Order getDetail(Long orderId) {
        Order order = orderMapper.selectById(orderId);
        if (order != null) {
            order.setItems(orderItemMapper.listByOrderId(orderId));
        }
        return order;
    }

    /** =========================================================
     * ⑧ 分页（PageHelper + PageParam + XML）
     * ========================================================= */
    @Override
    public PageResult<Order> page(PageParam param, Long merchantId) {
        param.setMerchantId(merchantId);
        PageHelper.startPage(param.getPage(), param.getSize());
        List<Order> list = orderMapper.pageList(param);
        for (Order o : list) {
            o.setItems(orderItemMapper.listByOrderId(o.getId()));
        }
        return new PageResult<>(list);
    }
}