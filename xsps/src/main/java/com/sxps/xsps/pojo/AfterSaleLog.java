package com.sxps.xsps.pojo;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("after_sale_log")
public class AfterSaleLog {

    private Long id;

    /** 对应的售后记录 ID */
    private Long afterSaleId;

    /** 操作内容，如：同意退款、拒绝退款 */
    private String action;

    /** 操作人类型：merchant / user / system */
    private String operator;

    /** 操作时间 */
    private LocalDateTime createTime;
}
