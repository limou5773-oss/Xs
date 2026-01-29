package com.sxps.xsps.pojo;

import lombok.Data;
import java.util.Date;

@Data
public class MerchantUser {

    private Long id;
    private Long merchantId;
    private String username;
    private String password;
    private Date createdAt;
    private Date updatedAt;
}
