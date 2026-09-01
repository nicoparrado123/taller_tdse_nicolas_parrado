package networking;

import java.net.*;
import java.io.*;

public class SquareServer {
    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(35001);
        System.out.println("SquareServer listening on port 35001...");
        Socket clientSocket = serverSocket.accept();

        PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
        BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

        String inputLine;
        while ((inputLine = in.readLine()) != null) {
            double number = Double.parseDouble(inputLine);
            out.println(number * number);
        }

        out.close();
        in.close();
        clientSocket.close();
        serverSocket.close();
    }
}
