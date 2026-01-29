package com.sxps.xsps.pojo;

import lombok.Data;

@Data
public class PageParam {

    /** 页码 */
    private Integer page = 1;

    /** 每页数量 */
    private Integer size = 10;

    /** 搜索关键字（订单号 / 备注 / 收货人 / 手机号） */
    private String keyword;

    /** 商品用的分类筛选（订单分页不会用到，可以留着共用） */
    private Long categoryId;

    /** 开始时间（字符串，前端传 yyyy-MM-dd HH:mm:ss） */
    private String startTime;

    /** 结束时间 */
    private String endTime;

    /** 商家 ID（订单分页用） */
    private Long merchantId;

    /** 订单状态（0~5，可空） */
    private Integer status;
}
