#!/bin/bash

### 打包安装
cd $PROJ_PATH/mycloud/
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

### 电影客户端
cd $PROJ_PATH/mycloud/mycloud-movie/mycloud-movie-client
mvn clean install package docker:build

### 电影服务端
cd $PROJ_PATH/mycloud/mycloud-movie/mycloud-movie-admin
mvn clean install package docker:build

### 开始启动
cd $PROJ_PATH/mycloud/docker
docker-compose up