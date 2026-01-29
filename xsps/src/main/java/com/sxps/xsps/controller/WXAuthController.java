package com.sxps.xsps.controller;

import com.sxps.xsps.pojo.Result;
import com.sxps.xsps.pojo.User;
import com.sxps.xsps.service.UserService;
import com.sxps.xsps.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class WXAuthController {

    private final UserService userService;

    @Value("${wx.appid}")
    private String appId;

    @Value("${wx.secret}")
    private String secret;

    @PostMapping("/wxLogin")
    public Result<Map<String, Object>> wxLogin(@RequestBody Map<String, String> body) {

        String code = body.get("code");
        if (code == null) return Result.fail("code不能为空");

        // 1. 调微信接口换 openid
        String url = "https://api.weixin.qq.com/sns/jscode2session?appid=" +
                appId + "&secret=" + secret + "&js_code=" + code + "&grant_type=authorization_code";

        RestTemplate restTemplate = new RestTemplate();
        String result = restTemplate.getForObject(url, String.class);
        JSONObject json = new JSONObject(result);

        if (!json.has("openid")) return Result.fail("登录失败: " + result);
        String openid = json.getString("openid");

        // 2. 查库：是不是老用户？
        User user = userService.getByOpenId(openid);

        if (user == null) {
            // 新用户：自动注册
            user = new User();
            user.setOpenid(openid);
            user.setNickname(body.getOrDefault("nickname", "微信用户"));
            user.setAvatar(body.getOrDefault("avatar", ""));
            userService.saveOrUpdateUser(user); // 必须保存到数据库，生成自增 ID
        }

        // 3.  重点：使用数据库生成的真实 ID 生成 Token
        String accessToken = JwtUtil.genAccess(user.getId(), user.getNickname());

        // 4. 返回给前端
        Map<String, Object> data = new HashMap<>();
        data.put("token", accessToken);
        data.put("user", user); // user.id 必须有值

        return Result.ok(data);
    }
}