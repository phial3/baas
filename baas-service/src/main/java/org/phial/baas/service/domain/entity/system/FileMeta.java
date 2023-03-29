package org.phial.baas.service.domain.entity.system;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.mayanjun.mybatisx.api.annotation.Column;
import org.mayanjun.mybatisx.api.annotation.Index;
import org.mayanjun.mybatisx.api.annotation.IndexColumn;
import org.mayanjun.mybatisx.api.annotation.Table;
import org.mayanjun.mybatisx.api.entity.LongEditableEntity;
import org.mayanjun.mybatisx.api.enums.DataType;

/**
 * 文件元数据
 * @author mayanjun
 * @date 2019-07-12
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Table(value = "t_file_meta",
        indexes = {
                @Index(value = "idx_name", columns = @IndexColumn("name")),
                @Index(value = "idx_dir", columns = @IndexColumn("dir")),
                @Index(value = "idx_tag", columns = @IndexColumn("tag")),
        },
        comment = "访客记录")
public class FileMeta extends LongEditableEntity {

    /**
     * 文件名
     */
    @Column(length = "64", comment = "文件名")
    private String name;

    /**
     * 所在目录
     */
    @Column(length = "32", comment = "所在目录")
    private String dir;

    /**
     * 文件字节数
     */
    @Column(type = DataType.BIGINT, comment = "文件字节数")
    private Long size;

    /**
     * MIME类型
     */
    @Column(length = "128", comment = "MIME类型")
    private String mime;

    /**
     * 文件标签
     */
    @Column(length = "32", comment = "文件标签")
    private String tag;

    /**
     * 下载地址，动态生成
     */
    private String url;

    /**
     * 文件所在节点名称
     */
    @Column(length = "32", comment = "文件所在节点名称")
    private String nodeName;

    /**
     * 文件所在主机IP地址
     */
    @Column(length = "100", comment = "文件所在主机IP地址")
    private String host;

    /**
     * 原始文件名
     */
    private String originalFileName;

    public FileMeta() {
    }

    public FileMeta(Long id) {
        super(id);
    }
}
