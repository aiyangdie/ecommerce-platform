package com.aiyangdie.mall.common;

public final class AuthContext {

    private static final ThreadLocal<LoginUser> HOLDER = new ThreadLocal<>();

    private AuthContext() {
    }

    public static void set(LoginUser user) {
        HOLDER.set(user);
    }

    public static LoginUser get() {
        return HOLDER.get();
    }

    public static Long userId() {
        LoginUser u = get();
        return u == null ? null : u.getId();
    }

    public static void clear() {
        HOLDER.remove();
    }

    public record LoginUser(Long id, String role, String name) {
    }
}
