package com.sxps.xsps.interceptor;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.sxps.xsps.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class JwtInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest req, HttpServletResponse resp, Object handler) {
        String uri = req.getRequestURI();

        // 白名单：不需要 token 的接口
        if (uri.startsWith("/api/merchant/login") ||
                uri.startsWith("/api/merchant/refresh") ||
                uri.startsWith("/api/auth/wxLogin") ||         //  小程序登录放行
                uri.startsWith("/imgs/") ||
                uri.startsWith("/api/upload")) {

            return true;
        }

        String auth = req.getHeader("Authorization");
        if (auth == null || !auth.startsWith("Bearer ")) {
            resp.setStatus(401);
            return false;
        }

        try {
            DecodedJWT jwt = JwtUtil.verify(auth.substring(7));
            req.setAttribute("merchantUserId", jwt.getClaim("uid").asLong());
            return true;
        } catch (Exception e) {
            resp.setStatus(401);
            return false;
        }
    }
}
