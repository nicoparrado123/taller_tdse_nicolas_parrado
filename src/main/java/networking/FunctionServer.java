package networking;

import java.net.*;
import java.io.*;

public class FunctionServer {
    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(35002);
        System.out.println("FunctionServer listening on port 35002...");
        Socket clientSocket = serverSocket.accept();

        PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
        BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

        String function = "cos";
        String inputLine;
        while ((inputLine = in.readLine()) != null) {
            if (inputLine.startsWith("fun:")) {
                function = inputLine.substring(4).trim();
                out.println("Function set to: " + function);
            } else {
                double number = Double.parseDouble(inputLine);
                double result = switch (function) {
                    case "sin" -> Math.sin(number);
                    case "tan" -> Math.tan(number);
                    default   -> Math.cos(number);
                };
                out.println(result);
            }
        }

        out.close();
        in.close();
        clientSocket.close();
        serverSocket.close();
    }
}
