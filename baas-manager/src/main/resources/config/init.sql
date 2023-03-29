CREATE TABLE IF NOT EXISTS `baas_chain`
(
    `id`           BIGINT UNSIGNED NOT NULL DEFAULT 0 COMMENT 'ID',
    `modifiedTime` DATETIME        NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT 'Last Modified Time',
    `type`         INT             NOT NULL DEFAULT 0 COMMENT '联盟链类型',
    `genesisBlock` BLOB            NOT NULL COMMENT '创世块',
    `blockHeight`  BIGINT          NOT NULL DEFAULT 0 COMMENT '链当前块高度',
    `txCount`      BIGINT          NOT NULL DEFAULT 0 COMMENT '链上总交易数',
    `createdTime`  DATETIME        NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT 'Created Time',
    `status`       INT             NOT NULL DEFAULT 0 COMMENT '联盟链状态',
    `enabled`      BIT(1)          NOT NULL DEFAULT 1 COMMENT '是否启用',
    `chainId`      VARCHAR(16)     NOT NULL DEFAULT '' COMMENT '链的唯一标识ID',
    `editor`       VARCHAR(32)     NOT NULL DEFAULT '' COMMENT 'Last editor',
    `creator`      VARCHAR(32)     NOT NULL DEFAULT '' COMMENT 'Creator',
    `name`         VARCHAR(64)     NOT NULL DEFAULT '' COMMENT '名称',
    `pinyin`       VARCHAR(255)    NOT NULL DEFAULT '' COMMENT '名称拼音',
    `extras`       VARCHAR(500)    NOT NULL DEFAULT '' COMMENT '扩展信息',
    `description`  VARCHAR(500)    NOT NULL DEFAULT '' COMMENT '备注',
    PRIMARY KEY (`id`),
    UNIQUE KEY `idx_chain_id` (`chainId`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8
  COLLATE = utf8_bin COMMENT ='联盟通道表';
CREATE TABLE IF NOT EXISTS `baas_chain_node`
(
    `id`           BIGINT UNSIGNED NOT NULL DEFAULT 0 COMMENT 'ID',
    `modifiedTime` DATETIME        NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT 'Last Modified Time',
    `createdTime`  DATETIME        NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT 'Created Time',
    `nodeId`       BIGINT          NOT NULL DEFAULT 0 COMMENT '节点ID',
    `chainId`      VARCHAR(16)     NOT NULL DEFAULT '' COMMENT '链ID',
    `editor`       VARCHAR(32)     NOT NULL DEFAULT '' COMMENT 'Last editor',
    `creator`      VARCHAR(32)     NOT NULL DEFAULT '' COMMENT 'Creator',
    `orgDomain`    VARCHAR(32)     NOT NULL DEFAULT '' COMMENT '组织域名',
    PRIMARY KEY (`id`),
    KEY `idx_chain_id` (`chainId`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8
  COLLATE = utf8_bin COMMENT ='联盟节点关系表';
CREATE TABLE IF NOT EXISTS `baas_node`
(
    `id`             BIGINT UNSIGNED NOT NULL DEFAULT 0 COMMENT 'ID',
    `modifiedTime`   DATETIME        NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT 'Last Modified Time',
    `monitorK8s`     BIGINT          NOT NULL DEFAULT 0 COMMENT 'monitorK8s',
    `p2pK8s`         BIGINT          NOT NULL DEFAULT 0 COMMENT 'p2pK8s',
    `type`           INT             NOT NULL DEFAULT 0 COMMENT '节点类型',
    `p2pPort`        BIGINT          NOT NULL DEFAULT 0 COMMENT 'p2pPort',
    `createdTime`    DATETIME        NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT 'Created Time',
    `monitorPort`    BIGINT          NOT NULL DEFAULT 0 COMMENT 'monitorPort',
    `rpcK8s`         BIGINT          NOT NULL DEFAULT 0 COMMENT 'rpcK8s',
    `rpcPort`        BIGINT          NOT NULL DEFAULT 0 COMMENT 'rpcPort',
    `status`         INT             NOT NULL DEFAULT 0 COMMENT '节点状态',
    `enabled`        BIT(1)          NOT NULL DEFAULT 1 COMMENT '是否启用',
    `limitsMemory`   VARCHAR(8)      NOT NULL DEFAULT '' COMMENT 'limitsMemory',
    `requestsMemory` VARCHAR(8)      NOT NULL DEFAULT '' COMMENT 'requestsMemory',
    `requestsCpu`    VARCHAR(8)      NOT NULL DEFAULT '' COMMENT 'requestsCpu',
    `limitsCpu`      VARCHAR(8)      NOT NULL DEFAULT '' COMMENT 'limitsCpu',
    `ip`             VARCHAR(16)     NOT NULL DEFAULT '' COMMENT '节点IP',
    `editor`         VARCHAR(32)     NOT NULL DEFAULT '' COMMENT 'Last editor',
    `creator`        VARCHAR(32)     NOT NULL DEFAULT '' COMMENT 'Creator',
    `vmName`         VARCHAR(32)     NOT NULL DEFAULT '' COMMENT '虚拟机名称',
    `orgDomain`      VARCHAR(32)     NOT NULL DEFAULT '' COMMENT '组织域名',
    `pvcName`        VARCHAR(32)     NOT NULL DEFAULT '' COMMENT 'PVC名称',
    `name`           VARCHAR(64)     NOT NULL DEFAULT '' COMMENT '名称',
    `pinyin`         VARCHAR(255)    NOT NULL DEFAULT '' COMMENT '名称拼音',
    `extras`         VARCHAR(500)    NOT NULL DEFAULT '' COMMENT '扩展信息',
    `description`    VARCHAR(500)    NOT NULL DEFAULT '' COMMENT '备注',
    PRIMARY KEY (`id`),
    KEY `idx_org_domain` (`orgDomain`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8
  COLLATE = utf8_bin COMMENT ='联盟节点信息表';
CREATE TABLE IF NOT EXISTS `baas_contract`
(
    `id`           BIGINT UNSIGNED NOT NULL DEFAULT 0 COMMENT 'ID',
    `modifiedTime` DATETIME        NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT 'Last Modified Time',
    `runtimeType`  INT             NOT NULL DEFAULT 0 COMMENT '合约运行类型',
    `version`      INT             NOT NULL DEFAULT 0 COMMENT '合约版本',
    `createdTime`  DATETIME        NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT 'Created Time',
    `status`       INT             NOT NULL DEFAULT 0 COMMENT '合约状态',
    `enabled`      BIT(1)          NOT NULL DEFAULT 1 COMMENT '是否启用',
    `chainId`      VARCHAR(16)     NOT NULL DEFAULT '' COMMENT '所属联盟链',
    `editor`       VARCHAR(32)     NOT NULL DEFAULT '' COMMENT 'Last editor',
    `creator`      VARCHAR(32)     NOT NULL DEFAULT '' COMMENT 'Creator',
    `name`         VARCHAR(64)     NOT NULL DEFAULT '' COMMENT '名称',
    `pinyin`       VARCHAR(255)    NOT NULL DEFAULT '' COMMENT '名称拼音',
    `extras`       VARCHAR(500)    NOT NULL DEFAULT '' COMMENT '扩展信息',
    `description`  VARCHAR(500)    NOT NULL DEFAULT '' COMMENT '备注',
    PRIMARY KEY (`id`),
    KEY `idx_chain_id` (`chainId`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8
  COLLATE = utf8_bin COMMENT ='智能合约信息表';
CREATE TABLE IF NOT EXISTS `t_organization`
(
    `id`           BIGINT UNSIGNED NOT NULL DEFAULT 0 COMMENT 'ID',
    `modifiedTime` DATETIME        NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT 'Last Modified Time',
    `createdTime`  DATETIME        NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT 'Created Time',
    `enabled`      BIT(1)          NOT NULL DEFAULT 1 COMMENT '是否启用',
    `country`      VARCHAR(16)     NOT NULL DEFAULT '' COMMENT '组织所属国家',
    `locality`     VARCHAR(16)     NOT NULL DEFAULT '' COMMENT '组织所属省份区域',
    `province`     VARCHAR(16)     NOT NULL DEFAULT '' COMMENT '组织所属省份',
    `editor`       VARCHAR(32)     NOT NULL DEFAULT '' COMMENT 'Last editor',
    `creator`      VARCHAR(32)     NOT NULL DEFAULT '' COMMENT 'Creator',
    `domain`       VARCHAR(32)     NOT NULL DEFAULT '' COMMENT '组织域名',
    `tenantId`     VARCHAR(32)     NOT NULL DEFAULT '' COMMENT '租户ID',
    `name`         VARCHAR(64)     NOT NULL DEFAULT '' COMMENT '名称',
    `pinyin`       VARCHAR(255)    NOT NULL DEFAULT '' COMMENT '名称拼音',
    `extras`       VARCHAR(500)    NOT NULL DEFAULT '' COMMENT '扩展信息',
    `description`  VARCHAR(500)    NOT NULL DEFAULT '' COMMENT '备注',
    PRIMARY KEY (`id`),
    UNIQUE KEY `idx_organization_domain` (`domain`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8
  COLLATE = utf8_bin COMMENT ='组织信息表';
CREATE TABLE IF NOT EXISTS `t_permission`
(
    `id`           BIGINT UNSIGNED NOT NULL DEFAULT 0 COMMENT 'ID',
    `modifiedTime` DATETIME        NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT 'Last Modified Time',
    `createdTime`  DATETIME        NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT 'Created Time',
    `enabled`      BIT(1)          NOT NULL DEFAULT 1 COMMENT '是否启用',
    `editor`       VARCHAR(32)     NOT NULL DEFAULT '' COMMENT 'Last editor',
    `creator`      VARCHAR(32)     NOT NULL DEFAULT '' COMMENT 'Creator',
    `name`         VARCHAR(64)     NOT NULL DEFAULT '' COMMENT '名称',
    `pinyin`       VARCHAR(255)    NOT NULL DEFAULT '' COMMENT '名称拼音',
    `extras`       VARCHAR(500)    NOT NULL DEFAULT '' COMMENT '扩展信息',
    `description`  VARCHAR(500)    NOT NULL DEFAULT '' COMMENT '备注',
    PRIMARY KEY (`id`),
    KEY `idx_name` (`name`(32))
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8
  COLLATE = utf8_bin COMMENT ='权限表';
CREATE TABLE IF NOT EXISTS `t_role`
(
    `id`           BIGINT UNSIGNED NOT NULL DEFAULT 0 COMMENT 'ID',
    `modifiedTime` DATETIME        NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT 'Last Modified Time',
    `createdTime`  DATETIME        NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT 'Created Time',
    `enabled`      BIT(1)          NOT NULL DEFAULT 1 COMMENT '是否启用',
    `editor`       VARCHAR(32)     NOT NULL DEFAULT '' COMMENT 'Last editor',
    `creator`      VARCHAR(32)     NOT NULL DEFAULT '' COMMENT 'Creator',
    `name`         VARCHAR(64)     NOT NULL DEFAULT '' COMMENT '名称',
    `pinyin`       VARCHAR(255)    NOT NULL DEFAULT '' COMMENT '名称拼音',
    `extras`       VARCHAR(500)    NOT NULL DEFAULT '' COMMENT '扩展信息',
    `description`  VARCHAR(500)    NOT NULL DEFAULT '' COMMENT '备注',
    PRIMARY KEY (`id`),
    KEY `idx_name` (`name`(32))
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8
  COLLATE = utf8_bin COMMENT ='角色表';
CREATE TABLE IF NOT EXISTS `t_role_organization`
(
    `id`           BIGINT UNSIGNED NOT NULL DEFAULT 0 COMMENT 'ID',
    `modifiedTime` DATETIME        NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT 'Last Modified Time',
    `role`         BIGINT          NOT NULL DEFAULT 0,
    `organization` BIGINT          NOT NULL DEFAULT 0,
    `createdTime`  DATETIME        NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT 'Created Time',
    `editor`       VARCHAR(32)     NOT NULL DEFAULT '' COMMENT 'Last editor',
    `creator`      VARCHAR(32)     NOT NULL DEFAULT '' COMMENT 'Creator',
    PRIMARY KEY (`id`),
    KEY `idx_role` (`role`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8
  COLLATE = utf8_bin COMMENT ='角色组织关系表';
CREATE TABLE IF NOT EXISTS `t_role_permission`
(
    `id`           BIGINT UNSIGNED NOT NULL DEFAULT 0 COMMENT 'ID',
    `modifiedTime` DATETIME        NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT 'Last Modified Time',
    `role`         BIGINT          NOT NULL DEFAULT 0,
    `createdTime`  DATETIME        NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT 'Created Time',
    `permission`   BIGINT          NOT NULL DEFAULT 0,
    `editor`       VARCHAR(32)     NOT NULL DEFAULT '' COMMENT 'Last editor',
    `creator`      VARCHAR(32)     NOT NULL DEFAULT '' COMMENT 'Creator',
    PRIMARY KEY (`id`),
    KEY `idx_role` (`role`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8
  COLLATE = utf8_bin COMMENT ='角色权限关系表';
CREATE TABLE IF NOT EXISTS `t_role_resource`
(
    `id`           BIGINT UNSIGNED NOT NULL DEFAULT 0 COMMENT 'ID',
    `modifiedTime` DATETIME        NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT 'Last Modified Time',
    `role`         BIGINT          NOT NULL DEFAULT 0,
    `resource`     BIGINT          NOT NULL DEFAULT 0,
    `createdTime`  DATETIME        NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT 'Created Time',
    `editor`       VARCHAR(32)     NOT NULL DEFAULT '' COMMENT 'Last editor',
    `creator`      VARCHAR(32)     NOT NULL DEFAULT '' COMMENT 'Creator',
    PRIMARY KEY (`id`),
    KEY `idx_role` (`role`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8
  COLLATE = utf8_bin COMMENT ='角色资源关系表';
CREATE TABLE IF NOT EXISTS `t_resource`
(
    `id`           BIGINT UNSIGNED NOT NULL DEFAULT 0 COMMENT 'ID',
    `modifiedTime` DATETIME        NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT 'Last Modified Time',
    `parentId`     BIGINT          NOT NULL DEFAULT 0 COMMENT '父级资源ID',
    `createdTime`  DATETIME        NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT 'Created Time',
    `enabled`      BIT(1)          NOT NULL DEFAULT 1 COMMENT '是否启用',
    `editor`       VARCHAR(32)     NOT NULL DEFAULT '' COMMENT 'Last editor',
    `creator`      VARCHAR(32)     NOT NULL DEFAULT '' COMMENT 'Creator',
    `icon`         VARCHAR(32)     NOT NULL DEFAULT '' COMMENT '资源图标',
    `sort`         VARCHAR(32)     NOT NULL DEFAULT '' COMMENT '排序',
    `type`         VARCHAR(32)     NOT NULL DEFAULT '' COMMENT '资源类型（菜单、按钮等）',
    `url`          VARCHAR(32)     NOT NULL DEFAULT '' COMMENT '资源URL',
    `tenantId`     VARCHAR(32)     NOT NULL DEFAULT '' COMMENT '租户ID',
    `name`         VARCHAR(64)     NOT NULL DEFAULT '' COMMENT '名称',
    `pinyin`       VARCHAR(255)    NOT NULL DEFAULT '' COMMENT '名称拼音',
    `extras`       VARCHAR(500)    NOT NULL DEFAULT '' COMMENT '扩展信息',
    `description`  VARCHAR(500)    NOT NULL DEFAULT '' COMMENT '备注',
    PRIMARY KEY (`id`),
    KEY `idx_name` (`name`(32))
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8
  COLLATE = utf8_bin COMMENT ='系统资源表';
CREATE TABLE IF NOT EXISTS `t_user`
(
    `id`             BIGINT UNSIGNED NOT NULL DEFAULT 0 COMMENT 'ID',
    `modifiedTime`   DATETIME        NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT 'Last Modified Time',
    `type`           INT             NOT NULL DEFAULT 0 COMMENT '账号类型:企业;个人',
    `createdTime`    DATETIME        NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT 'Created Time',
    `expirationDate` DATETIME        NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT '证书失效时间',
    `status`         INT             NOT NULL DEFAULT 0 COMMENT '用户账号状态',
    `enabled`        BIT(1)          NOT NULL DEFAULT 1 COMMENT '是否启用',
    `administrator`  BIT(1)          NOT NULL DEFAULT b'0' COMMENT '是否管理员',
    `phone`          VARCHAR(16)     NOT NULL DEFAULT '' COMMENT '手机号',
    `editor`         VARCHAR(32)     NOT NULL DEFAULT '' COMMENT 'Last editor',
    `enrollmentId`   VARCHAR(32)     NOT NULL DEFAULT '' COMMENT '身份ID',
    `creator`        VARCHAR(32)     NOT NULL DEFAULT '' COMMENT 'Creator',
    `number`         VARCHAR(32)     NOT NULL DEFAULT '' COMMENT '工号',
    `tenantId`       VARCHAR(32)     NOT NULL DEFAULT '' COMMENT '租户ID',
    `email`          VARCHAR(32)     NOT NULL DEFAULT '' COMMENT '电子邮箱',
    `username`       VARCHAR(32)     NOT NULL DEFAULT '' COMMENT '用户名',
    `secretKey`      VARCHAR(64)     NOT NULL DEFAULT '' COMMENT '接口调用安全码',
    `name`           VARCHAR(64)     NOT NULL DEFAULT '' COMMENT '名称',
    `address`        VARCHAR(255)    NOT NULL DEFAULT '' COMMENT '联系地址',
    `password`       VARCHAR(255)    NOT NULL DEFAULT '' COMMENT '密码',
    `pinyin`         VARCHAR(255)    NOT NULL DEFAULT '' COMMENT '名称拼音',
    `extras`         VARCHAR(500)    NOT NULL DEFAULT '' COMMENT '扩展信息',
    `description`    VARCHAR(500)    NOT NULL DEFAULT '' COMMENT '备注',
    PRIMARY KEY (`id`),
    UNIQUE KEY `idx_username` (`username`(32))
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8
  COLLATE = utf8_bin COMMENT ='平台用户账号管理';
CREATE TABLE IF NOT EXISTS `t_user_organization`
(
    `id`           BIGINT UNSIGNED NOT NULL DEFAULT 0 COMMENT 'ID',
    `modifiedTime` DATETIME        NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT 'Last Modified Time',
    `organization` BIGINT          NOT NULL DEFAULT 0,
    `createdTime`  DATETIME        NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT 'Created Time',
    `user`         BIGINT          NOT NULL DEFAULT 0,
    `editor`       VARCHAR(32)     NOT NULL DEFAULT '' COMMENT 'Last editor',
    `creator`      VARCHAR(32)     NOT NULL DEFAULT '' COMMENT 'Creator',
    PRIMARY KEY (`id`),
    KEY `idx_user` (`user`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8
  COLLATE = utf8_bin COMMENT ='用户组织关系表';
CREATE TABLE IF NOT EXISTS `t_user_resource`
(
    `id`           BIGINT UNSIGNED NOT NULL DEFAULT 0 COMMENT 'ID',
    `modifiedTime` DATETIME        NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT 'Last Modified Time',
    `resource`     BIGINT          NOT NULL DEFAULT 0,
    `createdTime`  DATETIME        NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT 'Created Time',
    `user`         BIGINT          NOT NULL DEFAULT 0,
    `editor`       VARCHAR(32)     NOT NULL DEFAULT '' COMMENT 'Last editor',
    `creator`      VARCHAR(32)     NOT NULL DEFAULT '' COMMENT 'Creator',
    PRIMARY KEY (`id`),
    KEY `idx_user` (`user`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8
  COLLATE = utf8_bin COMMENT ='用户资源关系表';
CREATE TABLE IF NOT EXISTS `t_user_role`
(
    `id`           BIGINT UNSIGNED NOT NULL DEFAULT 0 COMMENT 'ID',
    `modifiedTime` DATETIME        NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT 'Last Modified Time',
    `role`         BIGINT          NOT NULL DEFAULT 0,
    `createdTime`  DATETIME        NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT 'Created Time',
    `user`         BIGINT          NOT NULL DEFAULT 0,
    `editor`       VARCHAR(32)     NOT NULL DEFAULT '' COMMENT 'Last editor',
    `creator`      VARCHAR(32)     NOT NULL DEFAULT '' COMMENT 'Creator',
    PRIMARY KEY (`id`),
    KEY `idx_user` (`user`),
    KEY `idx_role` (`role`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8
  COLLATE = utf8_bin COMMENT ='用户角色关系表';
CREATE TABLE IF NOT EXISTS `t_apk`
(
    `id`           BIGINT UNSIGNED NOT NULL DEFAULT 0 COMMENT 'ID',
    `modifiedTime` DATETIME        NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT 'Last Modified Time',
    `code`         BIGINT          NOT NULL DEFAULT 0 COMMENT '应用编码',
    `createdTime`  DATETIME        NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT 'Created Time',
    `forced`       BIT(1)          NOT NULL DEFAULT b'0' COMMENT '是否强制升级',
    `enabled`      BIT(1)          NOT NULL DEFAULT 1 COMMENT '是否启用',
    `editor`       VARCHAR(32)     NOT NULL DEFAULT '' COMMENT 'Last editor',
    `creator`      VARCHAR(32)     NOT NULL DEFAULT '' COMMENT 'Creator',
    `name`         VARCHAR(64)     NOT NULL DEFAULT '' COMMENT '名称',
    `pinyin`       VARCHAR(255)    NOT NULL DEFAULT '' COMMENT '名称拼音',
    `file`         VARCHAR(256)    NOT NULL DEFAULT '' COMMENT '应用路径',
    `pkgName`      VARCHAR(256)    NOT NULL DEFAULT '' COMMENT '应用包名',
    `extras`       VARCHAR(500)    NOT NULL DEFAULT '' COMMENT '扩展信息',
    `description`  VARCHAR(500)    NOT NULL DEFAULT '' COMMENT '备注',
    PRIMARY KEY (`id`),
    KEY `idx_code` (`code`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8
  COLLATE = utf8_bin COMMENT ='应用管理';
CREATE TABLE IF NOT EXISTS `t_app`
(
    `id`           BIGINT UNSIGNED NOT NULL DEFAULT 0 COMMENT 'ID',
    `modifiedTime` DATETIME        NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT 'Last Modified Time',
    `createdTime`  DATETIME        NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT 'Created Time',
    `enabled`      BIT(1)          NOT NULL DEFAULT 1 COMMENT '是否启用',
    `editor`       VARCHAR(32)     NOT NULL DEFAULT '' COMMENT 'Last editor',
    `creator`      VARCHAR(32)     NOT NULL DEFAULT '' COMMENT 'Creator',
    `type`         VARCHAR(32)     NOT NULL DEFAULT 'MOBILE' COMMENT '应用类型',
    `secretKey`    VARCHAR(64)     NOT NULL DEFAULT '' COMMENT '安全码',
    `name`         VARCHAR(64)     NOT NULL DEFAULT '' COMMENT '名称',
    `pinyin`       VARCHAR(255)    NOT NULL DEFAULT '' COMMENT '名称拼音',
    `extras`       VARCHAR(500)    NOT NULL DEFAULT '' COMMENT '扩展信息',
    `description`  VARCHAR(500)    NOT NULL DEFAULT '' COMMENT '备注',
    PRIMARY KEY (`id`),
    KEY `idx_name` (`name`(32))
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8
  COLLATE = utf8_bin COMMENT ='APP KEY';
CREATE TABLE IF NOT EXISTS `t_attribute`
(
    `id`           BIGINT UNSIGNED NOT NULL DEFAULT 0 COMMENT 'ID',
    `modifiedTime` DATETIME        NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT 'Last Modified Time',
    `createdTime`  DATETIME        NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT 'Created Time',
    `editor`       VARCHAR(32)     NOT NULL DEFAULT '' COMMENT 'Last editor',
    `creator`      VARCHAR(32)     NOT NULL DEFAULT '' COMMENT 'Creator',
    `name`         VARCHAR(32)     NOT NULL DEFAULT '' COMMENT '属性名称',
    `user`         VARCHAR(32)     NOT NULL DEFAULT '' COMMENT '所属用户',
    `group`        VARCHAR(32)     NOT NULL DEFAULT '' COMMENT '属性组',
    `description`  VARCHAR(255)    NOT NULL DEFAULT '' COMMENT '备注',
    `value`        VARCHAR(5000)   NOT NULL DEFAULT '' COMMENT '属性值',
    PRIMARY KEY (`id`),
    UNIQUE KEY `idx_name` (`group`, `name`, `user`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8
  COLLATE = utf8_bin COMMENT ='属性';
CREATE TABLE IF NOT EXISTS `t_file_meta`
(
    `id`           BIGINT UNSIGNED NOT NULL DEFAULT 0 COMMENT 'ID',
    `modifiedTime` DATETIME        NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT 'Last Modified Time',
    `size`         BIGINT          NOT NULL DEFAULT 0 COMMENT '文件字节数',
    `createdTime`  DATETIME        NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT 'Created Time',
    `nodeName`     VARCHAR(32)     NOT NULL DEFAULT '' COMMENT '文件所在节点名称',
    `editor`       VARCHAR(32)     NOT NULL DEFAULT '' COMMENT 'Last editor',
    `creator`      VARCHAR(32)     NOT NULL DEFAULT '' COMMENT 'Creator',
    `tag`          VARCHAR(32)     NOT NULL DEFAULT '' COMMENT '文件标签',
    `dir`          VARCHAR(32)     NOT NULL DEFAULT '' COMMENT '所在目录',
    `name`         VARCHAR(64)     NOT NULL DEFAULT '' COMMENT '文件名',
    `host`         VARCHAR(100)    NOT NULL DEFAULT '' COMMENT '文件所在主机IP地址',
    `mime`         VARCHAR(128)    NOT NULL DEFAULT '' COMMENT 'MIME类型',
    PRIMARY KEY (`id`),
    KEY `idx_name` (`name`(32)),
    KEY `idx_dir` (`dir`),
    KEY `idx_tag` (`tag`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8
  COLLATE = utf8_bin COMMENT ='访客记录';
CREATE TABLE IF NOT EXISTS `t_menu`
(
    `id`           BIGINT UNSIGNED NOT NULL DEFAULT 0 COMMENT 'ID',
    `modifiedTime` DATETIME        NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT 'Last Modified Time',
    `parentId`     BIGINT          NOT NULL DEFAULT 0,
    `createdTime`  DATETIME        NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT 'Created Time',
    `order`        DOUBLE(10, 5)   NOT NULL DEFAULT 0 COMMENT '菜单顺序',
    `editor`       VARCHAR(32)     NOT NULL DEFAULT '' COMMENT 'Last editor',
    `creator`      VARCHAR(32)     NOT NULL DEFAULT '' COMMENT 'Creator',
    `icon`         VARCHAR(32)     NOT NULL DEFAULT '',
    `type`         VARCHAR(32)     NOT NULL DEFAULT 'LINK',
    `target`       VARCHAR(32)     NOT NULL DEFAULT '',
    `name`         VARCHAR(32)     NOT NULL DEFAULT '',
    `href`         VARCHAR(200)    NOT NULL DEFAULT '',
    `description`  VARCHAR(500)    NOT NULL DEFAULT '' COMMENT '备注',
    PRIMARY KEY (`id`),
    KEY `idx_name` (`name`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8
  COLLATE = utf8_bin COMMENT ='菜单';
CREATE TABLE IF NOT EXISTS `t_menu_privilege`
(
    `id`           BIGINT UNSIGNED NOT NULL DEFAULT 0 COMMENT 'ID',
    `modifiedTime` DATETIME        NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT 'Last Modified Time',
    `createdTime`  DATETIME        NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT 'Created Time',
    `privilege`    BIGINT          NOT NULL DEFAULT 0,
    `menu`         BIGINT          NOT NULL DEFAULT 0,
    `editor`       VARCHAR(32)     NOT NULL DEFAULT '' COMMENT 'Last editor',
    `creator`      VARCHAR(32)     NOT NULL DEFAULT '' COMMENT 'Creator',
    PRIMARY KEY (`id`),
    KEY `idx_role` (`menu`),
    KEY `idx_privilege` (`privilege`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8
  COLLATE = utf8_bin COMMENT ='菜单权限表';
CREATE TABLE IF NOT EXISTS `t_privilege`
(
    `id`           BIGINT UNSIGNED NOT NULL DEFAULT 0 COMMENT 'ID',
    `modifiedTime` DATETIME        NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT 'Last Modified Time',
    `createdTime`  DATETIME        NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT 'Created Time',
    `editor`       VARCHAR(32)     NOT NULL DEFAULT '' COMMENT 'Last editor',
    `creator`      VARCHAR(32)     NOT NULL DEFAULT '' COMMENT 'Creator',
    `name`         VARCHAR(32)     NOT NULL DEFAULT '' COMMENT '名称',
    `method`       VARCHAR(100)    NOT NULL DEFAULT '' COMMENT '方法',
    `description`  VARCHAR(500)    NOT NULL DEFAULT '' COMMENT '备注',
    `dependencies` VARCHAR(1000)   NOT NULL DEFAULT '' COMMENT '依赖',
    PRIMARY KEY (`id`),
    KEY `idx_name` (`name`),
    KEY `idx_method` (`method`(32))
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8
  COLLATE = utf8_bin COMMENT ='权限';
CREATE TABLE IF NOT EXISTS `t_region`
(
    `id`           BIGINT UNSIGNED NOT NULL DEFAULT 0 COMMENT 'ID',
    `modifiedTime` DATETIME        NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT 'Last Modified Time',
    `parent`       BIGINT          NOT NULL DEFAULT 0,
    `level`        INT             NOT NULL DEFAULT 0,
    `latitude`     DOUBLE(10, 7)   NOT NULL DEFAULT 0,
    `createdTime`  DATETIME        NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT 'Created Time',
    `longitude`    DOUBLE(10, 7)   NOT NULL DEFAULT 0,
    `postcode`     VARCHAR(10)     NOT NULL DEFAULT '',
    `number`       VARCHAR(10)     NOT NULL DEFAULT '',
    `editor`       VARCHAR(32)     NOT NULL DEFAULT '' COMMENT 'Last editor',
    `creator`      VARCHAR(32)     NOT NULL DEFAULT '' COMMENT 'Creator',
    `name`         VARCHAR(32)     NOT NULL DEFAULT '',
    `shortName`    VARCHAR(32)     NOT NULL DEFAULT '',
    `longName`     VARCHAR(255)    NOT NULL DEFAULT '',
    `pinyin`       VARCHAR(500)    NOT NULL DEFAULT '',
    PRIMARY KEY (`id`),
    KEY `idx_name` (`name`),
    KEY `idx_pinyin` (`pinyin`(32))
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8
  COLLATE = utf8_bin COMMENT ='地区';
CREATE TABLE IF NOT EXISTS `t_role_menu`
(
    `id`           BIGINT UNSIGNED NOT NULL DEFAULT 0 COMMENT 'ID',
    `modifiedTime` DATETIME        NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT 'Last Modified Time',
    `role`         BIGINT          NOT NULL DEFAULT 0,
    `createdTime`  DATETIME        NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT 'Created Time',
    `menu`         BIGINT          NOT NULL DEFAULT 0,
    `editor`       VARCHAR(32)     NOT NULL DEFAULT '' COMMENT 'Last editor',
    `creator`      VARCHAR(32)     NOT NULL DEFAULT '' COMMENT 'Creator',
    PRIMARY KEY (`id`),
    KEY `idx_role` (`role`),
    KEY `idx_menu` (`menu`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8
  COLLATE = utf8_bin COMMENT ='角色权限表';
CREATE TABLE IF NOT EXISTS `t_role_privilege`
(
    `id`           BIGINT UNSIGNED NOT NULL DEFAULT 0 COMMENT 'ID',
    `modifiedTime` DATETIME        NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT 'Last Modified Time',
    `role`         BIGINT          NOT NULL DEFAULT 0,
    `createdTime`  DATETIME        NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT 'Created Time',
    `privilege`    BIGINT          NOT NULL DEFAULT 0,
    `editor`       VARCHAR(32)     NOT NULL DEFAULT '' COMMENT 'Last editor',
    `creator`      VARCHAR(32)     NOT NULL DEFAULT '' COMMENT 'Creator',
    PRIMARY KEY (`id`),
    KEY `idx_role` (`role`),
    KEY `idx_privilege` (`privilege`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8
  COLLATE = utf8_bin COMMENT ='角色权限表';