package org.phial.baas.service.domain.entity.system;

import org.mayanjun.mybatisx.api.annotation.Column;
import org.mayanjun.mybatisx.api.annotation.Index;
import org.mayanjun.mybatisx.api.annotation.IndexColumn;
import org.mayanjun.mybatisx.api.annotation.Table;
import org.mayanjun.mybatisx.api.entity.LongEntity;
import org.mayanjun.mybatisx.api.enums.DataType;

import java.util.Date;

/**
 * API接口访问日志
 * @since 2021/4/8
 * @author mayanjun
 */
@Table(value = "t_access_log",
        indexes = {
                @Index(value = "idx_profilerName", columns = @IndexColumn("profilerName")),
                @Index(value = "idx_uri", columns = @IndexColumn("uri")),
                @Index(value = "idx_date", columns = @IndexColumn("date"))
        },
        comment = "访问日志")
public class AccessLog extends LongEntity {

    /**
     * 统计名称
     */
    @Column(comment = "统计名称", type = DataType.VARCHAR, length = "32")
    private String profilerName;

    /**
     * 访问URI
     */
    @Column(comment = "访问URI", type = DataType.VARCHAR, length = "100")
    private String uri;

    /**
     * 方法签名ID
     */
    @Column(comment = "方法签名ID", type = DataType.VARCHAR, length = "32")
    private Long methodId;

    /**
     * HTTP方法
     */
    @Column(comment = "HTTP方法", type = DataType.VARCHAR, length = "20")
    private String httpMethod;

    /**
     * HTTP MIME
     */
    @Column(comment = "HTTP MIME", type = DataType.VARCHAR, length = "100")
    private String contentType;

    /**
     * 访问用户
     */
    @Column(comment = "访问用户", type = DataType.VARCHAR, length = "64")
    private String user;

    /**
     * 请求参数
     */
    @Column(comment = "请求参数", type = DataType.LONGTEXT)
    private String parameters;

    /**
     * 请求时长:毫秒
     */
    @Column(comment = "请求时长:毫秒", type = DataType.BIGINT)
    private Long elapsed;

    /**
     * 是否发生异常
     */
    @Column(comment = "是否发生异常", type = DataType.BIT, length = "1")
    private Boolean exception;

    /**
     * 错误消息
     */
    @Column(comment = "错误消息", type = DataType.VARCHAR, length = "200")
    private String message;

    /**
     * 用户特征
     */
    @Column(comment = "UA", type = DataType.VARCHAR, length = "1000")
    private String userAgent;

    /**
     * 服务端IP
     */
    @Column(comment = "服务端IP", type = DataType.VARCHAR, length = "16")
    private String serverAddress;

    /**
     * 客户端IP
     */
    @Column(comment = "客户端IP", type = DataType.VARCHAR, length = "16")
    private String clientAddress;

    /**
     * 访问时间
     */
    @Column(comment = "时间", type = DataType.DATETIME)
    private Date date;

    /**
     * 默认构造器
     */
    public AccessLog() {
    }

    /**
     * ID构造器
     */
    public AccessLog(Long id) {
        super(id);
    }

    /**
     * 获取 uri
     *
     * @return uri
     */
    public String getUri() {
        return uri;
    }

    /**
     * 设置 uri
     *
     * @param uri uri 值
     */
    public void setUri(String uri) {
        this.uri = uri;
    }

    /**
     * 获取 contentType
     *
     * @return contentType
     */
    public String getContentType() {
        return contentType;
    }

    /**
     * 设置 contentType
     *
     * @param contentType contentType 值
     */
    public void setContentType(String contentType) {
        this.contentType = contentType;
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
     * 获取 parameters
     *
     * @return parameters
     */
    public String getParameters() {
        return parameters;
    }

    /**
     * 设置 parameters
     *
     * @param parameters parameters 值
     */
    public void setParameters(String parameters) {
        this.parameters = parameters;
    }

    /**
     * 获取 elapsed
     *
     * @return elapsed
     */
    public Long getElapsed() {
        return elapsed;
    }

    /**
     * 设置 elapsed
     *
     * @param elapsed elapsed 值
     */
    public void setElapsed(Long elapsed) {
        this.elapsed = elapsed;
    }

    /**
     * 获取 exception
     *
     * @return exception
     */
    public Boolean getException() {
        return exception;
    }

    /**
     * 设置 exception
     *
     * @param exception exception 值
     */
    public void setException(Boolean exception) {
        this.exception = exception;
    }

    /**
     * 获取 message
     *
     * @return message
     */
    public String getMessage() {
        return message;
    }

    /**
     * 设置 message
     *
     * @param message message 值
     */
    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * 获取 methodId
     *
     * @return methodId
     */
    public Long getMethodId() {
        return methodId;
    }

    /**
     * 设置 methodId
     *
     * @param methodId methodId 值
     */
    public void setMethodId(Long methodId) {
        this.methodId = methodId;
    }

    /**
     * 获取 httpMethod
     *
     * @return httpMethod
     */
    public String getHttpMethod() {
        return httpMethod;
    }

    /**
     * 设置 httpMethod
     *
     * @param httpMethod httpMethod 值
     */
    public void setHttpMethod(String httpMethod) {
        this.httpMethod = httpMethod;
    }

    /**
     * 获取 profilerName
     *
     * @return profilerName
     */
    public String getProfilerName() {
        return profilerName;
    }

    /**
     * 设置 profilerName
     *
     * @param profilerName profilerName 值
     */
    public void setProfilerName(String profilerName) {
        this.profilerName = profilerName;
    }

    /**
     * 获取 userAgent
     *
     * @return userAgent
     */
    public String getUserAgent() {
        return userAgent;
    }

    /**
     * 设置 userAgent
     *
     * @param userAgent userAgent 值
     */
    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    /**
     * 获取 serverAddress
     *
     * @return serverAddress
     */
    public String getServerAddress() {
        return serverAddress;
    }

    /**
     * 设置 serverAddress
     *
     * @param serverAddress serverAddress 值
     */
    public void setServerAddress(String serverAddress) {
        this.serverAddress = serverAddress;
    }

    /**
     * 获取 clientAddress
     *
     * @return clientAddress
     */
    public String getClientAddress() {
        return clientAddress;
    }

    /**
     * 设置 clientAddress
     *
     * @param clientAddress clientAddress 值
     */
    public void setClientAddress(String clientAddress) {
        this.clientAddress = clientAddress;
    }

    /**
     * 获取 date
     *
     * @return date
     */
    public Date getDate() {
        return date;
    }

    /**
     * 设置 date
     *
     * @param date date 值
     */
    public void setDate(Date date) {
        this.date = date;
    }
}
