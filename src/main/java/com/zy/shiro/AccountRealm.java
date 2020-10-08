package com.zy.shiro;

import cn.hutool.core.bean.BeanUtil;
import com.zy.entity.User;
import com.zy.service.UserService;
import com.zy.util.JwtUtils;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component //注入bean中
public class AccountRealm extends AuthorizingRealm {

    @Autowired
    UserService userService;

    @Autowired
    JwtUtils jwtUtils;

    @Override
    public boolean supports(AuthenticationToken authenticationToken){
        //判断是否支持我们自定义的JwtToken
        return authenticationToken instanceof JwtToken;
    }

    //获取权限的信息
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
        return null;
    }

    //进行密码之类的校验
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {
        JwtToken jwtToken= (JwtToken) authenticationToken;
        String userId = jwtUtils.getClaimByToken((String) jwtToken.getPrincipal()).getSubject();

        User user = userService.getById(Long.valueOf(userId));
        if (user == null) {
            throw new UnknownAccountException("账户不存在");
        }

        if (user.getStatus() == -1) {
            throw new LockedAccountException("账户已被锁定");
        }

        AccountProfile profile = new AccountProfile();
        BeanUtil.copyProperties(user, profile);
        //把用户的基本信息返回给shiro
        return new SimpleAuthenticationInfo(profile, jwtToken.getCredentials(), getName());
    }
}
