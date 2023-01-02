package pers.xxiangyeyu.dto;

import lombok.Data;

/**
 * @author xxiangyeyu
 * @since 2022-12-15
 */
@Data
public class LoginFormDTO {
    private String phone;
    private String code;
    private String password;
}
