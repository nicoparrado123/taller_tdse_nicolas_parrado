package networking;

import java.net.*;
import java.io.*;
import java.util.*;

public class URLExample {

    // Exercise 1: print the 8 URL methods
    static void exercise1() throws Exception {
        URL url = new URL("http://ldbn.escuelaing.edu.co:80/index.html?q=test#section");
        System.out.println("Protocol : " + url.getProtocol());
        System.out.println("Authority: " + url.getAuthority());
        System.out.println("Host     : " + url.getHost());
        System.out.println("Port     : " + url.getPort());
        System.out.println("Path     : " + url.getPath());
        System.out.println("Query    : " + url.getQuery());
        System.out.println("File     : " + url.getFile());
        System.out.println("Ref      : " + url.getRef());
    }

    // Exercise 2: mini-browser — asks for a URL, saves response to result.html
    static void exercise2() throws Exception {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter URL: ");
        String input = scanner.nextLine().trim();

        URL siteURL = new URL(input);
        URLConnection connection = siteURL.openConnection();

        // Print response headers
        Map<String, List<String>> headers = connection.getHeaderFields();
        for (Map.Entry<String, List<String>> entry : headers.entrySet()) {
            if (entry.getKey() != null) System.out.print(entry.getKey() + ": ");
            for (String value : entry.getValue()) System.out.print(value);
            System.out.println();
        }

        // Save body to result.html
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
             PrintWriter writer = new PrintWriter(new FileWriter("result.html"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                writer.println(line);
            }
        }
        System.out.println("Saved to result.html");
    }

    public static void main(String[] args) throws Exception {
        System.out.println("=== Exercise 1 ===");
        exercise1();
        System.out.println("\n=== Exercise 2 ===");
        exercise2();
    }
}
