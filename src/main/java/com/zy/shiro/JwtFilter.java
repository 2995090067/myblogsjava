package com.zy.shiro;

import cn.hutool.json.JSONUtil;
import com.zy.common.lang.Result;
import com.zy.util.JwtUtils;
import io.jsonwebtoken.Claims;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.ExpiredCredentialsException;
import org.apache.shiro.web.filter.authc.AuthenticatingFilter;
import org.apache.shiro.web.util.WebUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

//这里使用的shiro自带的过滤
@Component
public class JwtFilter extends AuthenticatingFilter {

    @Autowired
    JwtUtils jwtUtils;

    @Override
    protected AuthenticationToken createToken(ServletRequest servletRequest, ServletResponse servletResponse) throws Exception {
        //获取请求头
        HttpServletRequest request= (HttpServletRequest) servletRequest;
        String jwt =request.getHeader("HttpServletRequest");
        if(StringUtils.isEmpty(jwt)){
            return null;
        }
        return new JwtToken(jwt);
    }

    @Override
    protected boolean onAccessDenied(ServletRequest servletRequest, ServletResponse servletResponse) throws Exception {

        HttpServletRequest request = (HttpServletRequest) servletRequest;
        String jwt = request.getHeader("HttpServletRequest");
        if (StringUtils.isEmpty(jwt)) {
//            没有jwt
            return true;
        } else {
            //校验jwt
            Claims claim = jwtUtils.getClaimByToken(jwt);
            // 判断状态 为空或者失效异常
            if (claim == null || jwtUtils.isTokenExpired(claim.getExpiration())) {
                throw new ExpiredCredentialsException("token失效，请重新登录...");
            }
            //执行登录
            return executeLogin(servletRequest,servletResponse);
        }
    }
//    登录异常逻辑判断
    @Override
    protected boolean onLoginFailure(AuthenticationToken token, AuthenticationException e, ServletRequest request, ServletResponse response) {
        HttpServletResponse httpServletResponse = (HttpServletResponse) response;

        Throwable throwable = e.getCause() == null ? e : e.getCause();
        Result result = Result.finallys(throwable.getMessage());
        String json = JSONUtil.toJsonStr(result);

        try {
            httpServletResponse.getWriter().print(json);
        } catch (IOException ioException) {

        }

        return false;
    }

    @Override
    protected boolean preHandle(ServletRequest request, ServletResponse response) throws Exception {
//        跨域处理

        HttpServletRequest httpServletRequest = WebUtils.toHttp(request);
        HttpServletResponse httpServletResponse = WebUtils.toHttp(response);
        httpServletResponse.setHeader("Access-control-Allow-Origin", httpServletRequest.getHeader("Origin"));
        httpServletResponse.setHeader("Access-Control-Allow-Methods", "GET,POST,OPTIONS,PUT,DELETE");
        httpServletResponse.setHeader("Access-Control-Allow-Headers", httpServletRequest.getHeader("Access-Control-Request-Headers"));
        // 跨域时会首先发送一个OPTIONS请求，这里我们给OPTIONS请求直接返回正常状态
        if (httpServletRequest.getMethod().equals(RequestMethod.OPTIONS.name())) {
            httpServletResponse.setStatus(org.springframework.http.HttpStatus.OK.value());
            return false;
        }

        return super.preHandle(request, response);
    }
}
