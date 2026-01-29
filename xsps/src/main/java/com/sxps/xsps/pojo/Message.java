package com.sxps.xsps.pojo;

import lombok.Data;

import java.util.Date;

@Data
public class Message {

    private Long id;

    /** 商家后台用户ID */
    private Long receiverId;

    /** 订单ID */
    private Long orderId;

    /** 订单状态：0待支付 1已支付 2配送中 3已完成 4已取消 */
    private Integer status;

    /** 内容 */
    private String content;

    /** 是否阅读：0未读 1已读 */
    private Integer isRead;

    /** 创建时间 */
    private Date createTime;

    /** 阅读时间 */
    private Date readTime;
}
