package cn.structure.starter.mybatis.configuration;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.context.annotation.Configuration;

/**
 * <p>
 *     分页配置
 * </p>
 * @author chuck
 * @version 1.0.1
 * @since 2020/12/26 23:47
 */
@Getter
@Setter
@ToString
@Configuration
public class PageHelperProperties {
    private String helperDialect = "mysql";
    private String reasonable ="true";
    private String pageSizeZero = "true";
    private String supportMethodsArguments = "true";
    private String params = "count=countSql";
}
