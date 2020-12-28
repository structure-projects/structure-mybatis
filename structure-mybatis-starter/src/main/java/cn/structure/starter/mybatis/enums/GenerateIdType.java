package cn.structure.starter.mybatis.enums;

/**
 * <p>
 *     生成ID的方式
 * </p>
 * @author chuck
 * @version 1.0.1
 * @since 2020/12/26 23:47
 */
public enum GenerateIdType {
    /**
     * 无没有任何操作
     */
    NONE,
    /**
     * 使用UUID生成
     */
    UUID,
    /**
     * 使用雪花耍法生成
     */
    SNOWFLAKE,
    ;

    GenerateIdType() {
    }
}
