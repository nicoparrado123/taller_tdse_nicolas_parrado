# taller_tdse_nicolas_parrado

Introduction to Naming, Networks, Clients, and Services with Java

## Structure

```
src/main/java/
├── networking/
│   ├── URLExample.java       # Exercises 1 & 2: URL methods + mini-browser
│   ├── TCPServer.java        # TCP echo server
│   ├── TCPClient.java        # TCP echo client
│   ├── SquareServer.java     # Exercise 4.3.1: returns square of a number
│   ├── FunctionServer.java   # Exercise 4.3.2: sin/cos/tan server
│   ├── HttpServer.java       # Section 4.4: single-request HTTP server
│   ├── MultiHttpServer.java  # Exercise 4.5.1: multi-request HTTP server + web app
│   ├── UDPServer.java        # Basic UDP echo server
│   ├── UDPClient.java        # Basic UDP echo client
│   ├── UDPTimeServer.java    # Exercise 5.2.1: UDP time server
│   └── UDPTimeClient.java    # Exercise 5.2.1: polls time every 5s, tolerates downtime
└── rmi/
    ├── ChatPeer.java         # Remote interface
    └── ChatApp.java          # Exercise 6.4.1: RMI peer-to-peer chat
```

## Compile

```bash
javac -d out src/main/java/networking/*.java src/main/java/rmi/*.java
```

## Run

### URL Example
```bash
java -cp out networking.URLExample
```

### TCP
```bash
java -cp out networking.TCPServer
java -cp out networking.TCPClient
```

### UDP echo
```bash
java -cp out networking.UDPServer
java -cp out networking.UDPClient
```

### UDP Time (Exercise 5.2.1)
```bash
java -cp out networking.UDPTimeServer
java -cp out networking.UDPTimeClient
```

### RMI Chat (Exercise 6.4.1)
Open two terminals (can be on different machines):
```bash
# Terminal 1
java -cp out rmi.ChatApp
# Enter: username, local port (e.g. 23001), remote IP, remote port (e.g. 23002)

# Terminal 2
java -cp out rmi.ChatApp
# Enter: username, local port (e.g. 23002), remote IP, remote port (e.g. 23001)
```
