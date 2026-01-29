package com.sxps.xsps.controller;

import com.sxps.xsps.pojo.Result;
import com.sxps.xsps.pojo.User;
import com.sxps.xsps.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /**
     * 更新用户资料
     */
    @PostMapping("/update")
    public Result<User> updateUser(@RequestBody User user) {
        if (user.getId() == null) {
            return Result.fail("用户ID不能为空");
        }

        user.setUpdateTime(LocalDateTime.now());

        boolean ok = userService.updateById(user);
        if (!ok) return Result.fail("更新失败");

        User updated = userService.getById(user.getId());
        return Result.ok(updated);
    }

    /**
     * 根据ID查询用户
     */
    @GetMapping("/{id}")
    public Result<User> getUser(@PathVariable Long id) {
        User user = userService.getById(id);
        return user != null ? Result.ok(user) : Result.fail("用户不存在");
    }
}
