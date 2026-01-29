package com.sxps.xsps.controller;

import com.sxps.xsps.pojo.PageParam;
import com.sxps.xsps.pojo.PageResult;
import com.sxps.xsps.pojo.Result;
import com.sxps.xsps.pojo.Category;
import com.sxps.xsps.service.MerchantCategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/category")
@RequiredArgsConstructor
@CrossOrigin
public class MerchantCategoryController {

    private final MerchantCategoryService categoryService;

    /** 分类列表 */
    @GetMapping("/list")
    public Result<List<Category>> list() {
        return Result.ok(categoryService.list());
    }

    /** 新增分类 */
    @PostMapping("/add")
    public Result<?> add(@RequestBody Category category) {
        return categoryService.save(category)
                ? Result.ok("添加成功")
                : Result.fail("添加失败");
    }

    /** 更新分类 */
    @PostMapping("/update")
    public Result<?> update(@RequestBody Category category) {
        return categoryService.update(category)
                ? Result.ok("更新成功")
                : Result.fail("更新失败");
    }

    /** 删除分类 */
    @DeleteMapping("/delete/{id}")
    public Result<?> delete(@PathVariable Long id) {
        return categoryService.removeById(id)
                ? Result.ok("删除成功")
                : Result.fail("删除失败");
    }

    /** 分类分页 */
    @PostMapping("/page")
    public Result<PageResult<Category>> page(@RequestBody PageParam param) {
        return Result.ok(categoryService.page(param));
    }
}
