package org.phial.baas.service.sql;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.UpdateProvider;

import java.util.Date;

/**
 * 自定义SQL的 Mapper
 * @since 2019-07-06
 * @author mayanjun
 */
@Mapper
public interface CustomMapper {

    @UpdateProvider(type = CustomSQLBuilder.class, method = "databaseDDL")
    void generateDatabase();

}
