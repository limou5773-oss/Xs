package com.sxps.xsps.controller;

import com.sxps.xsps.pojo.Result;
import com.sxps.xsps.pojo.Address;
import com.sxps.xsps.service.AddressService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/address")
@RequiredArgsConstructor
public class UserAddressController {

    private final AddressService addressService;

    /**
     * 根据用户ID查询地址列表
     */
    @GetMapping("/list/{userId}")
    public Result<List<Address>> list(@PathVariable Long userId) {
        return Result.ok(addressService.listByUserId(userId));
    }

    /**
     * 获取单个地址详情
     */
    @GetMapping("/{id}")
    public Result<Address> get(@PathVariable Long id) {
        Address address = addressService.getById(id);
        return address != null ? Result.ok(address) : Result.fail("地址不存在");
    }

    /**
     * 新增地址
     */
    @PostMapping("/add")
    public Result<?> add(@RequestBody Address address) {
        boolean success = addressService.save(address);
        return success ? Result.ok("保存成功") : Result.fail("保存失败");
    }

    /**
     * 修改地址
     */
    @PostMapping("/update")
    public Result<?> update(@RequestBody Address address) {
        boolean success = addressService.update(address);
        return success ? Result.ok("更新成功") : Result.fail("更新失败");
    }

    /**
     * 删除地址
     */
    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Long id) {
        boolean success = addressService.delete(id);
        return success ? Result.ok("删除成功") : Result.fail("删除失败");
    }
}
