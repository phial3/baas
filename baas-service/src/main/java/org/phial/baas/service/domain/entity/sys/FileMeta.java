package org.phial.baas.service.domain.entity.sys;

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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDir() {
        return dir;
    }

    public void setDir(String dir) {
        this.dir = dir;
    }

    public Long getSize() {
        return size;
    }

    public void setSize(Long size) {
        this.size = size;
    }

    public String getMime() {
        return mime;
    }

    public void setMime(String mime) {
        this.mime = mime;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getOriginalFileName() {
        return originalFileName;
    }

    public void setOriginalFileName(String originalFileName) {
        this.originalFileName = originalFileName;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getNodeName() {
        return nodeName;
    }

    public void setNodeName(String nodeName) {
        this.nodeName = nodeName;
    }
}
