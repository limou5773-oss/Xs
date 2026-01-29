package com.sxps.xsps.task;

import com.sxps.xsps.service.MerchantOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@EnableScheduling
@RequiredArgsConstructor
public class OrderTask {

    private final MerchantOrderService orderService;

    /** 每 30 秒自动检查一次未支付订单 */
    @Scheduled(fixedRate = 30 * 1000)
    public void autoCancel() {
        orderService.autoCancelUnpaidOrders();
    }

    /** 每小时检查未发货订单自动退款 */
    @Scheduled(fixedRate = 60 * 60 * 1000)
    public void autoRefund() {
        orderService.autoRefundNoShipOrders();
    }
}
