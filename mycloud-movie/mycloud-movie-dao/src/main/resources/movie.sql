DROP DATABASE IF EXISTS mycloud_movie;
CREATE DATABASE mycloud_movie;
USE mycloud_movie;

DROP TABLE IF EXISTS movie;
CREATE TABLE movie (
  id          BIGINT       NOT NULL         AUTO_INCREMENT
  COMMENT '电影id',
  name        VARCHAR(100) NOT NULL
  COMMENT '电影名',
  price       DECIMAL(4, 2)                 DEFAULT 0
  COMMENT '电影价格',
  area_id     INT                           DEFAULT NULL
  COMMENT '电影地区',
  cover       VARCHAR(255)                  DEFAULT NULL
  COMMENT '电影封面图',
  play_count  BIGINT       NOT NULL         DEFAULT 0
  COMMENT '统计播放次数',
  show_year   TIMESTAMP    NOT NULL         DEFAULT '2018-01-01'
  COMMENT '电影上映年代',
  create_time TIMESTAMP    NOT NULL         DEFAULT CURRENT_TIMESTAMP
  COMMENT '创建、上传时间',
  PRIMARY KEY (id),
  KEY IDX_MOVIE_MOVIE_NAME (name)
)
  ENGINE = InnoDB
  DEFAULT CHARSET = utf8
  COMMENT '电影表';

DROP TABLE IF EXISTS movie_hot;
CREATE TABLE movie_hot (
  id   BIGINT NOT NULL
  COMMENT '电影id',
  sort INT    NOT NULL DEFAULT 0
  COMMENT '排序号',
  PRIMARY KEY (id)
)
  ENGINE = InnoDB
  DEFAULT CHARSET = utf8
  COMMENT '最热电影表，零时更新？(到时候迁移到redis)';

DROP TABLE IF EXISTS movie_detail;
CREATE TABLE movie_detail (
  id          BIGINT       NOT NULL AUTO_INCREMENT
  COMMENT '详细内容id',
  movie_id    BIGINT       NOT NULL
  COMMENT '电影id',
  director    VARCHAR(100) NOT NULL DEFAULT ''
  COMMENT '导演',
  actors      VARCHAR(255) NOT NULL DEFAULT ''
  COMMENT '电影主演',
  description TEXT         NOT NULL
  COMMENT '电影描述',
  keyword     VARCHAR(255) NOT NULL DEFAULT ''
  COMMENT '电影关键字',
  PRIMARY KEY (id),
  KEY IDX_MOVIE_DETAIL_KEYWORD (keyword)
)
  ENGINE = InnoDB
  DEFAULT CHARSET = utf8
  COMMENT '电影消息内容表';

DROP TABLE IF EXISTS movie_comment;
CREATE TABLE movie_comment (
  id          BIGINT    NOT NULL AUTO_INCREMENT
  COMMENT '评论id',
  movie_id    BIGINT    NOT NULL
  COMMENT '电影ID',
  content     TEXT      NOT NULL
  COMMENT '评论内容',
  user_id     BIGINT    NOT NULL DEFAULT 0
  COMMENT '评论者id',
  create_time TIMESTAMP NOT NULL DEFAULT current_timestamp
  COMMENT '评论时间',
  PRIMARY KEY (id)
)
  ENGINE = InnoDB
  DEFAULT CHARSET = utf8
  COMMENT '电影评论表';

DROP TABLE IF EXISTS kind;
CREATE TABLE kind (
  id   INT         NOT NULL AUTO_INCREMENT
  COMMENT '类型id',
  name VARCHAR(50) NOT NULL
  COMMENT '类型名',
  sort INT         NOT NULL DEFAULT 0
  COMMENT '排序号',
  PRIMARY KEY (id)
)
  ENGINE = InnoDB
  DEFAULT CHARSET = utf8
  COMMENT '电影类别表';


DROP TABLE IF EXISTS movie_kind;
CREATE TABLE movie_kind (
  id       BIGINT AUTO_INCREMENT
  COMMENT 'id',
  movie_id BIGINT NOT NULL
  COMMENT '电影id',
  kind_id  INT    NOT NULL
  COMMENT '类型id',
  PRIMARY KEY (id)
)
  ENGINE = InnoDB
  DEFAULT CHARSET = utf8
  COMMENT '电影类别关联表';

DROP TABLE IF EXISTS area;
CREATE TABLE area (
  id   INT         NOT NULL AUTO_INCREMENT
  COMMENT '地区id',
  name VARCHAR(50) NOT NULL
  COMMENT '地区名',
  sort INT         NOT NULL DEFAULT 0
  COMMENT '排序号',
  PRIMARY KEY (id)
)
  ENGINE = InnoDB
  DEFAULT CHARSET = utf8
  COMMENT '电影地区表';

