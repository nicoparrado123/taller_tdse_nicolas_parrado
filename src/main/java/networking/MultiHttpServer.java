package networking;

import java.net.*;
import java.io.*;
import java.nio.file.*;

public class MultiHttpServer {
    static final String WEB_ROOT = "www";

    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(35000);
        System.out.println("MultiHttpServer listening on port 35000...");

        while (true) {
            Socket clientSocket = serverSocket.accept();
            handleRequest(clientSocket);
        }
    }

    static void sendText(OutputStream out, String body) throws IOException {
        String headers = "HTTP/1.1 200 OK\r\nContent-Type: text/plain\r\nContent-Length: "
            + body.length() + "\r\n\r\n";
        out.write(headers.getBytes());
        out.write(body.getBytes());
    }

    static void handleRequest(Socket clientSocket) throws IOException {
        BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        OutputStream out = clientSocket.getOutputStream();

        String requestLine = in.readLine();
        if (requestLine == null) { clientSocket.close(); return; }
        System.out.println(requestLine);

        // Consume remaining headers
        while (in.ready()) in.readLine();

        // Parse requested path: GET /path HTTP/1.1
        String[] parts = requestLine.split(" ");
        String method = parts[0];
        String fullPath = parts.length > 1 ? parts[1] : "/";

        // Split path and query string
        String path = fullPath.contains("?") ? fullPath.substring(0, fullPath.indexOf('?')) : fullPath;
        String query = fullPath.contains("?") ? fullPath.substring(fullPath.indexOf('?') + 1) : "";

        String name = "";
        for (String param : query.split("&")) {
            if (param.startsWith("name=")) name = param.substring(5);
        }

        if (path.equals("/hello") && method.equals("GET")) {
            sendText(out, "Hello, " + name + "!");
        } else if (path.equals("/hellopost") && method.equals("POST")) {
            sendText(out, "Hello, " + name + " (POST)!");
        } else {
            if (path.equals("/")) path = "/index.html";
            File file = new File(WEB_ROOT + path);
            if (file.exists() && !file.isDirectory()) {
                byte[] body = Files.readAllBytes(file.toPath());
                String contentType = URLConnection.guessContentTypeFromName(file.getName());
                if (contentType == null) contentType = "application/octet-stream";
                String headers = "HTTP/1.1 200 OK\r\nContent-Type: " + contentType
                    + "\r\nContent-Length: " + body.length + "\r\n\r\n";
                out.write(headers.getBytes());
                out.write(body);
            } else {
                String body = "<html><body>404 Not Found</body></html>";
                String headers = "HTTP/1.1 404 Not Found\r\nContent-Type: text/html\r\nContent-Length: "
                    + body.length() + "\r\n\r\n";
                out.write(headers.getBytes());
                out.write(body.getBytes());
            }
        }

        out.flush();
        in.close();
        out.close();
        clientSocket.close();
    }
}
