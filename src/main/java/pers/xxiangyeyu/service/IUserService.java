package pers.xxiangyeyu.service;

import com.baomidou.mybatisplus.extension.service.IService;
import pers.xxiangyeyu.dto.LoginFormDTO;
import pers.xxiangyeyu.dto.Result;
import pers.xxiangyeyu.entity.User;

import javax.servlet.http.HttpSession;

/**
 * @author xxiangyeyu
 * @since 2022-12-15
 */
public interface IUserService extends IService<User> {

    Result sendCode(String phone, HttpSession session);

    Result login(LoginFormDTO loginForm, HttpSession session);
}
