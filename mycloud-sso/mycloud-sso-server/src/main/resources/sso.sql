DROP DATABASE IF EXISTS mycloud_sso;
CREATE DATABASE mycloud_sso;
USE mycloud_sso;

DROP TABLE IF EXISTS app;
CREATE TABLE app (
  app_id      INT                NOT NULL AUTO_INCREMENT
  COMMENT '应用id',
  app_name    VARCHAR(50) UNIQUE NOT NULL
  COMMENT '应用名',
  app_code    VARCHAR(50)        NOT NULL
  COMMENT '应用码',
  create_time TIMESTAMP          NOT NULL DEFAULT current_timestamp
  COMMENT '创建/注册时间',
  sort        INT                NOT NULL DEFAULT 1
  COMMENT '排序号',
  enable      TINYINT(1)         NOT NULL DEFAULT 1
  COMMENT '是否启用 0-不启用  1-启用',
  PRIMARY KEY (app_id)
)
  ENGINE = InnoDB
  DEFAULT CHARSET = utf8;

DROP TABLE IF EXISTS user;
CREATE TABLE user (
  `user_id`    BIGINT             NOT NULL AUTO_INCREMENT
  COMMENT '用户Id',
  `user_name`  VARCHAR(30) UNIQUE NOT NULL
  COMMENT '用户名',
  `password`   VARCHAR(50)        NOT NULL DEFAULT ''
  COMMENT '密码',
  `user_type`  TINYINT(1)         NOT NULL DEFAULT 0
  COMMENT '0:普通用户',
  `locked`     TINYINT(1)         NOT NULL DEFAULT 0
  COMMENT '0:未锁定 1:锁定',
  `last_visit` DATETIME                    DEFAULT NULL
  COMMENT '最后登陆时间',
  `last_ip`    VARCHAR(20)                 DEFAULT NULL
  COMMENT '最后登陆IP',
  create_time  TIMESTAMP          NOT NULL DEFAULT current_timestamp
  COMMENT '创建时间',
  PRIMARY KEY (`user_id`),
  KEY `IDX_USER_USER_NAME` (`user_name`)
)
  ENGINE = InnoDB
  AUTO_INCREMENT = 4
  DEFAULT CHARSET = utf8;

DROP TABLE IF EXISTS role;
CREATE TABLE role (
  role_id     INT         NOT NULL AUTO_INCREMENT
  COMMENT '角色ID',
  role_name   VARCHAR(30) NOT NULL
  COMMENT '角色名',
  sort        INT         NOT NULL DEFAULT 0
  COMMENT '排序号',
  enable      TINYINT(1)  NOT NULL DEFAULT 1
  COMMENT '是否启用: 0-不启用  1-启用',
  description VARCHAR(100) COMMENT '角色描述',
  create_time TIMESTAMP   NOT NULL DEFAULT current_timestamp
  COMMENT '创建时间',
  role_app_id INT         NOT NULL
  COMMENT '所属应用',
  PRIMARY KEY (role_id),
  FOREIGN KEY (role_app_id) REFERENCES app (app_id)
    ON DELETE CASCADE
    ON UPDATE CASCADE,
  KEY `IDX_ROLE_ROLE_NAME` (role_name)
)
  ENGINE = InnoDB
  DEFAULT CHARSET = utf8;

DROP TABLE IF EXISTS permission;
CREATE TABLE permission (
  permission_id     INT         NOT NULL AUTO_INCREMENT
  COMMENT '权限',
  parent_id         INT                  DEFAULT 0
  COMMENT '父ID',
  permission_name   VARCHAR(30) NOT NULL
  COMMENT '权限名称',
  menu              TINYINT(1)  NOT NULL DEFAULT 0
  COMMENT '是否菜单: 0-否  1-是',
  permission_url    VARCHAR(100)         DEFAULT NULL
  COMMENT '权限的url',
  permission_need   VARCHAR(50)          DEFAULT 'anon'
  COMMENT '所需权限',
  enable            TINYINT(1)  NOT NULL DEFAULT 1
  COMMENT '是否启用: 0-不启用  1-启用',
  sort              INT         NOT NULL DEFAULT 0
  COMMENT '排序号',
  description       VARCHAR(100)         DEFAULT NULL
  COMMENT '权限描述',
  create_time       TIMESTAMP   NOT NULL DEFAULT current_timestamp
  COMMENT '创建时间',
  permission_app_id INT         NOT NULL
  COMMENT '所属应用',
  PRIMARY KEY (permission_id),
  FOREIGN KEY (permission_app_id) REFERENCES app (app_id)
    ON DELETE CASCADE
    ON UPDATE CASCADE,
  KEY `IDX_PRIVILEGE_PRIVILEGE_NAME` (permission_name)
)
  ENGINE = InnoDB
  DEFAULT CHARSET = utf8
  COMMENT '权限表';

