-- PivotHub 系统库初始化脚本
-- 对应 pivot-hub-pojo/src/main/java/com/pivothub/pojo/po/system

CREATE TABLE IF NOT EXISTS sys_config (
    uuid           VARCHAR(32)  NOT NULL PRIMARY KEY COMMENT '主键',
    config_key     VARCHAR(100) NOT NULL COMMENT '配置键',
    config_value   TEXT         NULL COMMENT '配置值',
    config_name    VARCHAR(100) NULL COMMENT '配置名称',
    config_type    TINYINT      NULL COMMENT '类型 1-文本 2-JSON 3-加密',
    status         TINYINT      NOT NULL DEFAULT 1 COMMENT '状态 0-禁用 1-正常',
    remark         VARCHAR(500) NULL COMMENT '备注',
    create_user_id VARCHAR(32)  NULL COMMENT '创建人uuid',
    update_user_id VARCHAR(32)  NULL COMMENT '更新人uuid',
    create_time    DATETIME     NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time    DATETIME     NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    UNIQUE KEY uk_sys_config_key (config_key)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统配置表';

CREATE TABLE IF NOT EXISTS sys_file (
    uuid           VARCHAR(32)  NOT NULL PRIMARY KEY COMMENT '主键',
    file_name      VARCHAR(200) NOT NULL COMMENT '原始文件名',
    file_path      VARCHAR(500) NULL COMMENT '文件存储路径',
    url            VARCHAR(500) NULL COMMENT '文件URL',
    file_size      BIGINT       NULL COMMENT '文件大小（字节）',
    file_type      VARCHAR(100) NULL COMMENT '文件类型（MIME）',
    extension      VARCHAR(20)  NULL COMMENT '文件扩展名',
    module         VARCHAR(50)  NULL COMMENT '所属业务模块',
    uploader_id    VARCHAR(32)  NULL COMMENT '上传者uuid',
    uploader_name  VARCHAR(50)  NULL COMMENT '上传者名称',
    status         TINYINT      NOT NULL DEFAULT 1 COMMENT '状态 0-删除 1-正常',
    create_user_id VARCHAR(32)  NULL COMMENT '创建人uuid',
    update_user_id VARCHAR(32)  NULL COMMENT '更新人uuid',
    create_time    DATETIME     NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time    DATETIME     NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    KEY idx_sys_file_module (module),
    KEY idx_sys_file_uploader (uploader_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='文件记录表';

CREATE TABLE IF NOT EXISTS sys_mail_code (
    uuid           VARCHAR(32)  NOT NULL PRIMARY KEY COMMENT '主键',
    email          VARCHAR(100) NOT NULL COMMENT '邮箱地址',
    code           VARCHAR(10)  NOT NULL COMMENT '验证码',
    scene          VARCHAR(20)  NULL COMMENT '用途 register/login/reset',
    used           TINYINT      NOT NULL DEFAULT 0 COMMENT '是否已使用 0-未使用 1-已使用',
    expire_time    DATETIME     NOT NULL COMMENT '过期时间',
    create_user_id VARCHAR(32)  NULL COMMENT '创建人uuid',
    update_user_id VARCHAR(32)  NULL COMMENT '更新人uuid',
    create_time    DATETIME     NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time    DATETIME     NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    KEY idx_sys_mail_code_email (email),
    KEY idx_sys_mail_code_expire_time (expire_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='邮箱验证码表';

CREATE TABLE IF NOT EXISTS sys_menu (
    uuid           VARCHAR(32)  NOT NULL PRIMARY KEY COMMENT '主键',
    code           VARCHAR(64)  NULL COMMENT '菜单编码',
    menu_name      VARCHAR(64)  NOT NULL COMMENT '菜单名称',
    parent_id      VARCHAR(32)  NULL DEFAULT '0' COMMENT '父菜单uuid，0为顶级',
    module_id      VARCHAR(32)  NULL COMMENT '模块uuid',
    web_url        VARCHAR(256) NULL COMMENT 'Web端路由路径',
    app_url        VARCHAR(256) NULL COMMENT 'App端路由路径',
    icon           VARCHAR(128) NULL COMMENT '图标',
    sort           INT          NOT NULL DEFAULT 0 COMMENT '排序',
    status         TINYINT      NOT NULL DEFAULT 1 COMMENT '状态 0-禁用 1-正常',
    create_user_id VARCHAR(32)  NULL COMMENT '创建人uuid',
    update_user_id VARCHAR(32)  NULL COMMENT '更新人uuid',
    create_time    DATETIME     NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time    DATETIME     NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    KEY idx_sys_menu_parent_id (parent_id),
    KEY idx_sys_menu_module_id (module_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统菜单表';

CREATE TABLE IF NOT EXISTS sys_operation_log (
    uuid            VARCHAR(32)  NOT NULL PRIMARY KEY COMMENT '主键',
    user_id         VARCHAR(32)  NULL COMMENT '用户uuid',
    username        VARCHAR(50)  NULL COMMENT '用户名',
    title           VARCHAR(100) NULL COMMENT '操作模块',
    method          VARCHAR(50)  NULL COMMENT '操作类型',
    request_method  VARCHAR(20)  NULL COMMENT '请求方法名',
    request_url     VARCHAR(200) NULL COMMENT '请求URL',
    request_params  TEXT         NULL COMMENT '请求参数JSON',
    response_result TEXT         NULL COMMENT '响应结果JSON',
    cost_time       BIGINT       NULL COMMENT '消耗时间（毫秒）',
    ip              VARCHAR(50)  NULL COMMENT '操作IP',
    location        VARCHAR(100) NULL COMMENT 'IP属地',
    browser         VARCHAR(50)  NULL COMMENT '浏览器',
    os              VARCHAR(50)  NULL COMMENT '操作系统',
    status          TINYINT      NOT NULL DEFAULT 1 COMMENT '状态 0-失败 1-成功',
    error_msg       TEXT         NULL COMMENT '异常信息',
    create_user_id  VARCHAR(32)  NULL COMMENT '创建人uuid',
    update_user_id  VARCHAR(32)  NULL COMMENT '更新人uuid',
    create_time     DATETIME     NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time     DATETIME     NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    KEY idx_sys_operation_log_user_id (user_id),
    KEY idx_sys_operation_log_create_time (create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='操作日志表';

CREATE TABLE IF NOT EXISTS sys_permission (
    uuid           VARCHAR(32)  NOT NULL PRIMARY KEY COMMENT '主键',
    name           VARCHAR(100) NULL COMMENT '权限名称',
    menu_id        VARCHAR(32)  NOT NULL COMMENT '菜单uuid',
    role_id        VARCHAR(32)  NULL COMMENT '角色uuid',
    user_id        VARCHAR(32)  NULL COMMENT '用户uuid',
    person_type    TINYINT      NOT NULL COMMENT '归属类型 1-角色 2-用户',
    status         TINYINT      NOT NULL DEFAULT 1 COMMENT '状态 0-禁用 1-正常',
    create_user_id VARCHAR(32)  NULL COMMENT '创建人uuid',
    update_user_id VARCHAR(32)  NULL COMMENT '更新人uuid',
    create_time    DATETIME     NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time    DATETIME     NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    KEY idx_sys_permission_menu_id (menu_id),
    KEY idx_sys_permission_role_id (role_id),
    KEY idx_sys_permission_user_id (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统权限表';

CREATE TABLE IF NOT EXISTS sys_qq_info (
    uuid           VARCHAR(32)  NOT NULL PRIMARY KEY COMMENT '主键',
    openid         VARCHAR(128) NOT NULL COMMENT 'QQ openid',
    gender         VARCHAR(8)   NULL COMMENT '性别',
    gender_type    TINYINT      NULL COMMENT '性别类型',
    nickname       VARCHAR(128) NULL COMMENT '昵称',
    figureurl2     VARCHAR(512) NULL COMMENT '头像大图',
    figureurl1     VARCHAR(512) NULL COMMENT '头像中图',
    figureurl      VARCHAR(512) NULL COMMENT '头像',
    figureurl_qq   VARCHAR(512) NULL COMMENT 'QQ头像',
    figureurl_qq1  VARCHAR(512) NULL COMMENT 'QQ头像1',
    figureurl_qq2  VARCHAR(512) NULL COMMENT 'QQ头像2',
    status         TINYINT      NOT NULL DEFAULT 1 COMMENT '状态 0-弃用 1-启用',
    create_user_id VARCHAR(32)  NULL COMMENT '创建人uuid',
    update_user_id VARCHAR(32)  NULL COMMENT '更新人uuid',
    create_time    DATETIME     NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time    DATETIME     NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    UNIQUE KEY uk_sys_qq_info_openid (openid)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='QQ用户信息表';

CREATE TABLE IF NOT EXISTS sys_role (
    uuid           VARCHAR(32)  NOT NULL PRIMARY KEY COMMENT '主键',
    code           VARCHAR(64)  NULL COMMENT '角色编码',
    name           VARCHAR(64)  NOT NULL COMMENT '角色名称',
    defa           TINYINT      NOT NULL DEFAULT 0 COMMENT '是否默认角色 0-否 1-是',
    sort           INT          NOT NULL DEFAULT 0 COMMENT '排序',
    status         TINYINT      NOT NULL DEFAULT 1 COMMENT '状态 0-禁用 1-正常',
    remark         VARCHAR(500) NULL COMMENT '备注',
    create_user_id VARCHAR(32)  NULL COMMENT '创建人uuid',
    update_user_id VARCHAR(32)  NULL COMMENT '更新人uuid',
    create_time    DATETIME     NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time    DATETIME     NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    UNIQUE KEY uk_sys_role_code (code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统角色表';

CREATE TABLE IF NOT EXISTS sys_user (
    uuid           VARCHAR(32)  NOT NULL PRIMARY KEY COMMENT '主键',
    code           VARCHAR(64)  NULL COMMENT '用户编码',
    nickname       VARCHAR(64)  NULL COMMENT '昵称',
    email          VARCHAR(128) NULL COMMENT '邮箱',
    avatar         VARCHAR(512) NULL COMMENT '头像URL',
    role_id        VARCHAR(32)  NULL COMMENT '角色uuid',
    status         TINYINT      NOT NULL DEFAULT 1 COMMENT '状态 0-禁用 1-正常',
    remark         VARCHAR(500) NULL COMMENT '备注',
    create_user_id VARCHAR(32)  NULL COMMENT '创建人uuid',
    update_user_id VARCHAR(32)  NULL COMMENT '更新人uuid',
    create_time    DATETIME     NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time    DATETIME     NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    UNIQUE KEY uk_sys_user_code (code),
    UNIQUE KEY uk_sys_user_email (email),
    KEY idx_sys_user_role_id (role_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统用户表';

CREATE TABLE IF NOT EXISTS sys_user_platform (
    uuid           VARCHAR(32)  NOT NULL PRIMARY KEY COMMENT '主键',
    user_id        VARCHAR(32)  NOT NULL COMMENT '用户uuid',
    qq_open_id     VARCHAR(128) NULL COMMENT 'QQ开放平台openid',
    qq_status      TINYINT      NOT NULL DEFAULT 0 COMMENT 'QQ绑定状态 0-未绑定 1-已绑定',
    wx_open_id     VARCHAR(128) NULL COMMENT '微信开放平台openid',
    wx_status      TINYINT      NOT NULL DEFAULT 0 COMMENT '微信绑定状态 0-未绑定 1-已绑定',
    mall_aes       VARCHAR(256) NULL COMMENT 'Mall AES加密key',
    mall_status    TINYINT      NOT NULL DEFAULT 0 COMMENT 'Mall状态 0-禁用 1-正常',
    create_user_id VARCHAR(32)  NULL COMMENT '创建人uuid',
    update_user_id VARCHAR(32)  NULL COMMENT '更新人uuid',
    create_time    DATETIME     NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time    DATETIME     NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    UNIQUE KEY uk_sys_user_platform_user_id (user_id),
    UNIQUE KEY uk_sys_user_platform_qq_open_id (qq_open_id),
    UNIQUE KEY uk_sys_user_platform_wx_open_id (wx_open_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户平台账户表';

CREATE TABLE IF NOT EXISTS sys_user_role (
    uuid           VARCHAR(32) NOT NULL PRIMARY KEY COMMENT '主键',
    user_id        VARCHAR(32) NOT NULL COMMENT '用户uuid',
    role_id        VARCHAR(32) NOT NULL COMMENT '角色uuid',
    create_user_id VARCHAR(32) NULL COMMENT '创建人uuid',
    update_user_id VARCHAR(32) NULL COMMENT '更新人uuid',
    create_time    DATETIME    NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time    DATETIME    NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    UNIQUE KEY uk_sys_user_role_user_role (user_id, role_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户角色关联表';
