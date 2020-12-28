package cn.structure.example.mybatisplus;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * <p>
 * 启动mybatis-plus例子入口
 * </p>
 *
 * @author chuck
 * @version 1.0.1
 * @since 2020/12/27 20:26
 */
@MapperScan("cn.structure.example.mybatisplus.mapper.**")
@SpringBootApplication
public class MyBatisPlusApplication {
    public static void main(String[] args) {
        SpringApplication.run(MyBatisPlusApplication.class,args);
    }
}
