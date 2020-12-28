package cn.structure.starter.mybatis.annotation;


import cn.structure.starter.mybatis.configuration.EnableSplitDateSource;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * <p>
 *     开启分表功能
 * </p>
 * @author chuck
 * @version 1.0.1
 * @since 2020/12/26 23:06
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
@Import(EnableSplitDateSource.class)
public @interface EnableSplitTable {

}
