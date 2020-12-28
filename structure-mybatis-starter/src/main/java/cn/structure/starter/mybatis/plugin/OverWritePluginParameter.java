package cn.structure.starter.mybatis.plugin;

import cn.hutool.core.lang.Snowflake;
import cn.hutool.core.util.IdUtil;
import cn.structure.starter.mybatis.annotation.CreateTime;
import cn.structure.starter.mybatis.annotation.UpdateTime;
import cn.structure.starter.mybatis.configuration.MybatisProperties;
import org.apache.ibatis.binding.MapperMethod;
import org.apache.ibatis.cache.CacheKey;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.persistence.Id;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * <p>
 *     sql查询属性重写
 * </p>
 * @author chuck
 * @version 1.0.1
 * @since 2020/12/26 23:47
 */
@Intercepts({
        @Signature(type = Executor.class, method = "update", args = {MappedStatement.class, Object.class}),
        @Signature(type = Executor.class, method = "query", args = {MappedStatement.class, Object.class,
                RowBounds.class, ResultHandler.class}),
        @Signature(type = Executor.class, method = "query", args = {MappedStatement.class, Object.class,
                RowBounds.class, ResultHandler.class, CacheKey.class, BoundSql.class}),
        @Signature(type = Executor.class, method = "queryCursor", args = {MappedStatement.class, Object.class,
                RowBounds.class})
})
@Component
public class OverWritePluginParameter implements Interceptor {

    @Autowired
    private Snowflake snowflake;

    @Autowired
    private MybatisProperties properties;

    public Object intercept(Invocation invocation) throws Throwable {
        MappedStatement ms = (MappedStatement) invocation.getArgs()[0];
        SqlCommandType type = ms.getSqlCommandType();
        Object parameter = invocation.getArgs()[1];
        //create update time 自动生成
        if (parameter != null) {
            if (parameter instanceof MapperMethod.ParamMap) {
                return invocation.proceed();
            }
            if (parameter instanceof Map) {
                Map param = (Map) parameter;
                if (null != param.get("collection")) {
                    List list = (List) param.get("collection");
                    for (int i = 0; i < list.size(); i++) {
                        makeOverWrite(list.get(i), type);
                    }
                } else {
                    makeOverWrite(parameter, type);
                }
            } else {
                makeOverWrite(parameter, type);
            }
        }
        return invocation.proceed();
    }

    private void makeOverWrite(Object parameter, SqlCommandType type) throws IllegalAccessException, InstantiationException {
        // 获取成员变量
        Field[] declaredFields = parameter.getClass().getDeclaredFields();
        for (Field field : declaredFields) {
            if (null != field.getAnnotation(Id.class)) {
                // insert 语句插入 ID
                if (SqlCommandType.INSERT.equals(type)) {
                    field.setAccessible(true);
                    switch (properties.getGenerateIdType()) {
                        case UUID:
                            field.set(parameter, IdUtil.simpleUUID());
                            break;
                        case SNOWFLAKE:
                            if ("java.lang.Long".equals(field.getGenericType().getTypeName())){
                                field.set(parameter, snowflake.nextId());
                            }else {
                                field.set(parameter, String.valueOf(snowflake.nextId()));
                            }
                            break;
                        default:
                            //默认没有是因为使用数据库自增
                            break;
                    }
                }
            }
            if (null != field.getAnnotation(CreateTime.class)) {
                // insert 语句插入 createTime
                if (SqlCommandType.INSERT.equals(type)) {
                    field.setAccessible(true);
                    if (null == field.get(parameter)) {
                        field.set(parameter, field.getAnnotation(CreateTime.class).value().newInstance());
                    }
                }
            }
            // insert 或 update 语句插入 updateTime
            if (null != field.getAnnotation(UpdateTime.class)) {
                if (SqlCommandType.INSERT.equals(type) || SqlCommandType.UPDATE.equals(type)) {
                    field.setAccessible(true);
                    if (null == field.get(parameter)) {
                        field.set(parameter, field.getAnnotation(UpdateTime.class).value().newInstance());
                    }
                }
            }
        }
    }

    public Object plugin(Object target) {
        if (target instanceof Executor) {
            return Plugin.wrap(target, this);
        } else {
            return target;
        }
    }

    public void setProperties(Properties properties) {

    }
}
