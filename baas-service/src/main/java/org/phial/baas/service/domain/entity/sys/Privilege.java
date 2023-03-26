package org.phial.baas.service.domain.entity.sys;

import org.mayanjun.mybatisx.api.annotation.Column;
import org.mayanjun.mybatisx.api.entity.LongEditableEntity;

/**
 * 权限
 *
 * @author mayanjun
 * @since 2021/4/8
 */
//@Table(value = "t_privilege",
//        indexes = {
//                @Index(value = "idx_name", columns = @IndexColumn("name")),
//                @Index(value = "idx_method", columns = @IndexColumn("method"))
//        },
//        comment = "权限")
public class Privilege extends LongEditableEntity {

    @Column(length = "32", comment = "名称")
    private String name;

    @Column(length = "100", comment = "方法")
    private String method;

    @Column(length = "1000", comment = "依赖")
    private String dependencies;

    @Column(length = "500", comment = "备注")
    private String description;

    public Privilege() {
    }

    public Privilege(Long id) {
        super(id);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getDependencies() {
        return dependencies;
    }

    public void setDependencies(String dependencies) {
        this.dependencies = dependencies;
    }

    @Override
    public int hashCode() {
        Long id = getId();
        if (id == null) return System.identityHashCode(this);
        return id.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj != null && obj instanceof Privilege) {
            Long thisId = getId();
            Long thatId = ((Privilege) obj).getId();
            if (thisId == null || thatId == null) {
                return System.identityHashCode(this) == System.identityHashCode(obj);
            } else {
                return thisId.equals(thatId);
            }
        } else {
            return false;
        }
    }
}
