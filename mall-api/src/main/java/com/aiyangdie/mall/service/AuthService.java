package com.aiyangdie.mall.service;

import com.aiyangdie.mall.common.BusinessException;
import com.aiyangdie.mall.common.JwtUtil;
import com.aiyangdie.mall.dto.LoginRequest;
import com.aiyangdie.mall.entity.SysUser;
import com.aiyangdie.mall.mapper.SysUserMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuthService {

    private static final String DEMO_CODE = "123456";

    private final SysUserMapper sysUserMapper;
    private final JwtUtil jwtUtil;

    public Map<String, Object> login(LoginRequest req) {
        if (!DEMO_CODE.equals(req.getCode())) {
            throw BusinessException.of(40101, "验证码错误（演示固定 123456）");
        }
        SysUser user = sysUserMapper.selectOne(new LambdaQueryWrapper<SysUser>()
                .eq(SysUser::getMobile, req.getMobile()));
        if (user == null) {
            user = new SysUser();
            user.setMobile(req.getMobile());
            user.setNickname("用户" + req.getMobile().substring(7));
            user.setStatus(1);
            sysUserMapper.insert(user);
        }
        String token = jwtUtil.createToken("USER", user.getId(), user.getNickname());
        Map<String, Object> result = new HashMap<>();
        result.put("token", token);
        result.put("user", user);
        return result;
    }
}
