package cn.structure.starter.mybatis.annotation;


import cn.structure.starter.mybatis.enums.SplitTableEnum;

import java.lang.annotation.*;


/**
 * <p>
 *     分表注解
 * </p>
 * @author chuck
 * @version 1.0.1
 * @since 2020/12/26 23:06
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface SplitTable {
    /**
     * 表名
     *
     * @return 表名
     */
    String tableName();

    /**
     * 分表方式，如 time：表示按时间分表
     * key：表示按字段取模分表
     *
     * @return 返回分表的类型
     */
    SplitTableEnum splitType();

    /**
     * 根据什么字段分表
     *
     * @return 字段
     */
    String splitBy();

    /**
     * 分表参数，分表方式为time: 表示用于分表的参数名（如 dateInterval）
     * 分表方式为key: 表示取模的基数（如 %32 ，%64）
     *
     * @return 分表参数
     */
    int keySplitParam() default 64;

    /**
     * 时间分表参数
     */
    String timeSplitFormat() default  "YYYY-MM";
}
