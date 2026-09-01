# taller_tdse_nicolas_parrado

Introduction to Naming, Networks, Clients, and Services with Java

## Structure

```
src/main/java/networking/
├── URLExample.java     # URL and URLConnection usage
├── TCPServer.java      # TCP Server using ServerSocket
├── TCPClient.java      # TCP Client using Socket
├── UDPServer.java      # UDP Server using DatagramSocket
└── UDPClient.java      # UDP Client using DatagramSocket
```

## Compile

```bash
javac -d out src/main/java/networking/*.java
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

### UDP 
```bash
java -cp out networking.UDPServer
java -cp out networking.UDPClient
```
