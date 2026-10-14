-- PivotHub 业务库初始化脚本
-- 对应 pivot-hub-pojo/src/main/java/com/pivothub/pojo/po/business

CREATE TABLE IF NOT EXISTS community_post (
    uuid           VARCHAR(32)  NOT NULL PRIMARY KEY COMMENT '主键',
    author_id      VARCHAR(32)  NOT NULL COMMENT '发布用户uuid',
    title          VARCHAR(200) NOT NULL COMMENT '帖子标题',
    content        TEXT         NULL COMMENT '帖子正文',
    post_type      TINYINT      NULL COMMENT '帖子类型',
    view_count     INT          NOT NULL DEFAULT 0 COMMENT '浏览数',
    comment_count  INT          NOT NULL DEFAULT 0 COMMENT '评论数',
    like_count     INT          NOT NULL DEFAULT 0 COMMENT '点赞数',
    favorite_count INT          NOT NULL DEFAULT 0 COMMENT '收藏数',
    status         TINYINT      NOT NULL DEFAULT 1 COMMENT '状态 0-删除/隐藏 1-正常',
    remark         VARCHAR(500) NULL COMMENT '备注',
    create_user_id VARCHAR(32)  NULL COMMENT '创建人uuid',
    update_user_id VARCHAR(32)  NULL COMMENT '更新人uuid',
    create_time    DATETIME     NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time    DATETIME     NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    KEY idx_community_post_author_id (author_id),
    KEY idx_community_post_status_create_time (status, create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='社区帖子表';

CREATE TABLE IF NOT EXISTS community_comment (
    uuid             VARCHAR(32)  NOT NULL PRIMARY KEY COMMENT '主键',
    post_id          VARCHAR(32)  NOT NULL COMMENT '所属帖子uuid',
    parent_id        VARCHAR(32)  NULL DEFAULT '0' COMMENT '父评论uuid，0为顶级评论',
    root_id          VARCHAR(32)  NULL DEFAULT '0' COMMENT '根评论uuid',
    author_id        VARCHAR(32)  NOT NULL COMMENT '评论用户uuid',
    reply_to_user_id VARCHAR(32)  NULL COMMENT '被回复用户uuid',
    content          TEXT         NOT NULL COMMENT '评论内容',
    like_count       INT          NOT NULL DEFAULT 0 COMMENT '点赞数',
    status           TINYINT      NOT NULL DEFAULT 1 COMMENT '状态 0-删除 1-正常',
    remark           VARCHAR(500) NULL COMMENT '备注',
    create_user_id   VARCHAR(32)  NULL COMMENT '创建人uuid',
    update_user_id   VARCHAR(32)  NULL COMMENT '更新人uuid',
    create_time      DATETIME     NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time      DATETIME     NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    KEY idx_community_comment_post_id (post_id),
    KEY idx_community_comment_parent_id (parent_id),
    KEY idx_community_comment_root_id (root_id),
    KEY idx_community_comment_author_id (author_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='社区评论表';

CREATE TABLE IF NOT EXISTS community_post_image (
    uuid           VARCHAR(32) NOT NULL PRIMARY KEY COMMENT '主键',
    post_id        VARCHAR(32) NOT NULL COMMENT '帖子uuid',
    file_id        VARCHAR(32) NOT NULL COMMENT '文件uuid',
    sort           INT         NOT NULL DEFAULT 0 COMMENT '图片排序',
    status         TINYINT     NOT NULL DEFAULT 1 COMMENT '状态 0-删除 1-正常',
    create_user_id VARCHAR(32) NULL COMMENT '创建人uuid',
    update_user_id VARCHAR(32) NULL COMMENT '更新人uuid',
    create_time    DATETIME    NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time    DATETIME    NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    KEY idx_community_post_image_post_id (post_id),
    KEY idx_community_post_image_file_id (file_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='社区帖子图片表';

CREATE TABLE IF NOT EXISTS community_comment_image (
    uuid           VARCHAR(32) NOT NULL PRIMARY KEY COMMENT '主键',
    comment_id     VARCHAR(32) NOT NULL COMMENT '评论uuid',
    file_id        VARCHAR(32) NOT NULL COMMENT '文件uuid',
    sort           INT         NOT NULL DEFAULT 0 COMMENT '图片排序',
    status         TINYINT     NOT NULL DEFAULT 1 COMMENT '状态 0-删除 1-正常',
    create_user_id VARCHAR(32) NULL COMMENT '创建人uuid',
    update_user_id VARCHAR(32) NULL COMMENT '更新人uuid',
    create_time    DATETIME    NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time    DATETIME    NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    KEY idx_community_comment_image_comment_id (comment_id),
    KEY idx_community_comment_image_file_id (file_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='社区评论图片表';

CREATE TABLE IF NOT EXISTS community_post_like (
    uuid           VARCHAR(32) NOT NULL PRIMARY KEY COMMENT '主键',
    post_id        VARCHAR(32) NOT NULL COMMENT '帖子uuid',
    user_id        VARCHAR(32) NOT NULL COMMENT '点赞用户uuid',
    status         TINYINT     NOT NULL DEFAULT 1 COMMENT '状态 0-取消点赞 1-已点赞',
    create_user_id VARCHAR(32) NULL COMMENT '创建人uuid',
    update_user_id VARCHAR(32) NULL COMMENT '更新人uuid',
    create_time    DATETIME    NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time    DATETIME    NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    UNIQUE KEY uk_community_post_like_post_user (post_id, user_id),
    KEY idx_community_post_like_user_id (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='社区帖子点赞表';

CREATE TABLE IF NOT EXISTS community_comment_like (
    uuid           VARCHAR(32) NOT NULL PRIMARY KEY COMMENT '主键',
    comment_id     VARCHAR(32) NOT NULL COMMENT '评论uuid',
    user_id        VARCHAR(32) NOT NULL COMMENT '点赞用户uuid',
    status         TINYINT     NOT NULL DEFAULT 1 COMMENT '状态 0-取消点赞 1-已点赞',
    create_user_id VARCHAR(32) NULL COMMENT '创建人uuid',
    update_user_id VARCHAR(32) NULL COMMENT '更新人uuid',
    create_time    DATETIME    NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time    DATETIME    NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    UNIQUE KEY uk_community_comment_like_comment_user (comment_id, user_id),
    KEY idx_community_comment_like_user_id (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='社区评论点赞表';

CREATE TABLE IF NOT EXISTS community_post_favorite (
    uuid           VARCHAR(32) NOT NULL PRIMARY KEY COMMENT '主键',
    post_id        VARCHAR(32) NOT NULL COMMENT '帖子uuid',
    user_id        VARCHAR(32) NOT NULL COMMENT '收藏用户uuid',
    status         TINYINT     NOT NULL DEFAULT 1 COMMENT '状态 0-取消收藏 1-已收藏',
    create_user_id VARCHAR(32) NULL COMMENT '创建人uuid',
    update_user_id VARCHAR(32) NULL COMMENT '更新人uuid',
    create_time    DATETIME    NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time    DATETIME    NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    UNIQUE KEY uk_community_post_favorite_post_user (post_id, user_id),
    KEY idx_community_post_favorite_user_id (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='社区帖子收藏表';

CREATE TABLE IF NOT EXISTS map_theme (
  uuid VARCHAR(32) NOT NULL PRIMARY KEY,
  owner_user_id VARCHAR(32) NOT NULL,
  name VARCHAR(100) NOT NULL,
  description VARCHAR(500) NULL,
  center_longitude DECIMAL(10,6) NULL,
  center_latitude DECIMAL(9,6) NULL,
  default_zoom INT NOT NULL DEFAULT 14,
  sort INT NOT NULL DEFAULT 0,
  status TINYINT NOT NULL DEFAULT 1,
  create_user_id VARCHAR(32) NULL, update_user_id VARCHAR(32) NULL,
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
  update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  KEY idx_map_theme_owner_status_sort(owner_user_id,status,sort)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='私有地图主题';

CREATE TABLE IF NOT EXISTS map_icon (
  uuid VARCHAR(32) NOT NULL PRIMARY KEY,
  owner_user_id VARCHAR(32) NOT NULL,
  name VARCHAR(100) NOT NULL,
  object_key VARCHAR(512) NOT NULL,
  mime_type VARCHAR(50) NOT NULL,
  file_size BIGINT NOT NULL,
  width INT NOT NULL DEFAULT 32, height INT NOT NULL DEFAULT 40,
  anchor VARCHAR(32) NOT NULL DEFAULT 'bottom-center',
  status TINYINT NOT NULL DEFAULT 1,
  create_user_id VARCHAR(32) NULL, update_user_id VARCHAR(32) NULL,
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
  update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  KEY idx_map_icon_owner_status(owner_user_id,status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户私有地图图标素材';

CREATE TABLE IF NOT EXISTS map_category (
  uuid VARCHAR(32) NOT NULL PRIMARY KEY,
  owner_user_id VARCHAR(32) NOT NULL,
  theme_id VARCHAR(32) NOT NULL,
  name VARCHAR(64) NOT NULL,
  icon_id VARCHAR(32) NULL,
  sort INT NOT NULL DEFAULT 0,
  status TINYINT NOT NULL DEFAULT 1,
  create_user_id VARCHAR(32) NULL, update_user_id VARCHAR(32) NULL,
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
  update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  KEY idx_map_category_theme_owner_status(theme_id,owner_user_id,status),
  KEY idx_map_category_icon(icon_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='主题地点分类';

CREATE TABLE IF NOT EXISTS map_point (
  uuid VARCHAR(32) NOT NULL PRIMARY KEY,
  owner_user_id VARCHAR(32) NOT NULL,
  theme_id VARCHAR(32) NOT NULL,
  category_id VARCHAR(32) NULL,
  name VARCHAR(200) NOT NULL,
  address VARCHAR(500) NULL,
  longitude DECIMAL(10,6) NOT NULL, latitude DECIMAL(9,6) NOT NULL,
  note TEXT NULL,
  source TINYINT NOT NULL DEFAULT 0,
  amap_poi_id VARCHAR(100) NULL,
  status TINYINT NOT NULL DEFAULT 1,
  create_user_id VARCHAR(32) NULL, update_user_id VARCHAR(32) NULL,
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
  update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  KEY idx_map_point_theme_owner_status(theme_id,owner_user_id,status),
  KEY idx_map_point_theme_category(theme_id,category_id,status),
  KEY idx_map_point_amap_poi(theme_id,amap_poi_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='主题地点及文字笔记';
