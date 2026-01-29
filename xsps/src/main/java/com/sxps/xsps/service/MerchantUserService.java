package com.sxps.xsps.service;

import com.sxps.xsps.pojo.MerchantUser;

public interface MerchantUserService {

    MerchantUser findByUsername(String username);
}
