package com.phial.baas.api.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.web.multipart.MultipartFile;

import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.util.Date;

/**
 * @author gyf
 * @date 2022/12/12
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class SmartContract extends Entity<Long> implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * 唯一id
     */
    private Long id;

    /**
     * 名称
     */
    private String name;

    /**
     * 中文名称和描述
     */
    private String description;

    /**
     * 版本
     */
    private String version;

    /**
     * 部署的chain
     */
    private String chainId;

    /**
     * 合约状态
     */
    private Integer status;

    /**
     * 运行类型, go,cpp,wasm
     */
    private String runtimeType;
}