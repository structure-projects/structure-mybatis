package cn.structure.example.mybatisplus;

import com.baomidou.mybatisplus.generator.AutoGenerator;
import com.baomidou.mybatisplus.generator.config.*;
import com.baomidou.mybatisplus.generator.config.rules.NamingStrategy;

/**
 * @author: CHUCK
 * @date: 2020/11/6 13:54
 * @description: 代码生成
 */
public class GenMybatisPlus {
    /**
     * 父包
     */
    static String parent ="cn.structure.example.mybatisplus";

    static String module = "/structure-mybatis-starter-example/structure-mybatis-plus-starter-example";
    /**
     * 要生成的表名
     */
    static String [] tableList = {"user"};
    /**
     * 数据库信息不用改
     */
    static String url = "jdbc:mysql://localhost:3306/user?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC";

    static String user = "root";

    static String pwd = "123456";

    public static void main(String[] args) {

        genCode();

    }
    private static void genCode() {
        //代码生成器
        AutoGenerator autoGenerator = new AutoGenerator();
        //全局配置
        GlobalConfig globalConfig = new GlobalConfig();
        String projectPath = System.getProperty("user.dir");
        globalConfig.setOutputDir(projectPath + module + "/src/main/java");
        globalConfig.setAuthor("chuck");
        globalConfig.setOpen(false);
        autoGenerator.setGlobalConfig(globalConfig);
        // 数据源配置
        DataSourceConfig dsc = new DataSourceConfig();
        dsc.setUrl(url);
        dsc.setDriverName("com.mysql.cj.jdbc.Driver");
        dsc.setUsername(user);
        dsc.setPassword(pwd);
        autoGenerator.setDataSource(dsc);
        // 包配置
        PackageConfig pc = new PackageConfig();
        pc.setParent(parent);
        pc.setController("controller");
        pc.setService("service");
        pc.setServiceImpl("service.impl");
        pc.setMapper("mapper");
        pc.setEntity("pojo.po");
        autoGenerator.setPackageInfo(pc);
        StrategyConfig strategy = new StrategyConfig();
        strategy.setEntityLombokModel(true);
        strategy.setNaming(NamingStrategy.underline_to_camel);
        strategy.setColumnNaming(NamingStrategy.underline_to_camel);
        strategy.setEntityBooleanColumnRemoveIsPrefix(true);
        strategy.setEntityTableFieldAnnotationEnable(true);
        strategy.setRestControllerStyle(true);
        //要生成的表
        strategy.setInclude(tableList);
        autoGenerator.setStrategy(strategy);
        autoGenerator.execute();
    }
}
