package pers.xxiangyeyu.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import pers.xxiangyeyu.entity.UserInfo;
import pers.xxiangyeyu.mapper.UserInfoMapper;
import pers.xxiangyeyu.service.IUserInfoService;

/**
 * @author xxiangyeyu
 * @since 2022-12-15
 */
@Service
public class UserInfoServiceImpl extends ServiceImpl<UserInfoMapper, UserInfo> implements IUserInfoService {

}
