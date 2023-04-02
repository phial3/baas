package org.phial.baas.service.domain.entity.system;

import org.mayanjun.mybatisx.api.annotation.Column;
import org.mayanjun.mybatisx.api.annotation.Index;
import org.mayanjun.mybatisx.api.annotation.IndexColumn;
import org.mayanjun.mybatisx.api.annotation.Table;
import org.mayanjun.mybatisx.api.entity.LongEntity;
import org.mayanjun.mybatisx.api.enums.DataType;

/**
 * 方法名称映射
 * @since 2021/4/8
 * @author mayanjun
 */
@Table(value = "t_method_mapping",
        indexes = {
                @Index(value = "idx_name", columns = @IndexColumn(value = "name"))
        },
        comment = "方法名称映射")
public class MethodMapping extends LongEntity {

    /**
     * 类名
     */
    @Column(comment = "类名", type = DataType.VARCHAR, length = "1000")
    private String className;

    /**
     * 名称
     */
    @Column(comment = "名称", type = DataType.VARCHAR, length = "1000")
    private String name;

    /**
     * 默认构造器
     */
    public MethodMapping() {
    }

    /**
     * ID构造器
     */
    public MethodMapping(Long id) {
        super(id);
    }

    /**
     * 获取 name
     *
     * @return name
     */
    public String getName() {
        return name;
    }

    /**
     * 设置 name
     *
     * @param name name 值
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * 获取 className
     *
     * @return className
     */
    public String getClassName() {
        return className;
    }

    /**
     * 设置 className
     *
     * @param className className 值
     */
    public void setClassName(String className) {
        this.className = className;
    }
}
