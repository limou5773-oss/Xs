package com.sxps.xsps.service.impl;

import com.sxps.xsps.mapper.MerchantUserMapper;
import com.sxps.xsps.pojo.MerchantUser;
import com.sxps.xsps.service.MerchantUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MerchantUserServiceImpl implements MerchantUserService {

    @Autowired
    private MerchantUserMapper userMapper;

    @Override
    public MerchantUser findByUsername(String username) {
        return userMapper.findByUsername(username);
    }
}
