package com.sxps.xsps.controller;

import com.sxps.xsps.pojo.Result;
import com.sxps.xsps.mapper.MerchantUserMapper;
import com.sxps.xsps.pojo.MerchantUser;
import com.sxps.xsps.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/merchant")
@RequiredArgsConstructor
public class MerchantController {

    private final MerchantUserMapper userMapper;
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    @PostMapping("/login")

    public Result<?> login(@RequestBody Map<String, String> map) {
        String username = map.get("username");
        String password = map.get("password");

        MerchantUser user = userMapper.findByUsername(username);

        if (user == null) {
            return Result.fail("用户名或密码错误");
        }

        String dbPwd = user.getPassword();

        //明文密码自动升级为 BCrypt
        if (!dbPwd.startsWith("$2a$")) {
            if (password.equals(dbPwd)) {
                String encrypted = encoder.encode(password);
                userMapper.updatePassword(user.getId(), encrypted);
                dbPwd = encrypted;
            } else {
                return Result.fail("用户名或密码错误");
            }
        }

        // 已加密，正常比较
        if (!encoder.matches(password, dbPwd)) {
            return Result.fail("用户名或密码错误");
        }

        //生成 Token
        String token = JwtUtil.genAccess(user.getId(), username);
        String refresh = JwtUtil.genRefresh(user.getId(), username);

        Map<String, Object> data = new HashMap<>();
        data.put("token", token);
        data.put("refreshToken", refresh);
        data.put("merchantUserId", user.getId());
        data.put("merchantId", user.getMerchantId());
        data.put("username", user.getUsername());

        return Result.ok(data);
    }

    @PostMapping("/refresh")
    public Result<?> refresh(@RequestBody Map<String, String> map) {
        String refreshToken = map.get("refreshToken");
        if (refreshToken == null) {
            return Result.fail("refreshToken 缺失");
        }

        Map<String, Object> claims = JwtUtil.parse(refreshToken);
        if (claims == null) {
            return Result.fail("refreshToken 无效或已过期");
        }

        Long uid = (Long) claims.get("uid");
        String username = (String) claims.get("username");

        String access = JwtUtil.genAccess(uid, username);
        String newRefresh = JwtUtil.genRefresh(uid, username);

        Map<String, Object> data = new HashMap<>();
        data.put("accessToken", access);
        data.put("refreshToken", newRefresh);

        return Result.ok(data);
    }


    @GetMapping("/dashboard/{id}")
    public Result<?> dashboard(@PathVariable Long id) {
        Map<String, Object> data = new HashMap<>();
        data.put("orderCount", 20);
        data.put("productCount", 12);
        data.put("afterSaleCount", 3);
        data.put("income", 1520.5);

        return Result.ok(data);
    }

}
