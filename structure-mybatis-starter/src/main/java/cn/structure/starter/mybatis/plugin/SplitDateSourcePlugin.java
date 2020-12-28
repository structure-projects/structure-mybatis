package cn.structure.starter.mybatis.plugin;

import cn.structure.starter.mybatis.annotation.SplitTable;
import cn.structure.starter.mybatis.configuration.MybatisProperties;
import cn.structure.starter.mybatis.enums.SplitTableEnum;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ParameterMap;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.reflection.DefaultReflectorFactory;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.ReflectorFactory;
import org.apache.ibatis.reflection.factory.DefaultObjectFactory;
import org.apache.ibatis.reflection.factory.ObjectFactory;
import org.apache.ibatis.reflection.wrapper.DefaultObjectWrapperFactory;
import org.apache.ibatis.reflection.wrapper.ObjectWrapperFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * <p>
 *     分表sql重写
 * </p>
 * @author chuck
 * @version 1.0.1
 * @since 2020/12/26 23:47
 */
@Component
@Intercepts({@Signature(type = StatementHandler.class, method = "prepare", args = {Connection.class, Integer.class})})
public class SplitDateSourcePlugin implements Interceptor {

    @Autowired
    private MybatisProperties mybatisProperties;

    private Logger log = LoggerFactory.getLogger(SplitDateSourcePlugin.class);

    private static final ObjectFactory DEFAULT_OBJECT_FACTORY = new DefaultObjectFactory();
    private static final ObjectWrapperFactory DEFAULT_OBJECT_WRAPPER_FACTORY = new DefaultObjectWrapperFactory();
    private static final ReflectorFactory DEFAULT_REFLECTOR_FACTORY = new DefaultReflectorFactory();

    public static final String TABLE_NAME = "table_name";
    public static final String SPLIT_BY = "split_by";
    public static final String SPLIT_TYPE = "split_type";
    public static final String SPLIT_PARAM = "split_param";
    public static final String TIME_SPLIT_FORMAT = "time_split_format";
    public static final String EXECUTE_PARAM_DECLARE = "execute_param_declare";
    public static final String EXECUTE_PARAM_VALUES = "execute_param_values";
    public static final String ORIGINAL_SQL = "original_sql";
    public static final String SQL_COMMAND_TYPE = "sql_command_type";

    public Object intercept(Invocation invocation) throws Throwable {
        StatementHandler statementHandler = (StatementHandler) invocation
                .getTarget();
        MetaObject metaStatementHandler = MetaObject.forObject(
                statementHandler, DEFAULT_OBJECT_FACTORY,
                DEFAULT_OBJECT_WRAPPER_FACTORY, DEFAULT_REFLECTOR_FACTORY);
        doSplitTable(metaStatementHandler);
        // 传递给下一个拦截器处理
        return invocation.proceed();
    }

    /**
     * 分表入口
     *
     * @param metaStatementHandler
     * @throws Exception
     */
    private void doSplitTable(MetaObject metaStatementHandler) throws Exception {
        String originalSql = (String) metaStatementHandler.getValue("delegate.boundSql.sql");
        if (originalSql != null && !originalSql.equals("")) {
            Object param = metaStatementHandler.getValue("delegate.boundSql.parameterObject");
            log.info("分表前的SQL：" + originalSql);
            MappedStatement mappedStatement = (MappedStatement) metaStatementHandler.getValue("delegate.mappedStatement");
            String id = mappedStatement.getId();
            String className = id.substring(0, id.lastIndexOf("."));
            String methodName = id.substring(id.lastIndexOf(".") + 1);
            Class<?> clazz = Class.forName(className);
            ParameterMap paramMap = mappedStatement.getParameterMap();
            Method method = findMethod(clazz.getDeclaredMethods(), methodName);
            // 根据配置自动生成分表SQL
            SplitTable splitTable = null;
            if (method != null) {
                splitTable = method.getAnnotation(SplitTable.class);
            }
            if (splitTable == null) {
                splitTable = clazz.getAnnotation(SplitTable.class);
            }
            String convertedSql = originalSql;
            if (splitTable != null) {
                Map<String, Object> params = new HashMap<String, Object>();
                params.put(TABLE_NAME, splitTable.tableName());
                params.put(SPLIT_BY, splitTable.splitBy());
                params.put(SPLIT_TYPE, splitTable.splitType());
                params.put(SPLIT_PARAM, splitTable.keySplitParam());
                params.put(TIME_SPLIT_FORMAT, splitTable.timeSplitFormat());
                params.put(EXECUTE_PARAM_DECLARE, paramMap);
                params.put(EXECUTE_PARAM_VALUES, param);
                params.put(ORIGINAL_SQL, originalSql);
                params.put(SQL_COMMAND_TYPE, mappedStatement.getSqlCommandType());
                convertedSql = convert(params);
            }
            metaStatementHandler.setValue("delegate.boundSql.sql", convertedSql);
            log.info("分表后的SQL：" + convertedSql);
        }
    }

