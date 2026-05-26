package com.aiyangdie.mall.service;

import com.aiyangdie.mall.common.BusinessException;
import com.aiyangdie.mall.common.JwtUtil;
import com.aiyangdie.mall.dto.AdminLoginRequest;
import com.aiyangdie.mall.entity.AdminUser;
import com.aiyangdie.mall.mapper.AdminUserMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AdminAuthService {

    private final AdminUserMapper adminUserMapper;
    private final JwtUtil jwtUtil;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public Map<String, Object> login(AdminLoginRequest req) {
        AdminUser admin = adminUserMapper.selectOne(new LambdaQueryWrapper<AdminUser>()
                .eq(AdminUser::getUsername, req.getUsername()));
        if (admin == null || admin.getStatus() != 1
                || !passwordEncoder.matches(req.getPassword(), admin.getPassword())) {
            throw BusinessException.of(40102, "用户名或密码错误");
        }
        String token = jwtUtil.createToken("ADMIN", admin.getId(), admin.getNickname());
        Map<String, Object> result = new HashMap<>();
        result.put("token", token);
        result.put("admin", Map.of("id", admin.getId(), "username", admin.getUsername(), "nickname", admin.getNickname()));
        return result;
    }
}
