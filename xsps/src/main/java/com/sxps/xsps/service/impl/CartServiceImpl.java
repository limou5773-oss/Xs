package com.sxps.xsps.service.impl;

import com.sxps.xsps.mapper.CartMapper;
import com.sxps.xsps.pojo.Cart;
import com.sxps.xsps.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {

    private final CartMapper cartMapper;

    @Override
    public List<Cart> listByUserId(Long userId) {
        return cartMapper.selectByUserId(userId);
    }

    @Override
    public boolean addCart(Cart cart) {
        // 1. 先查该商品是否已在购物车
        Cart exist = cartMapper.selectByUserAndProduct(cart.getUserId(), cart.getProductId());

        if (exist != null) {
            // 2. 如果存在，只更新数量 (原数量 + 新增数量)
            int newQuantity = exist.getQuantity() + (cart.getQuantity() == null ? 1 : cart.getQuantity());
            return cartMapper.updateQuantity(exist.getId(), newQuantity) > 0;
        } else {
            // 3. 如果不存在，执行新增
            cart.setChecked(1); // 默认选中
            if (cart.getQuantity() == null) cart.setQuantity(1);
            return cartMapper.insert(cart) > 0;
        }
    }

    @Override
    public boolean removeById(Long id) {
        return cartMapper.deleteById(id) > 0;
    }

    @Override
    public boolean updateQuantity(Long id, Integer quantity) {
        if (quantity <= 0) {
            // 如果数量改成了0，直接删除
            return cartMapper.deleteById(id) > 0;
        }
        return cartMapper.updateQuantity(id, quantity) > 0;
    }

    @Override
    public boolean updateChecked(Long id, Integer checked) {
        return cartMapper.updateChecked(id, checked) > 0;
    }

    @Override
    public boolean clearByUserId(Long userId) {
        return cartMapper.deleteByUserId(userId) > 0;
    }
}