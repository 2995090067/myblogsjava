package com.zy.shiro;

import org.apache.shiro.authc.AuthenticationToken;
//自定义token 用于收集用户username以及传来的凭证

public class JwtToken implements AuthenticationToken {

    private String token;

    public JwtToken(String jwt){
        this.token=jwt;
    }

    @Override
    public Object getPrincipal() {
        return token;
    }

    @Override
    public Object getCredentials() {
        return token;
    }
}
