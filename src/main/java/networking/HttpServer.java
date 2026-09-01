package networking;

import java.net.*;
import java.io.*;

public class HttpServer {
    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(35000);
        System.out.println("Ready to receive...");
        Socket clientSocket = serverSocket.accept();

        PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
        BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

        String inputLine;
        while ((inputLine = in.readLine()) != null) {
            System.out.println("Received: " + inputLine);
            if (!in.ready()) break;
        }

        String output = "HTTP/1.1 200 OK\r\n"
            + "Content-Type: text/html\r\n\r\n"
            + "<!doctype html><html><head>"
            + "<meta charset=\"UTF-8\">"
            + "<title>My Web Site</title></head>"
            + "<body>My Web Site</body></html>";
        out.println(output);

        out.close();
        in.close();
        clientSocket.close();
        serverSocket.close();
    }
}
