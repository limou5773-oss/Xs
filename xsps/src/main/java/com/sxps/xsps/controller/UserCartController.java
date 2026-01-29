package com.sxps.xsps.controller;

import com.sxps.xsps.pojo.Result;
import com.sxps.xsps.pojo.Cart;
import com.sxps.xsps.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class UserCartController {

    private final CartService cartService;

    /** 获取购物车列表 */
    @GetMapping("/list/{userId}")
    public Result<List<Cart>> list(@PathVariable Long userId) {
        return Result.ok(cartService.listByUserId(userId));
    }

    /** 添加商品到购物车 */
    @PostMapping("/add")
    public Result<?> add(@RequestBody Cart cart) {
        return cartService.addCart(cart)
                ? Result.ok("已加入购物车")
                : Result.fail("添加失败");
    }

    /** 删除购物车商品 */
    @DeleteMapping("/remove/{id}")
    public Result<?> remove(@PathVariable Long id) {
        return cartService.removeById(id)
                ? Result.ok("删除成功")
                : Result.fail("删除失败");
    }

    /** 更新数量 */
    @PostMapping("/update/{id}")
    public Result<?> updateQuantity(@PathVariable Long id, @RequestBody Cart cart) {
        boolean ok = cartService.updateQuantity(id, cart.getQuantity());
        return ok ? Result.ok("数量已更新") : Result.fail("更新失败");
    }

    /** 勾选/取消勾选商品 */
    @PostMapping("/check/{id}")
    public Result<?> checkItem(@PathVariable Long id, @RequestBody Cart cart) {
        boolean ok = cartService.updateChecked(id, cart.getChecked());
        return ok ? Result.ok("选中状态已更新") : Result.fail("更新失败");
    }

    /** 清空购物车 */
    @DeleteMapping("/clear/{userId}")
    public Result<?> clear(@PathVariable Long userId) {
        boolean ok = cartService.clearByUserId(userId);
        return ok ? Result.ok("购物车已清空") : Result.fail("清空失败");
    }
}
