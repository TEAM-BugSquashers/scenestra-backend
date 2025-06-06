#!/bin/bash
# Install Docker and dependencies

echo "$(date): Starting dependency installation" >> /home/ec2-user/deploy.log

# Update system packages
yum update -y

# Install Docker if not already installed
if ! command -v docker &> /dev/null; then
    echo "$(date): Installing Docker" >> /home/ec2-user/deploy.log
    yum install -y docker
    systemctl start docker
    systemctl enable docker

    # Add ec2-user to docker group
    usermod -aG docker ec2-user
else
    echo "$(date): Docker already installed" >> /home/ec2-user/deploy.log
    systemctl start docker
fi

# Create deployment directory
mkdir -p /home/ec2-user/deploy
chown -R ec2-user:ec2-user /home/ec2-user/deploy

# Create log file if not exists
touch /home/ec2-user/deploy.log
chown ec2-user:ec2-user /home/ec2-user/deploy.log

echo "$(date): Dependencies installation completed" >> /home/ec2-user/deploy.log