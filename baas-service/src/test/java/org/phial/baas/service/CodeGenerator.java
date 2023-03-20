package org.phial.baas.service;

import com.baomidou.mybatisplus.generator.FastAutoGenerator;
import com.baomidou.mybatisplus.generator.config.OutputFile;
import com.baomidou.mybatisplus.generator.config.rules.DateType;
import com.baomidou.mybatisplus.generator.engine.FreemarkerTemplateEngine;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CodeGenerator {

    /**
     * 自动生成代码
     */
    public static void main(String[] args) {
        String url = "jdbc:mysql://localhost:3306/test?serverTimezone=Asia/Shanghai";

        List<String> schemas = new ArrayList<>(); // 添加数据库中的表名称列表
        schemas.add("user");
        FastAutoGenerator.create(url, "root", "123456aA#")
                //全局配置
                .globalConfig(builder -> {
                    builder.author("admin")                                              // 设置作者
                            .enableSwagger()                                                // 开启 swagger 模式
                            .disableOpenDir()                                               // 禁止打开输出目录
                            .dateType(DateType.TIME_PACK)                                   // 时间策略
                            .commentDate("yyyy-MM-dd")                              // 注释日期
                            .outputDir(System.getProperty("user.dir")
                                    + "/src/main/java");                           // 指定输出目录
                })
                //包配置
                .packageConfig(builder -> {
                    builder.parent("generator")                                    // 设置父包名(也可以生成目录)
                            //.moduleName("system")                                     // 设置父包模块名
                            .entity("model")
                            .service("service")
                            .controller("controller")
                            .mapper("mapper")                                           // Mapper 包名
                            .xml("mapper")                                              // Mapper XML 包名
                            .pathInfo(Collections.singletonMap(
                                    OutputFile.xml,
                                    System.getProperty("user.dir")
                                            + "/src/main/resources/mapper"));           // 设置mapperXml生成路径

                })
                //策略配置
                .strategyConfig(builder -> {
                    builder.addInclude(schemas)                                          // 设置需要生成的表名
                            .addTablePrefix("")                                       // 表前缀过滤
                            .entityBuilder()                                            // 切换至Entity设置
                            .enableFileOverride()                                       //文件覆盖
                            .versionColumnName("version")                               // 乐观锁字段名(数据库)
                            .logicDeleteColumnName("deleted")                         // 逻辑删除字段名(数据库)
                            .enableLombok()                                             // lombok生效
                            .enableTableFieldAnnotation()                               // 所有实体类加注解
                            .serviceBuilder()                                           // 切换至Service层设置
                            .formatServiceFileName("%sService")                         // 设定后缀名
                            .formatServiceImplFileName("%sServiceImpl");                // 设定后缀名
                })
                //模板配置
                .templateEngine(new FreemarkerTemplateEngine())                         // 使用Freemarker引擎模板，默认的是Velocity引擎模板
                .execute();

    }
}
