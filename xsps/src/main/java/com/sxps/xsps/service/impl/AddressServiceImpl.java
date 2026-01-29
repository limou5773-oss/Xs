package com.sxps.xsps.service.impl;

import com.sxps.xsps.mapper.AddressMapper;
import com.sxps.xsps.pojo.Address;
import com.sxps.xsps.service.AddressService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AddressServiceImpl implements AddressService {

    private final AddressMapper addressMapper;

    @Override
    public List<Address> listByUserId(Long userId) {
        return addressMapper.findByUserId(userId);
    }

    @Override
    public Address getById(Long id) {
        return addressMapper.findById(id);
    }

    @Override
    public boolean save(Address address) {
        // ✅ 如果当前地址设为默认，先清除其他默认地址
        if (Boolean.TRUE.equals(address.getIsDefault())) {
            addressMapper.clearDefault(address.getUserId());
        }
        return addressMapper.insert(address) > 0;
    }

    @Override
    public boolean update(Address address) {
        if (Boolean.TRUE.equals(address.getIsDefault())) {
            addressMapper.clearDefault(address.getUserId());
        }
        return addressMapper.update(address) > 0;
    }

    @Override
    public boolean delete(Long id) {
        return addressMapper.delete(id) > 0;
    }

    // ✅ 新增：获取用户默认收货地址
    @Override
    public Address getDefaultAddress(Long userId) {
        return addressMapper.findDefaultByUserId(userId);
    }
}
