# ChatFlow server deployment instructions

## Table of Contents

1. [EC2 Instance Setup](#ec2-instance-setup)
2. [Server Environment Configuration](#server-environment-configuration)
3. [Application Deployment](#application-deployment)
---

## EC2 Instance Setup

### 1. Launch EC2 Instance

1. Log in to AWS Console and navigate to EC2
2. Click "Launch Instance"
3. Configure the instance:
    - **Name**: `chatflow-server`
    - **AMI**: Amazon Linux 2023 or Ubuntu 22.04 LTS (free tier eligible)
    - **Instance Type**: `t3.micro` (free tier eligible)
    - **Key pair**: Select existing or create new key pair
    - **Network settings**:
        - Allow SSH traffic from your IP
        - Allow HTTP traffic (port 80)
        - Allow HTTPS traffic (port 443)
        - Add custom TCP rule for port 8080 (WebSocket server)

4. Configure Security Group with the following inbound rules:
```
Type            Protocol    Port Range    Source
SSH             TCP         22            Your IP/0.0.0.0/0
HTTP            TCP         80            0.0.0.0/0
HTTPS           TCP         443           0.0.0.0/0
Custom TCP      TCP         8080          0.0.0.0/0
```

5. Configure storage: 8 GB (default free tier)
6. Click "Launch Instance"

### 2. Connect to EC2 Instance
```bash
# Make key file read-only
chmod 400 your-key-pair.pem
# Connect via SSH
ssh -i your-key-pair.pem ec2-user@<EC2_PUBLIC_IP>
# OR for Ubuntu
ssh -i your-key-pair.pem ubuntu@<EC2_PUBLIC_IP>
```
---

## Server Environment Configuration
### 1. Update System Packages
**For Amazon Linux 2023:**
```bash
sudo yum update -y
```

**For Ubuntu:**
```bash
sudo apt update
sudo apt upgrade -y
```

### 2. Install Java 17
**For Amazon Linux 2023:**
```bash
sudo yum install java-17-amazon-corretto-devel -y
```
**For Ubuntu:**
```bash
sudo apt install openjdk-17-jdk -y
```

Verify installation:
```bash
java -version
javac -version
```

### 3. Install Maven (if building from source)
**For Amazon Linux 2023:**
```bash
sudo yum install maven -y
```

**For Ubuntu:**
```bash
sudo apt install maven -y
```

Verify installation:
```bash
mvn -version
```

---

## Application Deployment

### Method 1: Deploy Pre-built JAR

1. **Transfer JAR file to EC2**:
```bash
# From your local machine
scp -i your-key-pair.pem target/websocket-chat-server.jar ec2-user@<EC2_PUBLIC_IP>:~/
```

2. **Create application directory on EC2**:
```bash
sudo mkdir -p /opt/websocket-server
sudo mv ~/websocket-chat-server.jar /opt/websocket-server/
sudo chown -R ec2-user:ec2-user /opt/websocket-server
```

### Method 2: Build from Source on EC2

1. **Clone or transfer source code**:
```bash
# Create app directory
mkdir -p ~/websocket-server
cd ~/websocket-server

# Upload source files via SCP
# From local machine:
scp -i your-key-pair.pem -r /path/to/project/* ec2-user@<EC2_PUBLIC_IP>:~/websocket-server/
```

2. **Build the application**:
```bash
cd ~/websocket-server
mvn clean package

# Move JAR to deployment directory
sudo mkdir -p /opt/websocket-server
sudo cp target/websocket-chat-server*.jar /opt/websocket-server/websocket-chat-server.jar
sudo chown -R ec2-user:ec2-user /opt/websocket-server
```

---

Chatflow server is deployed!