package com.sxps.xsps.pojo;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class Address {
    private Long id;
    private Long userId;
    private String name;
    private String phone;
    private String region;
    private String detail;
    private Boolean isDefault;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
