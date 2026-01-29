package com.sxps.xsps.service;

import com.sxps.xsps.pojo.Cart;
import java.util.List;

public interface CartService {

    List<Cart> listByUserId(Long userId);

    boolean addCart(Cart cart);

    boolean removeById(Long id);

    boolean updateQuantity(Long id, Integer quantity);

    boolean updateChecked(Long id, Integer checked);

    boolean clearByUserId(Long userId);
}
