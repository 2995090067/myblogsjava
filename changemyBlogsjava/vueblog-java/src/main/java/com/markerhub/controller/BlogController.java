package com.markerhub.controller;


import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.markerhub.common.lang.Result;
import com.markerhub.entity.Blog;
import com.markerhub.entity.User;
import com.markerhub.service.BlogService;
import com.markerhub.service.UserService;
import com.markerhub.util.ShiroUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.math.BigInteger;
import java.time.LocalDateTime;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author 关注公众号：MarkerHub
 * @since 2020-05-25
 */
@Slf4j
@RestController
public class BlogController {

    @Autowired
    BlogService blogService;

    @GetMapping("/blogs")
    public Result list(@RequestParam(defaultValue = "1") Integer currentPage) {
//获取blog内容
        Page page = new Page(currentPage, 5);
        IPage pageData = blogService.page(page, new QueryWrapper<Blog>().orderByDesc("created"));

        return Result.succ(pageData);
    }

    @GetMapping("/blog/{id}")
    public Result detail(@PathVariable  (name = "id") Long id) {
        Blog blog = blogService.getById(id);
        Assert.notNull(blog, "该博客已被删除");

        return Result.succ(blog);
    }

    //权限认证，用户是否登录
    @RequiresAuthentication
    @PostMapping("/blog/edit")
    public Result edit(@Validated @RequestBody Blog blog) {
        log.debug("进入方法");
        log.debug("jinru getid==>"+blog.getId());
        Blog temp = null;
        if(blog.getId() != null) {
            temp = blogService.getById(blog.getId());
            // 只能编辑自己的文章
            System.out.println(ShiroUtil.getProfile().getId());
            Assert.isTrue(temp.getUserId().longValue() == ShiroUtil.getProfile().getId().longValue(), "没有权限编辑");

        } else {

            temp = new Blog();
            temp.setUserId(ShiroUtil.getProfile().getId());
            temp.setCreated(LocalDateTime.now());
            temp.setStatus(0);
        }

        BeanUtil.copyProperties(blog, temp, "id", "userId", "created", "status");
        blogService.saveOrUpdate(temp);

        return Result.succ(null);
    }
    //权限认证删除
//    @RequiresAuthentication
    @Autowired
    UserService userService;
    @GetMapping("/blogDelete/{blogId}")
    //这里最好不要真删除，可以做个逻辑删除
//    public Result DeleteBlog(@PathVariable(name = "blogId") long blogId,Long userId){
//        Result result=null;
//        long id=userId;
//        User user =userService.getById(id);
//
//        System.out.println("blogId:"+blogId);
//        System.out.println("userId:"+id);
//
//        if (user !=null) {
//
//            if (blogId > 0) {
//                Boolean bool = blogService.removeById(blogId);
//                if (bool) {
//                    result.setCode(200);
//                    return result;
//                } else {
//                    return result;
//                }
//            } else {
//                return result;
//            }
//        }else {
//            return result;
//        }
//
//    }
    public Result DeleteBlog(@PathVariable(name = "blogId") long blogId){
        Result result=null;
        System.out.println("blogId:"+blogId);
        System.out.println("进入方法。。。。。。。。。。。。。。。");
            if (blogId > 0) {
                Boolean bool = blogService.removeById(blogId);
                if (bool) {

                    return result.succ(200,"删除成功",null);
                } else {

                    return result.succ(400,"删除失败",null) ;
                }
            } else {
                return result.succ(403,"异常错误?改文章不存在",null);
            }
        }

}