    /**
     * sql参数处理
     *
     * @param params
     * @return
     * @throws Exception
     */
    private String convert(Map<String, Object> params) throws Exception {
        //获取mysql参数对象
        Object objectParam = params.get(EXECUTE_PARAM_VALUES);
        Map<String, Object> splitParams = new HashMap<>();
        SplitTableEnum splitType = (SplitTableEnum) params.get(SPLIT_TYPE);
        //获取sql用到的参数
        if (null != objectParam) {
            String className = objectParam.getClass().getName();
            Class<?> clazz = Class.forName(className);
            //判断是否是map入参
            if (className.equals("java.util.HashMap")) {
                //判断是否为区域分表
                if (splitType.equals(SplitTableEnum.AREA_CODE)) {
                    //获取时间分表字段
                    Map map = (Map) objectParam;
                    Object value = map.get(params.get(SPLIT_BY));
                    splitParams.put(SPLIT_PARAM, value);
                }
                //时间分表类型
                if (splitType.equals(SplitTableEnum.TIME)) {
                    //获取时间分表字段
                    Map map = (Map) objectParam;
                    Object value = map.get(params.get(SPLIT_BY));
                    splitParams.put(SPLIT_BY, value);
                    Object value1 = map.get(params.get(SPLIT_PARAM));
                    //获取查询时间区间
                    splitParams.put(SPLIT_PARAM, value1);
                }
                //取模分表类型
                if (splitType.equals(SplitTableEnum.KEY)) {
                    //获取模分表字段
                    Map map = (Map) objectParam;
                    Object value2 = map.get(params.get(SPLIT_BY));
                    splitParams.put(SPLIT_BY, value2);
                    //设置取模基数
                    splitParams.put(SPLIT_PARAM, params.get(SPLIT_PARAM));
                }
            } else {
                //时间分表类型
                if (splitType.equals(SplitTableEnum.TIME)) {
                    //获取时间分表字段
                    Method getTimeMethod = findMethod(clazz.getDeclaredMethods(), "get" + captureName((String) params.get(SPLIT_BY)));
                    if (getTimeMethod != null) {
                        splitParams.put(SPLIT_BY, getTimeMethod.invoke(objectParam));
                    }
                    //获取查询时间区间
                    Method getTimeIntervalMethod = findMethod(clazz.getDeclaredMethods(), "get" + captureName((String) params.get(SPLIT_PARAM)));
                    if (getTimeIntervalMethod != null) {
                        splitParams.put(SPLIT_PARAM, getTimeIntervalMethod.invoke(objectParam));
                    }
                }
                //取模分表类型
                if (splitType.equals(SplitTableEnum.KEY)) {
                    //获取时间分表字段
                    Method getKeyMethod = findMethod(clazz.getDeclaredMethods(), "get" + captureName((String) params.get(SPLIT_BY)));
                    if (getKeyMethod != null) {
                        splitParams.put(SPLIT_BY, getKeyMethod.invoke(objectParam));
                    }
                    //设置取模基数
                    splitParams.put(SPLIT_PARAM, params.get(SPLIT_PARAM));
                }
            }

        }
        String convertedSql = null;
        //调用区域分表sql重构
        if (splitType.equals(SplitTableEnum.TIME)) {
            convertedSql = areaSplit(params, splitParams);
        }
        //调用时间分表sql重构
        if (splitType.equals(SplitTableEnum.TIME)) {
            convertedSql = timeSplit(params, splitParams);
        }
        //调用取模分表sql重构
        if (splitType.equals(SplitTableEnum.KEY)) {
            convertedSql = keySplit(params, splitParams);
        }
        return convertedSql;
    }

    /**
     * 区域sql重构
     *
     * @param params
     * @param splitParams
     * @return
     */
    private String areaSplit(Map<String, Object> params, Map<String, Object> splitParams) {
        String originalSql = (String) params.get(ORIGINAL_SQL);
        StringBuilder newTableName = new StringBuilder((String) params.get(TABLE_NAME));
        String area = (String) splitParams.get(SPLIT_BY);
        newTableName.append("_").append(area);
        //替换sql
        String convertedSql = replaceTableName(originalSql, (String) params.get(TABLE_NAME), newTableName.toString(), (SqlCommandType) params.get(SQL_COMMAND_TYPE), (ParameterMap) params.get(EXECUTE_PARAM_DECLARE));
        return convertedSql;
    }

