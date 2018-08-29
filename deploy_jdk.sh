#!/bin/bash

### 打包安装
cd $PROJ_PATH/mycloud/
mvn clean install package

### 运行
cd $PROJ_PATH/mycloud/mycloud-eureka/target
java -jar mycloud-eureka-0.0.1-SNAPSHOT.jar --spring.profiles.active=pro

cd $PROJ_PATH/mycloud/mycloud-static/target
java -jar mycloud-static-0.0.1-SNAPSHOT.jar --spring.profiles.active=pro

cd $PROJ_PATH/mycloud/mycloud-sso/mycloud-sso-server/target
java -jar mycloud-sso-server-0.0.1-SNAPSHOT.jar --spring.profiles.active=pro

cd $PROJ_PATH/mycloud/mycloud-movie/mycloud-movie-admin/target
java -jar mycloud-movie-admin-0.0.1-SNAPSHOT.jar --spring.profiles.active=pro

cd $PROJ_PATH/mycloud/mycloud-movie/mycloud-movie-client/target
java -jar mycloud-movie-client-0.0.1-SNAPSHOT.jar --spring.profiles.active=pro