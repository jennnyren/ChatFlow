# ChatFlow - Complete Implementation Guide

## Table of Contents
1. [Overview](#overview)
2. [Prerequisites](#prerequisites)
3. [Part 1: Server Setup](#part-1-websocket-server-setup)
4. [Part 2: Client Setup](#part-2-multithreaded-client-setup)
5. [Part 3: Performance Analysis](#part-3-performance-analysis)
6. [Testing Instructions](#testing-instructions)
---
## Overview
This project implements a high-performance WebSocket chat system with:
- **Server**: WebSocket server with validation and message handling
- **Client**: Multithreaded Java client simulating sending messages with performance metrics
- **Analytics**: Statistical analysis, CSV export, and throughput visualization
---
## Prerequisites
### Required software
- **Java JDK 11+**
- **Maven 3.6+**
### Optional testing tools
- **wscat**
- **Postman**
- **cURL**: Usually pre-installed on Linux/Mac
---
## Part 1: WebSocket server setup
### Implementation steps
#### 1. Clone the project
```bash
git clone <repository-url>
cd server
```
#### 2. Open the project and run the server
**Using IntelliJ IDEA:**
- Open project (File → Open → select project folder)
- Click the green **Run** button or run `ChatServerMain.java`

(Please refer to external help if using other editors.)

Expected output:
```
Websocket server started at port 8080
Websocket server started successfully on port 8080
SLF4J: No SLF4J providers were found.
SLF4J: Defaulting to no-operation (NOP) logger implementation
SLF4J: See https://www.slf4j.org/codes.html#noProviders for further details.
Http server started on port 8081
Chat server is running.
WebSocket: ws://localhost:8080/chat/{roomId
Health: http://localhost:8081/health
```

Server is now running on `http://localhost:8080`

---

## Part 2: Multithreaded Client Setup
### Implementation steps
#### 1. Clone the project
```bash
git clone <repository-url>
cd client-part1
```
#### 2. Open the project and run the client
**Using IntelliJ IDEA:**
- Open project (File → Open → select project folder)
- Click the green **Run** button or run `LoadTestClient.java`
- Make sure server is already running

Expected output:

**Warmup Phase:**
![part1_output (warmup_phase).png](results/part1_output%20%28warmup_phase%29.png)

**Main Phase:**
![part1_output (main_phase).png](results/part1_output%20%28main_phase%29.png)

---

## Part 3: Performance Analysis
### Implementation steps
#### 1. Clone the project
```bash
git clone <repository-url>
cd client-part2
```
#### 2. Open the project and run the client
**Using IntelliJ IDEA:**
- Open project (File → Open → select project folder)
- Click the green **Run** button or run `LoadTestClient[2].java`
- Make sure server is already running

Expected output:

**Warmup Phase:**
![part2_output (warmup_phase).png](results/part2_output%20%28warmup_phase%29.png)

**Main Phase:**
![part2_output (main_phase).png](results/part2_output%20%28main_phase%29.png)

---

## Testing Instructions

### 1. Test Health Endpoint
```bash
curl http://localhost:8080/health
```

Expected response:
```json
{
  "status": "UP",
  "timestamp": "2026-02-11T14:30:22.123Z",
  "service": "WebSocket Chat Server",
  "version": "1.0.0"
}
```

### 2. Test WebSocket with wscat
```bash
# Install wscat
npm install -g wscat

# Connect to room 1
wscat -c ws://localhost:8080/chat/1

# Send a valid message
> {"userId":12345,"username":"user12345","message":"Hello!","timestamp":"2026-02-11T14:30:22.123Z","messageType":"TEXT"}

# Expected response:
< {"status":"SUCCESS","serverTimestamp":"2026-02-11T14:30:22.456Z","originalMessage":{...},"errorMessage":null}
```

### 3. Test Validation Errors
```bash
wscat -c ws://localhost:8080/chat/1

# Invalid userId (too large)
> {"userId":200000,"username":"test","message":"Hi","timestamp":"2026-02-11T14:30:22.123Z","messageType":"TEXT"}
< {"status":"ERROR","errorMessage":"Validation failed: userId must be between 1 and 100000"}

# Invalid username (too short)
> {"userId":123,"username":"ab","message":"Hi","timestamp":"2026-02-11T14:30:22.123Z","messageType":"TEXT"}
< {"status":"ERROR","errorMessage":"Validation failed: username must be 3-20 characters"}

# Invalid message (too long - over 500 chars)
> {"userId":123,"username":"testuser","message":"x".repeat(501),"timestamp":"2026-02-11T14:30:22.123Z","messageType":"TEXT"}
< {"status":"ERROR","errorMessage":"Validation failed: message must be 1-500 characters"}
```

### 4. Test with Postman
1. Open Postman
2. Click "New" → "WebSocket Request"
3. Enter URL: `ws://localhost:8080/chat/1`
4. Click "Connect"
5. Send JSON message in the message box
6. View responses in the response panel

### 5. Browser-Based Testing
Create `test.html`:
```html
<!DOCTYPE html>
<html>
<head><title>WebSocket Test</title></head>
<body>
    <h1>WebSocket Chat Test</h1>
    <button onclick="connect()">Connect</button>
    <button onclick="sendMessage()">Send Message</button>
    <button onclick="disconnect()">Disconnect</button>
    <pre id="output"></pre>

    <script>
        let ws;
        
        function connect() {
            ws = new WebSocket('ws://localhost:8080/chat/1');
            ws.onmessage = (event) => {
                document.getElementById('output').textContent += 
                    '\nReceived: ' + event.data;
            };
            ws.onopen = () => log('Connected');
            ws.onerror = (error) => log('Error: ' + error);
            ws.onclose = () => log('Disconnected');
        }
        
        function sendMessage() {
            const msg = {
                userId: Math.floor(Math.random() * 100000) + 1,
                username: 'user' + Math.floor(Math.random() * 1000),
                message: 'Hello from browser!',
                timestamp: new Date().toISOString(),
                messageType: 'TEXT'
            };
            ws.send(JSON.stringify(msg));
            log('Sent: ' + JSON.stringify(msg));
        }
        
        function disconnect() {
            ws.close();
        }
        
        function log(msg) {
            document.getElementById('output').textContent += '\n' + msg;
        }
    </script>
</body>
</html>
```

Open in browser: `file:///path/to/test.html`

---

## Additional Resources

### Documentation
- [Java-WebSocket Library](https://github.com/TooTallNate/Java-WebSocket)
- [Apache Commons Math](https://commons.apache.org/proper/commons-math/userguide/stat.html)

### Testing Tools
- [wscat GitHub](https://github.com/websockets/wscat)
- [Postman WebSocket](https://learning.postman.com/docs/sending-requests/websocket/websocket/)

### AWS Resources
- [EC2 Free Tier](https://aws.amazon.com/free/)
- [EC2 User Guide](https://docs.aws.amazon.com/AWSEC2/latest/UserGuide/)

---

**Last Updated**: February 11, 2026
**Version**: 1.0.0