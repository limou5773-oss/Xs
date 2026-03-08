package com.sxps.xsps.pojo;

import lombok.Data;
import java.util.Date;

@Data
public class Review {
    private Long id;
    private Long orderId;   // 逻辑关联订单ID
    private Long userId;    // 逻辑关联用户ID
    private String nickname; // 用户昵称
    private String avatar;   // 用户头像
    private String orderNo; // 订单编号
    private Integer rating; // 评分 1-5
    private String content; // 评价内容
    private Date createTime;
}