    /**
     * 时间sql重构
     *
     * @param params
     * @param splitParams
     * @return
     */
    private String timeSplit(Map<String, Object> params, Map<String, Object> splitParams) {
        String originalSql = (String) params.get(ORIGINAL_SQL);
        String timeSplitFormat = (String) params.get(TIME_SPLIT_FORMAT);
        SimpleDateFormat sdf = new SimpleDateFormat(timeSplitFormat);
        Date date;
        if (splitParams.get(SPLIT_BY) != null) {
            date = (Date) splitParams.get(SPLIT_BY);
        } else {
            date = new Date();
        }
        int size;
        if (splitParams.get(SPLIT_PARAM) != null) {
            size = (Integer) splitParams.get(SPLIT_PARAM);
        } else {
            size = mybatisProperties.getSplitLength();
        }
        if (params.get(SQL_COMMAND_TYPE).equals(SqlCommandType.SELECT)) {
            StringBuilder unionTable = new StringBuilder("(");
            String unionSelect = new String("( SELECT * FROM " + params.get(TABLE_NAME).toString() + ")");
            for (int i = 0; i < size; i++) {
                StringBuilder tableName = new StringBuilder(params.get(TABLE_NAME).toString());
                Calendar c = Calendar.getInstance();
                c.setTime(date);
                c.add(Calendar.MONTH, i * -1);
                Date m = c.getTime();
                //获取前一个月
                String mon = sdf.format(m);
                tableName.append("_");
                tableName.append(mon);
                unionTable.append(unionSelect.replaceAll(params.get(TABLE_NAME).toString(), tableName.toString()));
                if (i != size - 1) {
                    unionTable.append(" UNION ");
                } else {
                    unionTable.append(") ac");
                }
            }
            String convertedSql = replaceTableName(originalSql, params.get(TABLE_NAME).toString(), unionTable.toString(), (SqlCommandType) params.get(SQL_COMMAND_TYPE), (ParameterMap) params.get(EXECUTE_PARAM_DECLARE));
            return convertedSql;
        } else {
            StringBuilder tableName = new StringBuilder(params.get(TABLE_NAME).toString());
            String mon = sdf.format(date);
            tableName.append("_");
            tableName.append(mon);
            String convertedSql = replaceTableName(originalSql, params.get(TABLE_NAME).toString(), tableName.toString(), (SqlCommandType) params.get(SQL_COMMAND_TYPE), (ParameterMap) params.get(EXECUTE_PARAM_DECLARE));
            return convertedSql;
        }
    }

    /**
     * 取模sql重构
     *
     * @param params
     * @param splitParams
     * @return
     */
    private String keySplit(Map<String, Object> params, Map<String, Object> splitParams) {
        String originalSql = (String) params.get(ORIGINAL_SQL);
        StringBuilder newTableName = new StringBuilder((String) params.get(TABLE_NAME));
        Long id = (Long) splitParams.get(SPLIT_BY);
        Integer splitParam = (Integer) splitParams.get(SPLIT_PARAM);
        //计算id模计算后的值
        Long tableNameAfter = id % splitParam;
        //拼接取模后的表名
        newTableName.append("_").append(tableNameAfter);
        //替换sql
        String convertedSql = replaceTableName(originalSql, (String) params.get(TABLE_NAME), newTableName.toString(), (SqlCommandType) params.get(SQL_COMMAND_TYPE), (ParameterMap) params.get(EXECUTE_PARAM_DECLARE));
        return convertedSql;
    }

    /**
     * sql替换
     *
     * @param originalSql
     * @param tableName
     * @param newTableName
     * @param sqlType
     * @return
     */
    private String replaceTableName(String originalSql, String tableName, String newTableName, SqlCommandType sqlType, ParameterMap parameterMap) {
        StringBuilder newSql = new StringBuilder();
        String sqlTypeStr = null;
        if (sqlType.equals(SqlCommandType.INSERT)) {
            sqlTypeStr = "INSERT INTO ";
        }
        if (sqlType.equals(SqlCommandType.UPDATE)) {
            sqlTypeStr = "UPDATE ";
        }
        if (sqlType.equals(SqlCommandType.SELECT) || sqlType.equals(SqlCommandType.DELETE)) {
            sqlTypeStr = "FROM ";
        }
        //System.out.println(parameterMap.getId());
        int sqlTypeStrIndex = originalSql.indexOf(sqlTypeStr);
        //未找到指定关键字返回原sql
        if (sqlTypeStrIndex == -1) {
            return originalSql;
        }
        int tableNameIndex = originalSql.indexOf(tableName, sqlTypeStrIndex);
        //"INSERT INTO "
        String sqlBefore = originalSql.substring(0, sqlTypeStrIndex + sqlTypeStr.length());
        //" (xxx) VALUES (xxx)"
        String sqlAfter = originalSql.substring(tableNameIndex + tableName.length());
        newSql.append(sqlBefore).append(newTableName).append(sqlAfter);
        return newSql.toString();
    }

    /**
     * 查询指定方法
     *
     * @param methods
     * @param methodName
     * @return
     */
    private Method findMethod(Method[] methods, String methodName) {
        for (Method method : methods) {
            if (method.getName().equals(methodName)) {
                return method;
            }
        }
        return null;
    }

    /**
     * 首字母大写
     *
     * @param name
     * @return
     */
    private String captureName(String name) {
        char[] cs = name.toCharArray();
        cs[0] -= 32;
        return String.valueOf(cs);
    }

    public Object plugin(Object target) {
        // 当目标类是StatementHandler类型时，才包装目标类，否者直接返回目标本身,减少目标被代理的
        // 次数
        if (target instanceof StatementHandler) {
            return Plugin.wrap(target, this);
        } else {
            return target;
        }
    }

    public void setProperties(Properties properties) {

    }
}
