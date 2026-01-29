package com.sxps.xsps.controller;

import com.sxps.xsps.pojo.PageParam;
import com.sxps.xsps.pojo.Product;
import com.sxps.xsps.pojo.Result;
import com.sxps.xsps.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/product")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    /** 分页查询 */
    @PostMapping("/page")
    public Result<?> page(@RequestBody PageParam param) {
        return Result.ok(productService.page(param));
    }

    /** 详情 */
    @GetMapping("/detail/{id}")
    public Result<Product> get(@PathVariable Long id) {
        return Result.ok(productService.get(id));
    }

    /** 添加 */
    @PostMapping("/add")
    public Result<?> add(@RequestBody Product product) {
        product.setMerchantId(1L); // 默认商家ID
        return productService.add(product) ? Result.ok("添加成功") : Result.fail("添加失败");
    }

    /** 更新 */
    @PostMapping("/update")
    public Result<?> update(@RequestBody Product product) {
        return productService.update(product) ? Result.ok("更新成功") : Result.fail("更新失败");
    }

    /** 删除 */
    @DeleteMapping("/delete/{id}")
    public Result<?> delete(@PathVariable Long id) {
        return productService.delete(id) ? Result.ok("删除成功") : Result.fail("删除失败");
    }

    /**  新增：首页随机推荐6个接口  */
    @GetMapping("/recommend")
    public Result<List<Product>> recommend() {
        return Result.ok(productService.getRecommend(6));
    }
}