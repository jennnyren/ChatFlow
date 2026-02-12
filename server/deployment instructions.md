## AWS EC2 Deployment

### 1. Launch EC2 Instance

#### Instance Configuration
- **AMI**: Amazon Linux 2023 or Ubuntu 22.04
- **Instance Type**: t2.micro (free tier eligible)
- **Region**: us-west-2 (Oregon)
- **Storage**: 8 GB (default)

#### Security Group Rules
| Type | Protocol | Port Range | Source |
|------|----------|------------|--------|
| SSH | TCP | 22 | Your IP |
| HTTP | TCP | 8080 | 0.0.0.0/0 |
| Custom TCP | TCP | 8080 | 0.0.0.0/0 |

### 2. Connect to EC2
```bash
# Download your key pair (.pem file)
chmod 400 your-key.pem

# Connect via SSH
ssh -i your-key.pem ec2-user@<EC2-PUBLIC-IP>
```

### 3. Install Java and Maven on EC2
```bash
# Update system
sudo yum update -y  # Amazon Linux
# OR
sudo apt update && sudo apt upgrade -y  # Ubuntu

# Install Java 11
sudo yum install -y java-11-amazon-corretto  # Amazon Linux
# OR
sudo apt install -y openjdk-11-jdk  # Ubuntu

# Install Maven
sudo yum install -y maven  # Amazon Linux
# OR
sudo apt install -y maven  # Ubuntu

# Verify
java -version
mvn -version
```

### 4. Deploy Server to EC2
```bash
# On your local machine, build the JAR
cd websocket-chat-server
mvn clean package

# Copy JAR to EC2
scp -i your-key.pem target/websocket-chat-server-1.0.0.jar \
    ec2-user@<EC2-PUBLIC-IP>:~/

# SSH into EC2
ssh -i your-key.pem ec2-user@<EC2-PUBLIC-IP>

# Run the server
java -jar websocket-chat-server-1.0.0.jar
```

### 5. Run as Background Service
```bash
# Create systemd service
sudo nano /etc/systemd/system/chatserver.service
```

Add content:
```ini
[Unit]
Description=WebSocket Chat Server
After=network.target

[Service]
Type=simple
User=ec2-user
WorkingDirectory=/home/ec2-user
ExecStart=/usr/bin/java -jar /home/ec2-user/websocket-chat-server-1.0.0.jar
Restart=on-failure
RestartSec=10

[Install]
WantedBy=multi-user.target
```

Enable and start:
```bash
sudo systemctl daemon-reload
sudo systemctl enable chatserver
sudo systemctl start chatserver
sudo systemctl status chatserver
```

### 6. Test Deployment
```bash
# From your local machine
curl http://<EC2-PUBLIC-IP>:8080/health

# WebSocket test
wscat -c ws://<EC2-PUBLIC-IP>:8080/chat/1
```

### 7. Update Client Configuration
```java
// In ChatClient.java
private static final String SERVER_URL = "ws://<EC2-PUBLIC-IP>:8080";
```

Rebuild and run client:
```bash
mvn clean package
java -jar target/websocket-chat-client-1.0.0.jar
```

---