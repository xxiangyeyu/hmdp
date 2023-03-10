package pers.xxiangyeyu;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

/**
 * @author xxiangyeyu
 * @since 2022-12-15
 */
@EnableAspectJAutoProxy(exposeProxy = true)
@MapperScan("pers.xxiangyeyu.mapper")
@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

}
