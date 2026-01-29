package com.sxps.xsps.service;

import com.sxps.xsps.pojo.Message;
import com.sxps.xsps.pojo.PageParam;
import com.sxps.xsps.pojo.PageResult;

public interface MessageService {

    // 自动订单状态提醒（同一条消息更新：待支付/已支付/配送中/已完成/已取消）
    void sendOrderAuto(Long orderId, Integer status);

    // 用户下单 → 商家提醒（有需要的话，可以在创建订单时调用）
    void notifyMerchant(Long orderId);

    // 用户下单 → 后台商家提醒（如果暂时不用，也可以保留空实现）
    void notifyMerchantBackend(Long orderId);

    // 分页
    PageResult<Message> page(Long receiverId, PageParam param);

    // 未读数量
    int countUnread(Long receiverId);

    // 标记已读
    boolean markAsRead(Long id);
}
