package cn.structure.example.tk.mapper;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import tk.mybatis.spring.annotation.MapperScan;

/**
 * <p>
 * 启动Tk-Mapper例子入口
 * </p>
 *
 * @author chuck
 * @version 1.0.1
 * @since 2020/12/27 20:28
 */
@MapperScan("cn.structure.example.tk.mapper.dao.**")
@SpringBootApplication
public class TkMapperApplication {
    public static void main(String[] args) {
        SpringApplication.run(TkMapperApplication.class,args);
    }
}
