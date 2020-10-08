package com.zy.config;

import com.zy.shiro.AccountRealm;
import com.zy.shiro.JwtFilter;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.session.mgt.SessionManager;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.spring.web.config.DefaultShiroFilterChainDefinition;
import org.apache.shiro.spring.web.config.ShiroFilterChainDefinition;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.apache.shiro.web.session.mgt.DefaultWebSessionManager;
import org.crazycake.shiro.RedisCacheManager;
import org.crazycake.shiro.RedisSessionDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.servlet.Filter;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

@Configuration
public class ShiroConfig {

    @Autowired
    JwtFilter jwtFilter; //自动装配jwt

    @Bean
    public SessionManager sessionManager(RedisSessionDAO r){
        DefaultWebSessionManager sessionManager=new DefaultWebSessionManager();
        sessionManager.setSessionDAO(r);
        return sessionManager;
    }

    @Bean
    public DefaultWebSecurityManager securityManager(AccountRealm accountRealm,
                                                  SessionManager sessionManager,
                                                  RedisCacheManager redisCacheManager){
        DefaultWebSecurityManager securityManager=new DefaultWebSecurityManager(accountRealm);

        securityManager.setSessionManager(sessionManager);

        securityManager.setCacheManager(redisCacheManager);

        return securityManager;
    }

    //过滤器链接的定义
    @Bean
    public ShiroFilterChainDefinition shiroFilterChainDefinition() {
        DefaultShiroFilterChainDefinition chainDefinition = new DefaultShiroFilterChainDefinition();

        Map<String, String> filterMap = new LinkedHashMap<>();
//这里注意要开启redis缓存
        filterMap.put("/**", "jwt");//这里主要通过注解方式验证
//        filterMap.put("/**", "authc");//自带的需要身份验证
        chainDefinition.addPathDefinitions(filterMap);
        return chainDefinition;
    }

    @Bean("shiroFilterFactoryBean")
    public ShiroFilterFactoryBean shiroFilterFactoryBean(SecurityManager securityManager,
                                                         ShiroFilterChainDefinition shiroFilterChainDefinition) {
        ShiroFilterFactoryBean shiroFilter = new ShiroFilterFactoryBean();
        shiroFilter.setSecurityManager(securityManager);

        Map<String, Filter> filters = new HashMap<>();
        filters.put("jwt", jwtFilter);//这里使用的jwt认证登录权限
        shiroFilter.setFilters(filters);

        Map<String, String> filterMap = shiroFilterChainDefinition.getFilterChainMap();

        shiroFilter.setFilterChainDefinitionMap(filterMap);
        return shiroFilter;
    }


}
