package com.sxps.xsps.pojo;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("product")
public class Product {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long merchantId;
    private Long categoryId;
    private String name;
    private BigDecimal price;
    private Integer stock;
    private String unit;
    private String cover;
    private String description; // 新增商品介绍
    private Integer status;     // 1上架 0下架

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    // 分类名称（非数据库字段）
    @TableField(exist = false)
    private String categoryName;
}
