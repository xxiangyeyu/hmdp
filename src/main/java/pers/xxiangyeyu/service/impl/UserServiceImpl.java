package pers.xxiangyeyu.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import pers.xxiangyeyu.entity.User;
import pers.xxiangyeyu.mapper.UserMapper;
import pers.xxiangyeyu.service.IUserService;

/**
 * @author xxiangyeyu
 * @since 2022-12-15
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {

}
