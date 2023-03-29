package org.phial.baas.service.domain.entity.system;

import lombok.Data;
import org.mayanjun.mybatisx.api.annotation.Column;
import org.mayanjun.mybatisx.api.annotation.Index;
import org.mayanjun.mybatisx.api.annotation.IndexColumn;
import org.mayanjun.mybatisx.api.annotation.Table;
import org.mayanjun.mybatisx.api.entity.LongEditableEntity;

/**
 * 权限
 *
 * @author mayanjun
 * @since 2021/4/8
 */
@Data
@Table(value = "t_privilege",
        indexes = {
                @Index(value = "idx_name", columns = @IndexColumn("name")),
                @Index(value = "idx_method", columns = @IndexColumn("method"))
        },
        comment = "权限")
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

    @Override
    public int hashCode() {
        Long id = getId();
        if (id == null) return System.identityHashCode(this);
        return id.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Privilege) {
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
