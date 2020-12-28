package cn.structure.starter.mybatis.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Date;

/**
 * <p>
 *     修改时间注解
 * </p>
 * @author chuck
 * @version 1.0.1
 * @since 2020/12/26 23:06
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface UpdateTime {
    Class<?> value() default Date.class;
}
