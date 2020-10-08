package com.zy.service.impl;

import com.zy.entity.User;
import com.zy.mapper.UserMapper;
import com.zy.service.UserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author 关注github：2995090067@qq.com
 * @since 2020-06-16
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

}
