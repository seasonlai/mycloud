#!/bin/bash

### 基础jar包
cd $PROJ_PATH/mycloud/mycloud-common/mycloud-common-core
mvn clean install package
cd $PROJ_PATH/mycloud/mycloud-common/mycloud-common-web
mvn clean install package
cd $PROJ_PATH/mycloud/mycloud-sso/mycloud-sso-client
mvn clean install package

### 服务发现者
cd $PROJ_PATH/mycloud/mycloud-eureka
mvn clean install package docker:build

### 静态资源
cd $PROJ_PATH/mycloud/mycloud-static
mvn clean install package docker:build

### 单点服务端
cd $PROJ_PATH/mycloud/mycloud-sso/mycloud-sso-server
mvn clean install package docker:build

### 电影dao
cd $PROJ_PATH/mycloud/mycloud-movie/mycloud-movie-dao
mvn clean install package
### 电影service
cd $PROJ_PATH/mycloud/mycloud-movie/mycloud-movie-service
mvn clean install package
### 电影客户端
cd $PROJ_PATH/mycloud/mycloud-movie/mycloud-movie-client
mvn clean install package docker:build
### 电影服务端
cd $PROJ_PATH/mycloud/mycloud-movie/mycloud-movie-admin
mvn clean install package docker:build

### 开始启动
cd $PROJ_PATH/mycloud/docker
docker-compose up