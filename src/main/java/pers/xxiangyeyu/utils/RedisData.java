package pers.xxiangyeyu.utils;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author xxiangyeyu
 * @since 2022-12-15
 */
@Data
public class RedisData {
    private LocalDateTime expireTime;
    private Object data;
}
