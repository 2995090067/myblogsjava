package com.zy.service.impl;

import com.zy.entity.Blog;
import com.zy.mapper.BlogMapper;
import com.zy.service.BlogService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author 关注github：2995090067@qq.com
 * @since 2020-07-05
 */
@Service
public class BlogServiceImpl extends ServiceImpl<BlogMapper, Blog> implements BlogService {

}
