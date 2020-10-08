package com.zy.controller;


import cn.hutool.core.map.MapUtil;
import cn.hutool.crypto.SecureUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.zy.common.dto.LoginDto;
import com.zy.common.lang.Result;
import com.zy.entity.User;
import com.zy.service.UserService;
import com.zy.util.JwtUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;


@RestController
public class AccountController {

    @Autowired
    UserService userService;

    @Autowired
    JwtUtils jwtUtils;

    @PostMapping("/login")
//    检验传来的数据是否正常
    public Result login(@Validated @RequestBody LoginDto loginDto, HttpServletResponse response){
//        查询数据库是否真的存在此用户
        User user=userService.getOne(new QueryWrapper<User>().eq("username",loginDto.getUsername()));
        Assert.notNull(user,"此用户不存在");
        //这里如果单独一个md5加密或导致结果不一致
        if(!SecureUtil.md5(user.getPassword()).equals(SecureUtil.md5(loginDto.getPassword()))){

            return Result.finallys("用户密码不正确");
        }
        String jwt = jwtUtils.generateToken(user.getId());

        response.setHeader("Authorization", jwt);
        response.setHeader("Access-control-Expose-Headers", "Authorization");

        return Result.success(MapUtil.builder()
                .put("id", user.getId())
                .put("username", user.getUsername())
                .put("avatar", user.getAvatar())
                .put("email", user.getEmail())
                .map()
        );
    }
    //退出登录
    @RequiresAuthentication
    @GetMapping("/logout")
    public Result logout() {
        SecurityUtils.getSubject().logout();
        return Result.success(null);
    }

}
