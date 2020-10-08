package com.zy.controller;


import com.zy.common.lang.Result;
import com.zy.entity.User;
import com.zy.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author 关注github：2995090067@qq.com
 * @since 2020-06-16
 */
@RestController
@RequestMapping("/user")//接口路径
public class UserController {

    @Autowired//自动装配
    UserService userService;

    @GetMapping("/index")//get请求，路径为index
    public Result index(){
        User user=userService.getById(1l);
        return Result.success(user);
    }

    @PostMapping("/eus")//测试实体校检@validated
    public Result examineUserSave(@Validated @RequestBody User user){
        return Result.success(user);
    }


}
