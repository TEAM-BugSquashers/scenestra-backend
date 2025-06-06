#!/bin/bash
# Start Spring Boot Docker application

echo "$(date): Starting server deployment" >> /home/ec2-user/deploy.log

# Change to deployment directory
cd /home/ec2-user/deploy

# Build Docker image
echo "$(date): Building Docker image..." >> /home/ec2-user/deploy.log
sudo docker build -t sc-spring-boot-docker . >> /home/ec2-user/deploy.log 2>&1

if [ $? -eq 0 ]; then
    echo "$(date): Docker image built successfully" >> /home/ec2-user/deploy.log
else
    echo "$(date): Failed to build Docker image" >> /home/ec2-user/deploy.log
    exit 1
fi

# Run Docker container
echo "$(date): Starting Docker container..." >> /home/ec2-user/deploy.log
CONTAINER_ID=$(sudo docker run -d -p 8080:8080 --name spring-boot-app sc-spring-boot-docker)

if [ $? -eq 0 ]; then
    echo "$(date): Docker container started successfully with ID: $CONTAINER_ID" >> /home/ec2-user/deploy.log
else
    echo "$(date): Failed to start Docker container" >> /home/ec2-user/deploy.log
    exit 1
fi

# Wait a moment for container to start
sleep 10

# Check if container is running
RUNNING_CONTAINER=$(sudo docker ps -q --filter "name=spring-boot-app")
if [ -n "$RUNNING_CONTAINER" ]; then
    echo "$(date): Spring Boot application is running successfully" >> /home/ec2-user/deploy.log

    # Optional: Check if application is responding
    # sleep 30
    # curl -f http://localhost:8080/actuator/health || echo "$(date): Health check failed, but container is running" >> /home/ec2-user/deploy.log
else
    echo "$(date): Container failed to start properly" >> /home/ec2-user/deploy.log
    # Show container logs for debugging
    sudo docker logs spring-boot-app >> /home/ec2-user/deploy.log 2>&1
    exit 1
fi

echo "$(date): Deployment completed successfully" >> /home/ec2-user/deploy.log