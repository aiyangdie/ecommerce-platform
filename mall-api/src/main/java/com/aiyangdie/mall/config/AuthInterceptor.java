package com.aiyangdie.mall.config;

import com.aiyangdie.mall.common.AuthContext;
import com.aiyangdie.mall.common.BusinessException;
import com.aiyangdie.mall.common.JwtUtil;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
@RequiredArgsConstructor
public class AuthInterceptor implements HandlerInterceptor {

    private final JwtUtil jwtUtil;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }
        String auth = request.getHeader("Authorization");
        if (auth == null || !auth.startsWith("Bearer ")) {
            throw BusinessException.of(40100, "未登录");
        }
        Claims claims = jwtUtil.parse(auth.substring(7));
        String role = claims.get("role", String.class);
        String path = request.getRequestURI();
        if (path.startsWith("/api/v1/admin") && !"ADMIN".equals(role)) {
            throw BusinessException.of(40300, "无管理员权限");
        }
        if ((path.startsWith("/api/v1/cart") || path.startsWith("/api/v1/orders"))
                && !"USER".equals(role)) {
            throw BusinessException.of(40300, "请使用用户账号登录");
        }
        AuthContext.set(new AuthContext.LoginUser(
                Long.parseLong(claims.getSubject()),
                role,
                claims.get("name", String.class)
        ));
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        AuthContext.clear();
    }
}