DROP TABLE IF EXISTS role_permissions;
CREATE TABLE role_permissions
(
  role_id       INT NOT NULL,
  permission_id INT NOT NULL,
  PRIMARY KEY (role_id, permission_id),
  CONSTRAINT FK_ROLE_PERMISSIONS_ROLE
  FOREIGN KEY (role_id) REFERENCES mycloud_sso.role (role_id),
  CONSTRAINT FK_ROLE_PERMISSIONS_PERMISSION
  FOREIGN KEY (permission_id) REFERENCES mycloud_sso.permission (permission_id)
);

DROP TABLE IF EXISTS user_roles;
CREATE TABLE user_roles
(
  user_id BIGINT NOT NULL,
  role_id INT    NOT NULL,
  PRIMARY KEY (user_id, role_id),
  CONSTRAINT FK_USER_ROLES_USER
  FOREIGN KEY (user_id) REFERENCES mycloud_sso.user (user_id),
  CONSTRAINT FK_USER_ROLES_ROLE
  FOREIGN KEY (role_id) REFERENCES mycloud_sso.role (role_id)
);

# DROP TABLE IF EXISTS app_roles;
# CREATE TABLE app_roles
# (
#   app_id  INT NOT NULL,
#   role_id INT NOT NULL,
#   PRIMARY KEY (app_id, role_id),
#   CONSTRAINT FK_APP_ROLES_APP
#   FOREIGN KEY (app_id) REFERENCES mycloud_sso.app (app_id),
#   CONSTRAINT FK_APP_ROLES_ROLE
#   FOREIGN KEY (role_id) REFERENCES mycloud_sso.role (role_id)
# );

INSERT app (app_name, app_code) VALUES ('单点登录系统', 'mycloud-sso');
INSERT user (user_id, user_name, password, user_type) VALUES (1, 'root', 'e10adc3949ba59abbe56e057f20f883e', 1);
INSERT user (user_name, password, user_type) VALUES ('test', 'e10adc3949ba59abbe56e057f20f883e', 1);
INSERT role (role_name, sort, description, role_app_id) VALUES ('管理员', 1, '单点登录系统管理员', 1);
INSERT permission (permission_name, description, permission_app_id) VALUES ('系统权限', '单点服务器权限', 1);
INSERT permission (permission_name, description, permission_app_id) VALUES ('通用权限', '不需认证的权限', 1);
INSERT permission (permission_name, description, permission_app_id) VALUES ('调试权限', '开发时调试使用', 1);
INSERT permission (permission_name, permission_url, permission_need, permission_app_id, parent_id)
VALUES ('后台管理', '/admin/**', 'perms[sso:admin]', 1, 1);
INSERT permission (permission_name, permission_url, permission_need, permission_app_id, parent_id)
VALUES ('验证码', '/captcha', 'captcha', 1, 2);
INSERT permission (permission_name, permission_url, permission_need, permission_app_id, parent_id)
VALUES ('登出', '/sso/logout', 'logout', 1, 2);
INSERT permission (permission_name, permission_url, permission_need, permission_app_id, parent_id)
VALUES ('错误跳转', '/sso/error', 'anon', 1, 2);
INSERT permission (permission_name, permission_url, permission_need, permission_app_id, parent_id)
VALUES ('浏览器图标', '/favicon.ico', 'anon', 1, 2);
INSERT permission (permission_name, permission_url, permission_need, permission_app_id, parent_id)
VALUES ('用户注册', '/register/**', 'anon', 1, 2);
INSERT permission (permission_name, permission_url, permission_need, permission_app_id, parent_id)
VALUES ('swagger-ui', '/swagger-ui.html', 'anon', 1, 3);
INSERT permission (permission_name, permission_url, permission_need, permission_app_id, parent_id)
VALUES ('swagger-resources', '/swagger-resources/**', 'anon', 1, 3);
INSERT permission (permission_name, permission_url, permission_need, permission_app_id, parent_id)
VALUES ('swagger-api', '/v2/api-docs/**', 'anon', 1, 3);
INSERT permission (permission_name, permission_url, permission_need, permission_app_id, parent_id)
VALUES ('swagger-springfox', '/webjars/springfox-swagger-ui/**', 'anon', 1, 3);
INSERT permission (permission_name, permission_url, permission_need, permission_app_id, parent_id)
VALUES ('druid控制台', '/druid/**', 'anon', 1, 3);
INSERT user_roles VALUES (1, 1);
INSERT role_permissions VALUES (1, 4);
