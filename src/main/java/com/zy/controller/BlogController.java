package com.zy.controller;


import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.lang.Assert;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zy.common.lang.Result;

import com.zy.entity.Blog;
import com.zy.service.BlogService;
import com.zy.util.ShiroUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author 关注github：2995090067@qq.com
 * @since 2020-07-05
 */
@Slf4j //测试一下记得删除
@RestController
public class BlogController {

    @Autowired
    BlogService blogService;

    /**
     * 文章列表
     * @param currentPage
     * @return
     */
    @GetMapping("/blogs")
    public Result list(@RequestParam(defaultValue = "1") Integer currentPage){
       //分页处理，此处都是mybatisplus包下的
        Page page = new Page(currentPage,5);
        IPage pageData = blogService.page(page, new QueryWrapper<Blog>().orderByDesc("created"));
        return Result.success(pageData);
    }

    /**
     * 文章详情
     * @param id
     * @return
     */
    @GetMapping("/blog/{id}")
    public Result detail(@PathVariable(name = "id")Long id){

        Blog blog = blogService.getById(id);
        Assert.notNull(blog,"此文章已被删除！");
        return Result.success(blog);
    }


    /**
     * 文章编辑
     * @param blog
     * @return
     */
    @RequiresAuthentication //认证后方可访问该接口
    @PostMapping("/blog/edit")
    public Result edit(@Validated @RequestBody Blog blog){

        System.out.println("进入方法==>id:" +blog.getId());

        Blog temp =null;
        if(blog.getId() !=null){
            temp = blogService.getById(blog.getId());
            // 只能编辑自己的文章
            log.debug("zylog,数据库文章id:==>"+temp.getId()+"当前用户id===>"+ShiroUtil.getProfile().getId().longValue());
            System.out.println(ShiroUtil.getProfile().getId());

            Assert.isTrue(temp.getUserId().longValue()==ShiroUtil.getProfile().getId().longValue(),"无编辑权限!");
        }else {
            //初始化新文章
            temp = new Blog();
            temp.setUserId(ShiroUtil.getProfile().getId());
            temp.setCreated(LocalDateTime.now());
            temp.setStatus(0);
        }
        //复制文章数据，忽略不需要的字段
        BeanUtil.copyProperties(blog,temp,"id","userId","created","status");
        blogService.saveOrUpdate(temp);

        log.debug("进入方法==>id:" +blog.getId());
//        文章发表成功！
        return Result.success(null);
    }


    /**
     * 文章删除
     * @param id
     * @return
     */
    @GetMapping("/blog/delete/{id}")
    public Result delete(@PathVariable(name = "id")String  id){
        boolean result = blogService.removeById(id);// 据库中为Long id
        Assert.isTrue(result,"此文章已被删除！");
        return Result.success("成功删除文章！");
    }

    /**
     * 模糊查询
     * @param title
     * @return
     */
    //Error:Failed to convert value of type 'java.lang.String' to required type 'java.lang.Long';
    @GetMapping("/blog/findByTitle")
    public Result findByTitle(@RequestParam(defaultValue = "") String title){
        List<Blog> blogs = blogService.list(new QueryWrapper<Blog>()
                .like("title", title)
                .orderByDesc("created"));
        Assert.notNull(blogs,"未查询到指定文章");

        return Result.success(blogs);
    }


}
