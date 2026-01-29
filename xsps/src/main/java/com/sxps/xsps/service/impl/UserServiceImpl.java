package com.sxps.xsps.service.impl;

import com.sxps.xsps.mapper.UserMapper;
import com.sxps.xsps.pojo.User;
import com.sxps.xsps.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;

    @Override
    public User getByOpenId(String openid) {
        return userMapper.getByOpenId(openid);
    }

    @Override
    public boolean saveOrUpdateUser(User user) {
        if (user.getId() == null) {
            return userMapper.insertUser(user) > 0;
        } else {
            return userMapper.updateUser(user) > 0;
        }
    }

    @Override
    public User login(User user) {
        return userMapper.getByMobile(user.getMobile());
    }

    @Override
    public boolean register(User user) {
        return userMapper.insertUser(user) > 0;
    }

    @Override
    public User getById(Long id) {
        return userMapper.findById(id);
    }

    @Override
    public boolean updateById(User user) {
        return userMapper.updateById(user) > 0;
    }
}
