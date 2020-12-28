package cn.structure.starter.mybatis.enums;

/**
 * <p>
 *     分表的类型
 * </p>
 * @author chuck
 * @version 1.0.1
 * @since 2020/12/26 23:06
 */
public enum SplitTableEnum {
    /**
     * 按照地区码分表
     */
    AREA_CODE,
    /**
     * 按照创建时间分表
     */
    TIME,
    /**
     * 按照主键ID分表
     */
    KEY;

    SplitTableEnum() {
    }
}
