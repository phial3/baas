package org.phial.baas.service.domain.entity.system;


import org.mayanjun.mybatisx.api.annotation.Column;
import org.mayanjun.mybatisx.api.entity.LongEditableEntity;

/**
 * 属性
 * @since 2021/4/8
 * @author mayanjun
 */
//@Table(value = "t_attribute",
//        indexes = {
//                @Index(value = "idx_name", columns = {
//                        @IndexColumn("group"),
//                        @IndexColumn("name"),
//                        @IndexColumn("user")
//                }, type = IndexType.UNIQUE)
//        },
//        comment = "属性")
public class Attribute extends LongEditableEntity {

    /**
     * 所属用户
     */
    @Column(length = "32", comment = "所属用户")
    private String user;

    /**
     * 属性名称
     */
    @Column(length = "32", comment = "属性名称")
    private String name;

    /**
     * 属性组
     */
    @Column(length = "32", comment = "属性组")
    private String group;

    /**
     * 属性值
     */
    @Column(length = "5000", comment = "属性值")
    private String value;

    /**
     * 备注
     */
    @Column(length = "255", comment = "备注")
    private String description;

    /**
     * 默认构造器
     */
    public Attribute() {
    }

    /**
     * ID构造器
     */
    public Attribute(Long id) {
        super(id);
    }

    /**
     * 获取 user
     *
     * @return user
     */
    public String getUser() {
        return user;
    }

    /**
     * 设置 user
     *
     * @param user user 值
     */
    public void setUser(String user) {
        this.user = user;
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
     * 获取 group
     *
     * @return group
     */
    public String getGroup() {
        return group;
    }

    /**
     * 设置 group
     *
     * @param group group 值
     */
    public void setGroup(String group) {
        this.group = group;
    }

    /**
     * 获取 value
     *
     * @return value
     */
    public String getValue() {
        return value;
    }

    /**
     * 设置 value
     *
     * @param value value 值
     */
    public void setValue(String value) {
        this.value = value;
    }

    /**
     * 获取 description
     *
     * @return description
     */
    public String getDescription() {
        return description;
    }

    /**
     * 设置 description
     *
     * @param description description 值
     */
    public void setDescription(String description) {
        this.description = description;
    }
}
