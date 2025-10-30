package com.birdy.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.birdy.domain.entity.User;
import com.birdy.service.UserService;
import com.birdy.mapper.UserMapper;
import org.springframework.stereotype.Service;

/**
* @author Birdy
* @description 针对表【sys_user(用户表)】的数据库操作Service实现
* @createDate 2025-10-30 13:06:55
*/
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
    implements UserService{

}




