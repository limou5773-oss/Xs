package com.sxps.xsps.service;

import com.sxps.xsps.pojo.Address;
import java.util.List;

public interface AddressService {
    List<Address> listByUserId(Long userId);
    Address getById(Long id);
    boolean save(Address address);
    boolean update(Address address);
    boolean delete(Long id);
    Address getDefaultAddress(Long userId);
}
