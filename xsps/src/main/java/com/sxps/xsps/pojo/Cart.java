package com.sxps.xsps.pojo;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("cart")
public class Cart {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;
    private Long productId;
    private String productName;
    private BigDecimal price;
    private Integer quantity;
    private String cover;
    private Integer checked; // 1选中 0未选中

    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
