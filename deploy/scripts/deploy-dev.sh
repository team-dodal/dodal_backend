#!/bin/bash

SERVICE_PATH="/home/ec2-user/app/deploy"

echo ">>> 모든 Docker 컨테이너를 정지하고 삭제합니다." >> $SERVICE_PATH/deploy.log
sudo docker stop $(sudo docker ps -qa)
sudo docker rm $(sudo docker ps -qa)

echo ">>> 모든 Docker 이미지를 삭제합니다." >> $SERVICE_PATH/deploy.log
sudo docker image prune -a -f

echo ">>> Redis 이미지를 가져옵니다." >> $SERVICE_PATH/deploy.log
sudo docker pull redis:alpine

echo ">>> Redis 컨테이너를 실행합니다." >> $SERVICE_PATH/deploy.log
sudo docker run --name redis -d -p 6379:6379 redis:alpine

BUILD_JAR=$(ls $SERVICE_PATH/*.jar)
JAR_NAME=$(basename $BUILD_JAR)

echo ">>> build 파일명: $JAR_NAME" >> $SERVICE_PATH/deploy.log

echo ">>> build 파일 복사" >> $SERVICE_PATH/deploy.log
DEPLOY_PATH=$SERVICE_PATH/
cp $BUILD_JAR $DEPLOY_PATH

echo ">>> 현재 실행중인 애플리케이션 pid 확인" >> $SERVICE_PATH/deploy.log
CURRENT_PID=$(pgrep -f $JAR_NAME)

if [ -z $CURRENT_PID ]
then
  echo ">>> 현재 구동중인 애플리케이션이 없으므로 종료하지 않습니다." >> $SERVICE_PATH/deploy.log
else
  echo ">>> kill -15 $CURRENT_PID"
  kill -15 $CURRENT_PID
  sleep 5
fi

DEPLOY_JAR=$DEPLOY_PATH$JAR_NAME
echo ">>> DEPLOY_JAR 배포"    >> $SERVICE_PATH/deploy.log
nohup java -jar -Dspring.profiles.active=dev $DEPLOY_JAR >> /home/ec2-user/dodal-app.log 2> $SERVICE_PATH/dodal-app.log &