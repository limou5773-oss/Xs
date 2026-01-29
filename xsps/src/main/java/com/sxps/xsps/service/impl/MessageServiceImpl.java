package com.sxps.xsps.service.impl;

import com.github.pagehelper.PageHelper;
import com.sxps.xsps.mapper.MerchantMessageMapper;
import com.sxps.xsps.mapper.MerchantOrderMapper;
import com.sxps.xsps.pojo.Message;
import com.sxps.xsps.pojo.Order;
import com.sxps.xsps.pojo.PageParam;
import com.sxps.xsps.pojo.PageResult;
import com.sxps.xsps.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MessageServiceImpl implements MessageService {

    private final MerchantMessageMapper messageMapper;
    private final MerchantOrderMapper orderMapper;

    /**
     * 自动订单提醒（同一条消息随状态变化）
     * status：0待支付 1已支付 2配送中 3已完成 4已取消
     */
    @Override
    public void sendOrderAuto(Long orderId, Integer status) {

        Order order = orderMapper.selectById(orderId);
        if (order == null) return;

        Long receiverId = order.getMerchantId();   // 商家后台用户 ID（按你项目实际调整）
        String orderNo = order.getOrderNo();

        String content;
        switch (status) {
            case 0 -> content = "订单待支付，订单号：" + orderNo;
            case 1 -> content = "用户已支付订单，订单号：" + orderNo;
            case 2 -> content = "订单正在配送中，订单号：" + orderNo;
            case 3 -> content = "订单已完成，订单号：" + orderNo;
            case 4 -> content = "订单已取消，订单号：" + orderNo;
            default -> content = "订单状态更新，订单号：" + orderNo;
        }

        // 查是否已存在该订单的消息
        Message msg = messageMapper.findByOrder(receiverId, orderId);

        if (msg == null) {
            // 首次：插入
            messageMapper.insertOrderMessage(receiverId, orderId, status, content);
        } else {
            // 已有：更新
            messageMapper.updateOrderMessage(msg.getId(), status, content);
        }
    }

    /**
     * 用户下单 → 商家提醒
     * 一般可以在创建订单时调用，给商家一条“您有新的订单”的消息
     */
    @Override
    public void notifyMerchant(Long orderId) {
        Order order = orderMapper.selectById(orderId);
        if (order == null) return;

        Long receiverId = order.getMerchantId();
        String orderNo = order.getOrderNo();

        String content = "您有新的订单，订单号：" + orderNo + "，请及时处理。";

        // 这里也保持“一单一条消息”的原则
        Message msg = messageMapper.findByOrder(receiverId, orderId);
        if (msg == null) {
            // 默认给一个状态，比如 1：已支付 / 待处理
            messageMapper.insertOrderMessage(receiverId, orderId, 1, content);
        } else {
            // 只更新内容，不动状态（或者按需调整）
            messageMapper.updateOrderMessage(msg.getId(), msg.getStatus(), content);
        }
    }

    /**
     * 用户下单 → 后台商家提醒
     * 如果你有多后台账号（merchant_user），可以在这里查出所有后台账号循环插入。
     * 目前先留一个简单实现，避免接口报错。
     */
    @Override
    public void notifyMerchantBackend(Long orderId) {
        // 如果暂时用不到，可以保持空实现，保证编译通过
        // 之后如果需要，可以和 notifyMerchant 类似实现一套逻辑。
    }

    /** 分页 */
    @Override
    public PageResult<Message> page(Long receiverId, PageParam param) {
        PageHelper.startPage(param.getPage(), param.getSize());
        List<Message> list = messageMapper.page(receiverId, param);
        return new PageResult<>(list);
    }

    @Override
    public int countUnread(Long receiverId) {
        return messageMapper.countUnread(receiverId);
    }

    @Override
    public boolean markAsRead(Long id) {
        return messageMapper.markAsRead(id) > 0;
    }
}