DROP TABLE IF EXISTS user;
CREATE TABLE user (
  id          BIGINT    NOT NULL
  COMMENT '用户id',
  head_img    VARCHAR(255)       DEFAULT NULL
  COMMENT '头像',
  nickname    VARCHAR(50)        DEFAULT NULL
  COMMENT '呢称',
  sex         TINYINT   NOT NULL DEFAULT 0
  COMMENT '0:男，1：女',
  introduce   VARCHAR(255)       DEFAULT NULL
  COMMENT '个人简介',
  create_time TIMESTAMP NOT NULL DEFAULT current_timestamp
  COMMENT '创建时间',
  PRIMARY KEY (id)
)
  ENGINE = InnoDB
  DEFAULT CHARSET = utf8
  COMMENT '用户表';

--  2018-08-18更新 --------
-- 修改movie的play_count可以为空
ALTER TABLE movie
  MODIFY COLUMN play_count BIGINT DEFAULT 0
  COMMENT '播放次数统计';
-- 增加文件任务表
DROP TABLE IF EXISTS task;
CREATE TABLE task (
  id          BIGINT        AUTO_INCREMENT
  COMMENT 'id',
  name        VARCHAR(200) NOT NULL
  COMMENT '任务名称',
  file_path   VARCHAR(200)
  COMMENT '文件路径',
  save_path   VARCHAR(200)
  COMMENT '保存文件名称/路径',
  size        BIGINT        DEFAULT 0
  COMMENT '文件大小',
  progress    BIGINT        DEFAULT 0
  COMMENT '进度（断点续传）',
  percent     DECIMAL(4, 2) DEFAULT 0
  COMMENT '百分比',
  status      TINYINT       DEFAULT 0
  COMMENT '状态',
  kind        TINYINT       DEFAULT 0
  COMMENT '类型: 0-上传 1-下载',
  user_id     BIGINT COMMENT '任务所属用户id',
  target_id   BIGINT COMMENT '任务对应的目标id',
  create_time TIMESTAMP     DEFAULT current_timestamp,
  PRIMARY KEY (id),
  KEY (name),
  KEY (target_id)
)
  ENGINE = InnoDB
  DEFAULT CHARSET = utf8
  COMMENT '文件任务表';

-- 2018-08-19更新---------
-- 增加视频表
DROP TABLE IF EXISTS video;
CREATE TABLE video (
  id           BIGINT    AUTO_INCREMENT
  COMMENT '视频id',
  name         VARCHAR(100) NOT NULL
  COMMENT '视频名称',
  code         VARCHAR(100)
  COMMENT '视频码/文件名',
  description  VARCHAR(100) COMMENT '描述',
  quality_id   TINYINT COMMENT '质量ID',
  quality_name VARCHAR(50) COMMENT '质量: 高清，标清...',
  status       TINYINT   DEFAULT -100
  COMMENT '状态：可用、不存在、侵权...',
  create_time  TIMESTAMP DEFAULT current_timestamp
  COMMENT '创建时间',
  user_id      BIGINT COMMENT '视频上传者id',
  PRIMARY KEY (id),
  KEY (name)
)
  ENGINE = InnoDB
  DEFAULT CHARSET = utf8
  COMMENT '视频表';
-- 增加质量表
DROP TABLE IF EXISTS quality;
CREATE TABLE quality (
  id   TINYINT AUTO_INCREMENT
  COMMENT '质量表id',
  name VARCHAR(50) COMMENT '名称',
  PRIMARY KEY (id)
)
  ENGINE = InnoDB
  DEFAULT CHARSET = utf8
  COMMENT '质量表';
-- 给表movie添加视频源字段
ALTER TABLE movie
  ADD COLUMN video_id BIGINT DEFAULT -1
COMMENT '视频id';

-- 2018-08-23更新---------
-- 增加文件表
DROP TABLE IF EXISTS file_info;
CREATE TABLE file_info (
  id   INT AUTO_INCREMENT
  COMMENT '文件ID',
  name VARCHAR(100)        NOT NULL
  COMMENT '文件名',
  md5  VARCHAR(100) UNIQUE NOT NULL
  COMMENT 'MD5的值',
  KEY (id),
  INDEX (md5)
)
  ENGINE = InnoDB
  DEFAULT CHARSET = utf8
  COMMENT '文件表';

-- 2018-08-27更新--
-- 增加video表的user_name冗余字段
ALTER TABLE video
  ADD COLUMN user_name VARCHAR(30) COMMENT '所属用户名';


-- 2018-08-28更新
ALTER TABLE file_info
  ADD COLUMN pointer INT DEFAULT 1 COMMENT '视频被指向计数';
