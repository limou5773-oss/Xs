package com.sxps.xsps.pojo;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@TableName("after_sale")
public class AfterSale {

    private Long id;
    private Long orderId;
    private Long userId;
    private Long merchantId;
    private String reason;
    private Integer status; // 0待处理 1已同意 2已拒绝
    private LocalDateTime createTime;
    private LocalDateTime handleTime;
    //类型
    private Integer type;
    /** ⭐ 订单号（JOIN orders 表得到） */
    private String orderNo;
    /** ⭐ 商品列表（order_item 表） */
    private List<OrderItem> items;
    private BigDecimal refundPrice;
    private String addressSnapshot;

}
