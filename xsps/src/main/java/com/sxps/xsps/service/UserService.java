package com.sxps.xsps.service;

import com.sxps.xsps.pojo.User;

public interface UserService {

    User getByOpenId(String openid);

    boolean saveOrUpdateUser(User user);

    User login(User user);

    boolean register(User user);

    User getById(Long id);

    boolean updateById(User user);
}
