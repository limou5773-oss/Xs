package com.sxps.xsps.pojo;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@TableName("orders")
public class Order {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 订单号（生成规则：时间戳 + 随机） */
    private String orderNo;

    /** 用户 ID */
    private Long userId;

    /** 商家 ID */
    private Long merchantId;

    /** 订单原价 */
    private BigDecimal totalPrice;

    /** 实际支付金额 */
    private BigDecimal payPrice;

    /** 地址快照（JSON：name/phone/address） */
    private String addressSnapshot;

    /** 用户备注 */
    private String remark;

    /**
     * 订单状态：
     * 0 = 待支付
     * 1 = 已支付（已废弃，正常流程不会用）
     * 2 = 配送中（用户支付后自动进入）
     * 3 = 已完成
     * 4 = 已取消
     * 5 = 已退款
     */
    private Integer status;

    /** 创建时间 */
    private LocalDateTime createTime;

    /** 更新时间 */
    private LocalDateTime updateTime;
    private List<OrderItem> items = new ArrayList<>();
}
