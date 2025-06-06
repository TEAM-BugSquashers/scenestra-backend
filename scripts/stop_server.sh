#!/bin/bash
# Stop Spring Boot Docker containers

echo "$(date): Starting server stop process" >> /home/ec2-user/deploy.log

# Stop container by name first
if sudo docker ps -a --format "table {{.Names}}" | grep -q "spring-boot-app"; then
    echo "$(date): Stopping spring-boot-app container" >> /home/ec2-user/deploy.log
    sudo docker stop spring-boot-app
    sudo docker rm spring-boot-app
    echo "$(date): spring-boot-app container stopped and removed" >> /home/ec2-user/deploy.log
fi

# Also stop any containers using port 8080
echo "$(date): Checking for containers using port 8080" >> /home/ec2-user/deploy.log
CONTAINERS_ON_8080=$(sudo docker ps --filter "publish=8080" -q)

if [ -n "$CONTAINERS_ON_8080" ]; then
    echo "$(date): Stopping containers using port 8080: $CONTAINERS_ON_8080" >> /home/ec2-user/deploy.log
    sudo docker stop $CONTAINERS_ON_8080
    sudo docker rm $CONTAINERS_ON_8080
fi

# Fallback: stop all running containers (as in your original script)
echo "$(date): 현재 실행 중인 Docker 컨테이너 pid 확인" >> /home/ec2-user/deploy.log
CURRENT_PID=$(sudo docker container ls -q)

if [ -z "$CURRENT_PID" ]
then
  echo "$(date): 현재 구동중인 Docker 컨테이너가 없으므로 종료하지 않습니다." >> /home/ec2-user/deploy.log
else
  echo "$(date): sudo docker stop $CURRENT_PID" >> /home/ec2-user/deploy.log
  sudo docker stop $CURRENT_PID
  sleep 5
fi

# Clean up
sudo docker container prune -f
sudo docker image prune -f

echo "$(date): Server stop process completed" >> /home/ec2-user/deploy.log